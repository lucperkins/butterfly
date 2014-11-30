package butterfly.core

import akka.util.ByteString
import com.google.protobuf.{ByteString => BS, Message}

import scala.collection.mutable.ListBuffer

trait RiakConverter extends DataTypeConversions {
   implicit def akkaToProtobuf(bs: ByteString): BS = BS.copyFrom(bs.asByteBuffer)
   implicit def protobufToAkka(bs: BS): ByteString = ByteString.fromArray(bs.toByteArray)
   implicit def byteStringToString(bs: BS): String = bs.toStringUtf8
   implicit def stringToByteString(str: String): BS = BS.copyFromUtf8(str)
   implicit def pbcMessageToAkkaByteString(msg: Message): ByteString = protobufToAkka(msg.toByteString)
 }

trait DataTypeConversions {
  implicit def byteStringConversion(lbs: List[BS]): List[String] = {
    val stringList = new ListBuffer[String]
    lbs.map(item => stringList += item.toStringUtf8)
    stringList.toList
  }
}