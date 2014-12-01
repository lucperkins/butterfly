package butterfly.core

import nl.gideondk.sentinel.{ConsumerAction, Resolver}

object RiakMessageHandler extends Resolver[RiakMessage, RiakMessage] {
   def process = {
     case x =>
       x.messageType match {
         case _ => ConsumerAction.AcceptSignal
       }
   }
 }
