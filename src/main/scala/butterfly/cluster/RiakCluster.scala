package butterfly.cluster

import java.util

import akka.actor.ActorSystem
import butterfly.{RiakWorker, RiakMessage}
import butterfly.requests.KVRequests

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.Future

trait NodeManager {
  def nodeList: ListBuffer[RiakNode]

  def addNodes(newNodes: List[RiakNode]) = {
    nodeList.appendAll(newNodes)
  }

  def addNode(node: RiakNode) = {
    nodeList.append(node)
  }

  def removeNode(node: RiakNode) = {
    nodeList -= node
  }

  def selectRandomtNode(): RiakNode = {
    val nodeCount = nodeList.length
    val random = new scala.util.Random().nextInt(nodeCount + 1)
    nodeList(random)
  }

  def numberOfNodes(): Int = nodeList.toList.length
}

sealed trait ClusterState

object ClusterState {
  case object CREATED extends ClusterState
  case object RUNNING extends ClusterState
  case object SHUTTING_DOWN extends ClusterState
  case object SHUT_DOWN extends ClusterState
}

object RiakCluster {
  def apply(): RiakCluster = {
    new RiakCluster
  }

  def apply(node: RiakNode): RiakCluster = {
    val cluster = new RiakCluster
    cluster.addNode(node)
    cluster
  }

  def apply(nodes: List[RiakNode]): RiakCluster = {
    val cluster = new RiakCluster
    cluster.addNodes(nodes)
    cluster
  }
}

class RiakCluster extends NodeManager {
  import ClusterState._

  def nodeList = new ListBuffer[RiakNode]

  def shutDown() = {
    state = ClusterState.SHUTTING_DOWN
  }
}
