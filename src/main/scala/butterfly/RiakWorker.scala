package butterfly

import akka.actor.ActorSystem
import akka.io.{LengthFieldFrame, PipePair, PipelineContext, PipelineStage}
import akka.util.{ByteString, ByteStringBuilder}
import com.google.protobuf.Message
import com.google.protobuf.{ByteString => BS}
import nl.gideondk.sentinel.{Client, ConsumerAction, Resolver}

import concurrent.duration._

case class RiakMessage(messageType: RiakMessageType, message: BS)

trait RiakConverter {
  implicit def akkaToProtobuf(bs: ByteString): BS = BS.copyFrom(bs.asByteBuffer)
  implicit def protobufToAkka(bs: BS): ByteString = ByteString.fromArray(bs.toByteArray)
  implicit def byteStringToString(bs: BS): String = bs.toStringUtf8
  implicit def stringToByteString(str: String): BS = BS.copyFromUtf8(str)
  implicit def pbcMessageToAkkaByteString(msg: Message): ByteString = protobufToAkka(msg.toByteString)
}

object RiakMessageHandler extends Resolver[RiakMessage, RiakMessage] {
  def process = {
    case x =>
      x.messageType match {
        case _ => ConsumerAction.AcceptSignal
      }
  }
}

class RiakPipeline extends PipelineStage[PipelineContext, RiakMessage, ByteString, RiakMessage, ByteString]
  with RiakConverter {

  def apply(ctx: PipelineContext) = new PipePair[RiakMessage, ByteString, RiakMessage, ByteString] {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN

    override val commandPipeline = {
      msg: RiakMessage =>
        val bsb = new ByteStringBuilder
        bsb.putByte(RiakMessageType.messageTypeToInt(msg.messageType).toByte)
        bsb ++= msg.message
        ctx.singleCommand(bsb.result())
    }

    override val eventPipeline = {
      bs: ByteString =>
        val iter = bs.iterator
        val messageType = iter.getByte
        val message = iter.toByteString
        val akkaMessage = RiakMessage(RiakMessageType.intToMessageType(messageType.toInt), message)
        ctx.singleEvent(akkaMessage)
    }
  }
}

trait RiakStages {
  val stages = new RiakPipeline >> new LengthFieldFrame(1024 * 1024 * 200, lengthIncludesHeader = false)
}

class RiakWorker

object RiakWorker extends RiakStages {
  def apply(host: String, port: Int, numberOfWorkers: Int = 4)(implicit system: ActorSystem) = {
    Client.randomRouting(
      host,
      port,
      numberOfWorkers,
      "Riak",
      stages,
      5 seconds,
      RiakMessageHandler,
      true, // allow pipelining
      1024 * 8, // lowBytes
      1024 * 1024 * 5, // highBytes
      1024 * 1024 * 200 //maxBufferSize
    )(system)
  }
}