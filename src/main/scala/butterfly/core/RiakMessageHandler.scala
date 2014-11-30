package butterfly.core

object RiakMessageHandler extends Resolver[RiakMessage, RiakMessage] {
   def process = {
     case x =>
       x.messageType match {
         case _ => ConsumerAction.AcceptSignal
       }
   }
 }
