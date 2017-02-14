 
# Hacker News Reader

Reads news items from hacker news (<https://news.ycombinator.com/>) and outputs a given number of posts as JSON

## Building

Packaging requires `sbt (>= 0.13)` and `scala (2.11.8)`. At the command-line run

```
  sbt pack-archive
```

This creates a zip and a tar ball in the `target` directory of the project which contains the binaries.


## Usage

```
  hackernews --posts n
```
where `0 < n <= 100`


## Example output

```json
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

```

## Libraries used

The program uses a combination of `scala-xml` and `TagSoup` to convert HTML content into XML for easier parsing. Validation of parameters
is facilitated by the scala `cats` library and the conversion to JSON format is done the `Play JSON` library.





