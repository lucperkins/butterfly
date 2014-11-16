package butterfly

import com.basho.riak.protobuf.RiakKvPB._
import com.google.protobuf.ByteString

object Main extends App {
  val message = RpbGetReq.newBuilder()
    .setBucket(ByteString.copyFromUtf8("test"))
    .setKey(ByteString.copyFromUtf8("test"))
  assert(message.getBucket.toStringUtf8 == "test")
  assert(message.getKey.toStringUtf8 == "test")
}