package butterfly

import akka.actor.ActorSystem
import butterfly.datatypes.RiakMap
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp
import spray.json.DefaultJsonProtocol._

import concurrent.ExecutionContext.Implicits.global

case class Person(name: String, age: Int, interests: Set[String])

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakClient("localhost", 10017)

  implicit val personFormat = jsonFormat3(Person)

  val luc = new Person("Luc", 32, Set("weights", "computers"))

  client.get[Person]("test", "test") map {
    case Some(p) => println(p)
    case None    => println("OOPS")
  }
}