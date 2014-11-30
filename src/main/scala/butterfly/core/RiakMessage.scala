package butterfly.core

import com.google.protobuf.{ByteString => BS}

case class RiakMessage(messageType: RiakMessageType, message: BS)
