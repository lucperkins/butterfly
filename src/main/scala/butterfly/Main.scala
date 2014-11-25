package butterfly

import akka.actor.ActorSystem
import butterfly.cluster.{RiakNode, RiakCluster}
import butterfly.conflict.RiakResolver
import butterfly.datatypes.RiakMap
import butterfly.requests.KVRequests
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp
import spray.json.DefaultJsonProtocol._

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Person(name: String, age: Int)

/*
trait Updates extends RiakRequest with KVRequests {
  def safeUpdate[T](t: T, bucket: String, key: String, bucketType: String): Future[Unit] = {
    fetch(bucket, key, bucketType) map {
      case resp: RpbGetResp =>
        val vClock = resp.getVclock
        store[T](t, bucket, key, bucketType, Some(vClock)) map (x => ())
    }
  }
}
*/

class RiakException(message: String) extends Exception

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakNode("localhost", 10017)

  client.store[Person]()
}