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
  var references: java.util.Collection[ReferencesRelation] = _

  def references(person: Author, in: String, out: String): ReferencesRelation = {
    if (references == null) {
      references = new java.util.HashSet[ReferencesRelation]()
    }
    val ref = ReferencesRelation(this, person, in, out)
    references.add(ref)
    ref
  }

  def this() = this("")

  def makeSetToString = if (references == null) "" else references.asScala.map(x => "[" + x.workCited + " by " + x.end.name + " in " + x.workCitedIn + "]").mkString(", ")

  override def toString: String = name + "->" + makeSetToString
}

@RelationshipEntity(`type` = "REFERENCES")
case class ReferencesRelation(@StartNode start: Author, @Fetch @EndNode end: Author, @Fetch workCited: String, @Fetch workCitedIn: String) {
  @GraphId
  var id: java.lang.Long = _

  def this() = this(null, null, "", "")
}