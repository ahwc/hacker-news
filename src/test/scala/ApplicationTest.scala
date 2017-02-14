import java.net.URI

import org.scalatest.{Matchers, WordSpec}

import scala.xml.Node


class ApplicationTest extends WordSpec with Matchers {



  "The hacker news reader" must {
    // use copy and pasted content from hacker news
    val content: Node = HTML.load("/sample.txt")

    // the application will result in the following output:
    /**
    WARNING: Could not read items: Encountered errors while reading item: title: Valid(Build APIs for the Fortune 100 – EasyPost Hiring), link: Valid(https://www.easypost.com/jobs),
               author: Invalid(NonEmptyList(Could not find author)), points: Invalid(NonEmptyList(Could not find the number of points)), comments: Valid(0), rank: Valid(19)
[ {
  "title" : "Encrypted email is still a pain in 2017",
  "link" : "http://incoherency.co.uk/blog/stories/gpg.html",
  "author" : "jstanley",
  "points" : 236,
  "comments" : 192,
  "rank" : 1
}, {
  "title" : "Government-grade spyware hits Mexican advocates of soda tax",
  "link" : "http://www.bendbulletin.com/nation/5063332-151/government-grade-spyware-hits-mexican-advocates-of-soda-tax",
  "author" : "srameshc",
  "points" : 51,
  "comments" : 12,
  "rank" : 2
}, {
  "title" : "On Loneliness",
  "link" : "https://krishnamurti-teachings.info/book/commentaries-on-living-first-series.html#loneliness",
  "author" : "dominotw",
  "points" : 74,
  "comments" : 37,
  "rank" : 3
}, {
  "title" : "Big Picture of Calculus (2010) [video]",
  "link" : "https://www.youtube.com/watch?v=UcWsDwg1XwM&amp;index=2&amp;list=PLBE9407EA64E2C318",
  "author" : "espeed",
  "points" : 206,
  "comments" : 18,
  "rank" : 4
}, {
  "title" : "The Data Science Process",
  "link" : "https://www.springboard.com/blog/data-science-process/",
  "author" : "EternalData",
  "points" : 72,
  "comments" : 17,
  "rank" : 5
} ]
      */

    "be able to read the contents of HTML from hacker news" in {
      Application.readHackerNews(content, 3) shouldBe Right(List(
        Item("Encrypted email is still a pain in 2017",
          new URI("http://incoherency.co.uk/blog/stories/gpg.html"),
          "jstanley", 236, 192, 1),
        Item("Government-grade spyware hits Mexican advocates of soda tax",
          new URI("http://www.bendbulletin.com/nation/5063332-151/government-grade-spyware-hits-mexican-advocates-of-soda-tax"),
          "srameshc", 51, 12, 2),
        Item("On Loneliness",
          new URI("https://krishnamurti-teachings.info/book/commentaries-on-living-first-series.html#loneliness"),
        "dominotw", 74, 37, 3)
      ))
    }

    // TODO: test input validation!

  }


}
