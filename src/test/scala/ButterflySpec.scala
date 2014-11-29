import akka.actor.ActorSystem
import butterfly.cluster.RiakNode
import butterfly.requests.SiblingResolver
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Suite, ShouldMatchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import spray.json.DefaultJsonProtocol._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

object Riak {
  implicit val system = ActorSystem("riak-test-system")
  val node = RiakNode("localhost", 10017)
}

abstract class ButterflySpec extends WordSpec with Suite with ShouldMatchers with ScalaFutures {
  implicit val timeout = 60 seconds
  implicit val patience = PatienceConfig(timeout = Span(10, Seconds), interval = Span(200, Millis))
  val node = Riak.node
}

class RiakNodeSpec extends ButterflySpec {
  "A Riak node" should {
    "be properly instantiated" in {
      node.host should equal("localhost")
      node.port should equal(10017)
    }
  }
}

case class Generic(word: String, number: Int)

class GenericResolver extends SiblingResolver[Generic] {
  def resolveFunction(g1: Generic, g2: Generic): Generic = {
    if (g1.number > g2.number) g1 else g2
  }
}

object GenericResolver {
  def apply = new GenericResolver()
}

class SiblingResolutionSpec extends ButterflySpec {
  implicit val jsonFormat = jsonFormat2(Generic)
  implicit val resolver = GenericResolver

  val g1 = new Generic("word", 999)
  val g2 = new Generic("longer word", 111)
  node.store(g1, "test", "test", "siblings", None)
  node.store(g2, "test", "test", "siblings", None)

  "A set of siblings" should {
    "return a single value upon GET" in {
      node.get[Generic]("test", "test", "siblings") map {
        case Some(g) =>
          g.number should equal(999)
          g.word should equal("longer word")
        case None =>
          throw new Exception("Sibling resolution error")
      }
    }

    "resolve the conflict in Riak" in {
      node.get[Generic]("test", "test", "siblings") map {
        case Some(g) =>
          node.safeUpdate(g, "test", "test", "siblings")
        case None =>
          throw new Exception("Something went wrong")
      }

      node.get[Generic]("test", "test", "siblings") map {
        case Some(g) =>
          g.number should equal(999)
          g.word should equal("longer word")
        case None =>
          throw new Exception("Something went wrong")
      }
    }
  }
}