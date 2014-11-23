package butterfly

import akka.actor.ActorSystem
import butterfly.conflict.RiakResolver
import butterfly.datatypes.RiakMap
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp
import spray.json.DefaultJsonProtocol._

import concurrent.ExecutionContext.Implicits.global

case class Person(name: String, age: Int, interests: Set[String])

class PersonResolver extends RiakResolver[Person] {
  def resolve(siblings: List[Person]) = {
    siblings(0)
  }
}

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakClient("localhost", 10017)

  implicit val personFormat = jsonFormat3(Person)

  client.get[Person]("test", "test") map (println(_))
}