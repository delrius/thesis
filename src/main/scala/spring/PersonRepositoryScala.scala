package spring

import org.springframework.data.neo4j.repository.GraphRepository


trait PersonRepositoryScala extends GraphRepository[Person] {
  def findByName(name: String): Person
  def findByTeammatesName(name: String): java.lang.Iterable[Person]
}
