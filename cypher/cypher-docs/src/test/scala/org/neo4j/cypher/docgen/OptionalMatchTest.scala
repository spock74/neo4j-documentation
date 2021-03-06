/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.docgen

import org.junit.Assert._
import org.junit.Test
import org.neo4j.visualization.graphviz.GraphStyle
import org.neo4j.visualization.graphviz.AsciiDocSimpleStyle

class OptionalMatchTest extends DocumentingTestBase with SoftReset {

  override def graphDescription = List(
    "Charlie:Person ACTED_IN WallStreet:Movie",
    "Martin:Person ACTED_IN WallStreet:Movie",
    "Michael:Person ACTED_IN WallStreet:Movie",
    "Martin:Person ACTED_IN TheAmericanPresident:Movie",
    "Michael:Person ACTED_IN TheAmericanPresident:Movie",
    "Oliver:Person DIRECTED WallStreet:Movie",
    "Rob:Person DIRECTED TheAmericanPresident:Movie",
    "Charlie:Person FATHER Martin:Person")

  override val properties = Map(
    "Charlie" -> Map("name" -> "Charlie Sheen"),
    "Oliver" -> Map("name" -> "Oliver Stone"),
    "Michael" -> Map("name" -> "Michael Douglas"),
    "Rob" -> Map("name" -> "Rob Reiner"),
    "Martin" -> Map("name" -> "Martin Sheen"),
    "WallStreet" -> Map("title" -> "Wall Street"),
    "TheAmericanPresident" -> Map("title" -> "The American President")
  )

  override protected def getGraphvizStyle: GraphStyle =
    AsciiDocSimpleStyle.withAutomaticRelationshipTypeColors()

  def section: String = "OPTIONAL MATCH"

  @Test def optionalRelationship() {
    testQuery(
      title = "Relationship",
      text = "If a relationship is optional, use the `OPTIONAL MATCH` clause. This is similar to how a SQL outer join " +
        "works. If the relationship is there, it is returned. If it's not, `null` is returned in its place. ",
      queryText = """MATCH (a:Movie {title: 'Wall Street'}) OPTIONAL MATCH (a)-->(x) RETURN x""",
      optionalResultExplanation = """Returns `null`, since the node has no outgoing relationships.""",
      assertions = (p) => assertEquals(List(Map("x" -> null)), p.toList)
    )
  }

  @Test def nodePropertyFromOptionalNode() {
    testQuery(
      title = "Properties on optional elements",
      text = "Returning a property from an optional element that is `null` will also return `null`.",
      queryText = "MATCH (a:Movie {title: 'Wall Street'}) OPTIONAL MATCH (a)-->(x) RETURN x, x.name",
      optionalResultExplanation = """Returns the element x (`null` in this query), and `null` as its name.""",
      assertions = (p) => assertEquals(List(Map("x" -> null, "x.name" -> null)), p.toList)
    )
  }

  @Test def optionalTypedRelationship() {
    testQuery(
      title = "Optional typed and named relationship",
      text = "Just as with a normal relationship, you can decide which variable it goes into, and what relationship type " +
        "you need.",
      queryText = """MATCH (a:Movie {title: 'Wall Street'}) OPTIONAL MATCH (a)-[r:ACTS_IN]->() RETURN r""",
      optionalResultExplanation = """This returns a node, and `null`, since the node has no outgoing `ACTS_IN` relationships.""",
      assertions = (p) => assertEquals(List(Map("r" -> null)), p.toList)
    )
  }
}
