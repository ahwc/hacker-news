import java.net.URL

import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.xml.sax.InputSource

import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter


/**
  * Provides tools to convert HTML to an XML
  */
object HTML {
  lazy val adapter = new NoBindingFactoryAdapter
  lazy val parser = (new SAXFactoryImpl).newSAXParser

  /**
    * Read HTML from the given URL
    */
  def load(url: URL, headers: Map[String, String] = Map.empty): Node = {
    val conn = url.openConnection()
    headers.foreach{ case (k,v) ⇒ conn.setRequestProperty(k, v) }
    val source = new InputSource(conn.getInputStream)
    adapter.loadXML(source, parser)
  }

  /**
    * Read HTML from the contents of the given file
    */
  def load(filePath: String): Node = {
    val stream = getClass.getResourceAsStream(filePath)
    val source = new InputSource(stream)
    adapter.loadXML(source, parser)
  }
}
