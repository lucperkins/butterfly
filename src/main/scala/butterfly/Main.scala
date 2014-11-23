package butterfly

import akka.actor.ActorSystem
import butterfly.conflict.RiakResolver
import butterfly.datatypes.RiakMap
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp
import spray.json.DefaultJsonProtocol._

import concurrent.ExecutionContext.Implicits.global

case class Person(name: String, age: Int)

class PersonResolver extends RiakResolver[Person] {
  def resolve(siblings: List[Person]) = {
    siblings(0)
  }
}

class RiakException(message: String) extends Exception

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakClient("localhost", 10017)

  implicit val personFormat = jsonFormat2(Person)

  val tony = new Person("Tony", 55)

  client.store[Person](tony, "test", "test") map (println(_))

  client.get[Person]("test", "test") map {
    case Some(p) => println(p.name)
    case None => throw new RiakException("Something went wrong")
  }
}