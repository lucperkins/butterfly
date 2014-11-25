package butterfly.cluster

import java.util

import akka.actor.ActorSystem
import butterfly.{RiakWorker, RiakMessage}
import butterfly.requests.KVRequests

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.Future

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

trait NodeStateListener {
  def nodeStateChanged(node: RiakNode, state: RiakNode.State)
}

class