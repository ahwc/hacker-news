import java.net.URL

import cats.data.Validated
import play.api.libs.json.Json

import scala.util.Try
import scala.xml.Node

object Application  {

  val usage = "hackernews  --posts n"

  /**
    * Read the given number of posts from the given content. If an error is encountered, return a
    * String describing the error
    */
  def readHackerNews(content: Node, posts: Int): Either[String, List[Item]] = {

    ItemNodes.fromHTML(content).fold(
      errors ⇒ Left(s"Could not read HTML contents: ${errors.toList.mkString("; ")}"), { nodes ⇒
        val validatedItems = nodes.map { n ⇒Item.fromItemNodes(n) }
        validatedItems.collect { case Validated.Invalid(error) ⇒
          println(s"WARNING: Could not read items: $error")
        }
        Right(validatedItems.collect{ case Validated.Valid(item) ⇒ item }.take(posts))
      }
    )
  }

  def main(args: Array[String]) {
    // try to parse the number of posts required
    val result = if(args.isEmpty){
      s"Could not read arguments. Usage:\n$usage"
    } else {
      args.toList match {
        case "--posts" :: n :: Nil ⇒
          Try(n.toInt) match {
            case scala.util.Failure(_) ⇒ s"Could not parse number of posts: $n"
            case scala.util.Success(posts) if posts > 100 ⇒ "Please ensure number of posts is < 100"
            case scala.util.Success(posts) if posts <= 0 ⇒ "Please ensure number of posts is > 0"
            case scala.util.Success(posts) ⇒
              // read the number of required posts and convert the items to JSON
              val site = new URL("https://news.ycombinator.com/")
              val content = HTML.load(site)
              val items = readHackerNews(content, posts)
              items.fold(identity, i ⇒ Json.prettyPrint(Json.toJson(i)))
          }

        case _ ⇒ s"Could not read the number of posts. Usage:\n$usage"
      }
    }

    println(result)
  }




}
