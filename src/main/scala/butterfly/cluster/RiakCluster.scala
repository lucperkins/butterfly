package butterfly.cluster

import butterfly.core.RiakMessage

trait NodeManager {
  def init(nodes: List[RiakNode]): Unit
  def executeOnNode(operation: RiakMessage => RiakMessage, previousNode: RiakNode): Unit
  def addNote(node: RiakNode): Unit
  def removeNode(node: RiakNode): Boolean
}

class DefaultNodeManager extends NodeManager {
  def init(nodes: List[RiakNode]): Unit = println("Init")
  def executeOnNode(operation: RiakMessage => RiakMessage, previousNode: RiakNode): Unit = println("Executing")
  def addNote(node: RiakNode): Unit = println("Adding node")
  def removeNode(node: RiakNode): Boolean = true
}