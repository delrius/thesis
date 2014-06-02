package spring

import org.springframework.data.neo4j.repository.GraphRepository


trait AuthorRepository extends GraphRepository[Author] {
  def findByName(name: String): Author
  def findByReferences(name: String): java.lang.Iterable[ReferencesRelation]
}
