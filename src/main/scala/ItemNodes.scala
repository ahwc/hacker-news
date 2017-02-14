import java.net.URI

import scala.xml.{Node, NodeSeq}
import Helpers._
import cats.Show
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.instances.int._
import cats.instances.string._
import cats.syntax.option._
import cats.syntax.cartesian._
import cats.syntax.show._
import play.api.libs.json._

import scala.util.Try

/**
  * Represents the [[Node Nodes]] for each item in the items list
  * @param thingNode The HTML node with class 'athing' - contains the title, author, link and rank
  * @param subtext The HTML node with class 'subtext' - contains the number of points and comments
  */
case class ItemNodes(thingNode: Node, subtext: Node) {

  private val commentsRegex = "(\\d+).*comments".r

  private val storyLink: ValidatedNel[String, Node] =
    (thingNode \\ "a").find(attributeValueEquals("storylink"))
      .toValidNel("Could not find story link node")

  /**
    * Make sure we can parse the string as an [[Int]] and that it is positive
    */
  private def validatePositiveInt(s: String): ValidatedNel[String,Int] = {
    Try(s.toInt).toOption.toValidNel(s"Could not parse points: $s")
      .andThen{ i ⇒
        if(i >= 0){
          Validated.Valid(i)
        } else {
          Validated.Invalid(NonEmptyList[String](s"Number of points was negative: $i", List.empty[String]))
        }
      }
  }

  /**
    * Make sure the given string is non empty and not more than 256 characters
    */
  private def validateString(s: String): ValidatedNel[String,String] = {
    if(s.isEmpty){
      Validated.Invalid(NonEmptyList[String]("Found empty string", List.empty[String]))
    } else if(s.length > 256) {
      Validated.Invalid(NonEmptyList[String]("Length of string was > 256", List.empty[String]))
    } else {
      Validated.Valid(s)
    }
  }

  val title: ValidatedNel[String,String] =
    storyLink.map(_.text).andThen(validateString)

  val author: ValidatedNel[String,String] =
    (subtext \ "a").find(n ⇒ className(n) == "hnuser").map(_.text)
      .toValidNel("Could not find author")
      .andThen(validateString)



  val link: ValidatedNel[String,URI] =
    storyLink.andThen{ storyLinkNode ⇒
      val l = (storyLinkNode \ "@href").toString
      Try(URI.create(l)).toOption.toValidNel(s"Could not read link: $l")
    }

  val points: ValidatedNel[String,Int] = {
    val pointsString = (subtext \ "span").find(n ⇒ className(n) == "score")
      .map(_.text.stripSuffix(" points"))
      .toValidNel("Could not find the number of points")
    pointsString.andThen(validatePositiveInt)
  }

  val comments: ValidatedNel[String,Int] = {
    // find text which matches our regex to get the number of comments
    val numbers: Option[ValidatedNel[String, Int]] = (subtext \ "a").map { s ⇒
      s.text match {
        case commentsRegex(number) ⇒
          Some(validatePositiveInt(number))
        case _ ⇒
          None
      }
    }.find(_.isDefined).flatten

    // if numbers is not defined then there was no text to say
    // how many comments there were - this seems to mean there are
    // no comments
    numbers.getOrElse(Validated.Valid(0))
  }


  val rank: ValidatedNel[String, Int] = {
    (thingNode \\ "span").find(n ⇒ className(n) == "rank")
      .toValidNel("Could not find rank")
      .andThen{ node ⇒
      val regex = "(\\d+).*".r
      val rank = node.text match {
        case regex(i) ⇒ Some(i)
        case _ ⇒ None
      }
        rank.toValidNel("Could not find rank")
    }.andThen(validatePositiveInt)

  }

}

object ItemNodes {

  // show required for the itemsShows below
  implicit def uriShow: Show[URI] = Show.fromToString[URI]

  /**
    * Pretty print [[Show]] for [[Item]]
    */
  implicit def itemShows: Show[ItemNodes] = new Show[ItemNodes]{
    override def show(f: ItemNodes): String = {
      s"title: ${f.title.show}, link: ${f.link.show}, author: ${f.author.show}, " +
        s"points: ${f.points.show}, comments: ${f.comments.show}, rank: ${f.rank.show}"
    }
  }

  /**
    * Read a list of [[ItemNodes]] from the given [[Node]] representation of some HTML
    */
  def fromHTML(content: Node): ValidatedNel[String,List[ItemNodes]] = {
    // get the table rows with the items we are interested in
    val table = content \ "body" \ "center" \ "table"
    val rows = table \ "tr"
    val itemList: ValidatedNel[String, Node] =
      rows.find(r ⇒ (r \ "td" \ "table" \ "@class").toString == "itemlist")
        .toValidNel("Could not find item list")

    itemList.map{ list ⇒
      // find the nodes with class 'athing' and 'subtext;
      val trs: NodeSeq = list \ "td" \ "table" \ "tr"
      val tds = trs \\ "td"
      val things = trs.filter(x ⇒ (x \ "@class").toString == "athing")
      val subtexts = tds.filter{x ⇒className(x) == "subtext" }
      things.zip(subtexts).map{ case(t,s) ⇒ ItemNodes(t, s)}.toList
    }
  }

}

/** A news item */
case class Item(title: String, link: URI, author: String, points: Int, comments: Int, rank: Int)

object Item {

  /**
    * Try to create an [[Item]] from an [[ItemNodes]]
    */
  def fromItemNodes(item: ItemNodes): Validated[String, Item] = {
    (item.title |@| item.link |@| item.author |@| item.points |@|
      item.comments |@| item.rank).map{
      case(title, link, author, points, comments, rank) ⇒
        Item(title, link, author, points, comments, rank)
    }.leftMap{ _ ⇒
      s"Encountered errors while reading item: ${item.show}"
    }
  }

  implicit val uriWrites: Format[URI] = new Format[URI]{
    override def writes(o: URI): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[URI] = json match {
      case JsString(s) ⇒
        Try(new URI(s)) match {
          case scala.util.Success(uri) ⇒ JsSuccess(uri)
          case scala.util.Failure(error) ⇒ JsError(error.getMessage)
        }
      case _ ⇒ JsError("Expected string")
    }
  }

  /**
    * Used to convert an [[Item]] to and from JSON
    */
  implicit val itemFormat: Format[Item] = Json.format[Item]
}


private object Helpers{
  def className(node: Node): String = (node \ "@class").toString

  def attributeValueEquals(value: String)(node: Node): Boolean = {
    node.attributes.exists(_.value.text == value)
  }
}
