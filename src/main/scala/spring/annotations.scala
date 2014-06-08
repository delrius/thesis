package spring

import scala.annotation.meta.field

object annotations {

  // Class annotations

  type NodeEntity = org.springframework.data.neo4j.annotation.NodeEntity
  type RelationshipEntity = org.springframework.data.neo4j.annotation.RelationshipEntity

  // Field annotations

  type EndNode = org.springframework.data.neo4j.annotation.EndNode @field
  type Fetch = org.springframework.data.neo4j.annotation.Fetch @field
  type GraphId = org.springframework.data.neo4j.annotation.GraphId @field
  type GraphProperty = org.springframework.data.neo4j.annotation.GraphProperty @field
  type GraphTraversal = org.springframework.data.neo4j.annotation.GraphTraversal @field
  type Indexed = org.springframework.data.neo4j.annotation.Indexed @field
  type QueryResult = org.springframework.data.neo4j.annotation.QueryResult @field
  type Query = org.springframework.data.neo4j.annotation.Query @field
  type RelatedTo = org.springframework.data.neo4j.annotation.RelatedTo @field
  type RelatedToVia = org.springframework.data.neo4j.annotation.RelatedToVia @field
  type RelationshipType = org.springframework.data.neo4j.annotation.RelationshipType @field
  type ResultColumn = org.springframework.data.neo4j.annotation.ResultColumn @field
  type StartNode = org.springframework.data.neo4j.annotation.StartNode @field

}