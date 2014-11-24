package butterfly

import akka.actor.ActorSystem
import butterfly.conflict.RiakResolver
import butterfly.datatypes.RiakMap
import butterfly.requests.KVRequests
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp
import spray.json.DefaultJsonProtocol._

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Person(name: String, age: Int)

trait Updates extends RiakRequest with KVRequests {
  def safeUpdate[T](t: T, bucket: String, key: String, bucketType: String): Future[Unit] = {
    fetch(bucket, key, bucketType) map {
      case resp: RpbGetResp =>
        val vClock = resp.getVclock
        store[T](t, bucket, key, bucketType, Some(vClock)) map (x => ())
    }
  }
}

class RiakException(message: String) extends Exception

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakClient("localhost", 10017)

  implicit val personFormat = jsonFormat2(Person)

  val tony = new Person("Tony", 55)

  client.search("scores", "counter:*") map {
    case Some(r) =>
      println(s"MaxScore: ${r.maxScore}")
      println(s"NumFound: ${r.numFound}")
      r.docs.map(doc => {
        println(s"Key: ${doc.key}; bucket: ${doc.bucket}; bucketType: ${doc.bucketType}")
        println(s"Value: ${doc.value}")
      })
    case None => println("OOPS")
  }
}