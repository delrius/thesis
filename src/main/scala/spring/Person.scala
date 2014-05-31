package spring

import org.springframework.data.neo4j.annotation._
import org.neo4j.graphdb.Direction
import collection.JavaConverters._
import spring.annotations._
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.annotation.NodeEntity

@NodeEntity
case class Person(val name: String) {
  @GraphId
  var id: java.lang.Long = _

  @Fetch
  @RelatedTo(`type` = "TEAMMATE", direction = Direction.BOTH)
  var teammates: java.util.Set[Person]  = _

  def workWith(person: Person) {
    if (teammates == null) {
      teammates = new java.util.HashSet[Person]()
    }
    teammates.add(person)
  }

  def this() = this("")

  def makeSetToString = if (teammates == null) "" else teammates.asScala.map(_.name).mkString(", ")
  override def toString: String = name +"->"+ makeSetToString
}