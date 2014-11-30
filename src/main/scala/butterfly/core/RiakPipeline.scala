package butterfly.core

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
