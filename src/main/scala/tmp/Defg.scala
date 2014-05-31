package tmp

object Defg extends App {
  val HREF_ATTR = """(?i).*href\s*=\s*(\"([^\"]*\")|'[^']*'|([^'\">\s]+)).*""".r
  val A_TAG = "(?i)<a([^>]+)>.+?</a>".r
  val body = "<a href=\"_preface.pdf\" target=\"_blank\">de</a>"

  val DOWNLOAD_CONDITION = """(?i).*comp.*pdf.*""".r

  println("dsdsdsds/Comp/ooo.pdf".matches(DOWNLOAD_CONDITION.toString()))


//  private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
//  private static final String HTML_A_HREF_TAG_PATTERN =
//    "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
//
//  for (link <- findLinks(body))
//    println(link)


  def findLinks(body: String) : Iterator[String] = {
    //println("body = " + body)   //
    for {
      anchor <- A_TAG.findAllMatchIn(body)
      HREF_ATTR(dquot, quot, bare) <- anchor.subgroups
    }
      yield
      if (dquot != null) dquot
      else if (quot != null) quot
      else bare
  }


}
