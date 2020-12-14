package io.joern.batteries

import io.shiftleft.codepropertygraph.generated.nodes

package object lib {

  object FindingKeys {
    val title = "title"
    val description = "description"
    val score = "score"
  }

  def finding(evidence: nodes.StoredNode,
              title: String,
              description: String,
              score: Double): nodes.NewFinding = {
    nodes.NewFinding(
      evidence = List(evidence),
      keyValuePairs = List(
        nodes.NewKeyValuePair(FindingKeys.title, title),
        nodes.NewKeyValuePair(FindingKeys.description, description),
        nodes.NewKeyValuePair(FindingKeys.score, score.toString)
      )
    )
  }

}