package butterfly

import akka.actor.ActorSystem
import butterfly.datatypes.RiakMap
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp
import spray.json.DefaultJsonProtocol._

import concurrent.ExecutionContext.Implicits.global

case class Person(name: String, age: Int)

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakClient("localhost", 10017)

  implicit val personFormat = jsonFormat2(Person)

  client.get[Person]("test", "test", "default") map (x => println(x.get.name))
}