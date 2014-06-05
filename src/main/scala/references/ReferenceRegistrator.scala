package references

object ReferenceRegistrator {
  var map: Map[Article, List[Reference]] = Map.empty


  def register(article: Article, reference: List[Reference]) = {
    map += (article -> reference)
  }
}

case class Article(name: String, authors: List[String])

case class Reference(name : String, authors : List[String])