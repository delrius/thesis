package spring

import org.neo4j.graphdb.Direction
import collection.JavaConverters._
import spring.annotations._
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity

@NodeEntity
case class Author(name: String) {
  @GraphId
  var id: java.lang.Long = _

  @Fetch
  @RelatedToVia(`type` = "REFERENCES", direction = Direction.OUTGOING)
  var references: java.util.Set[ReferencesRelation] = _

  def workWith(person: Author, in: String, out: String) {
    if (references == null) {
      references = new java.util.HashSet[ReferencesRelation]()
    }

    references.add(ReferencesRelation(this, person, in, out))
  }

  def this() = this("")

  def makeSetToString = if (references == null) "" else references.asScala.map(_.end.name).mkString(", ")

  override def toString: String = name + "->" + makeSetToString
}

@RelationshipEntity(`type` = "REFERENCES")
case class ReferencesRelation(@StartNode start: Author, @Fetch @EndNode end: Author, @Fetch workCited: String, @Fetch workCitedIn: String) {
  @GraphId
  var id: java.lang.Long = _

  def this() = this(null, null, "", "")
}