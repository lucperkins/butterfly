package butterfly.cluster

import butterfly.RiakMessage

import scala.concurrent.Future

object RiakCluster {
  def apply(nodes: List[RiakNode]): RiakCluster = RiakCluster(nodes)
}

class RiakCluster(var nodes: collection.mutable.ListBuffer[RiakNode]) {
  def addNode(node: RiakNode) = {
    nodes += node
  }

  def removeNode(node: RiakNode) = {
    nodes -= node
  }

  private def selectRandomtNode(): RiakNode = {
    val nodeCount = nodes.length
    val random = new scala.util.Random().nextInt(nodeCount + 1)
    nodes(random)
  }
}
