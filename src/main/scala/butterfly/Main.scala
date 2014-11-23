package butterfly

import akka.actor.ActorSystem
import com.basho.riak.protobuf.RiakKvPB.RpbGetResp

import concurrent.ExecutionContext.Implicits.global

object Main extends App {
  implicit val system = ActorSystem("main-riak-butterfly-system")

  val client = RiakClient("localhost", 10017)

  client.get("test", "test") map (x => println(x.getContent(0).getValue.toStringUtf8))
}