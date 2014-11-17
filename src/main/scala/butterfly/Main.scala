package butterfly

import java.net.InetSocketAddress

import com.basho.riak.protobuf.RiakKvPB.RpbGetReq.Builder
import com.basho.riak.protobuf.RiakKvPB._
import com.google.protobuf.{Message, ByteString}

object Main extends App {
  val props = new StoreProperties()

  val message: Message.Builder = new RpbPutReq().newBuilderForType()
    .setAsis(props.asIs)
    .setBucket(ByteString.copyFromUtf8("test"))
    .setDw(props.dw)
    .setIfNoneMatch(props.ifNoneMatch)
    .setIfNotModified(props.ifNotModified)
    .setKey(ByteString.copyFromUtf8("test"))
    .setNVal(props.nVal)
    .setPw(props.pw)
    .setReturnBody(props.returnBody)
    .setReturnHead(props.returnHead)
    .setSloppyQuorum(props.sloppyQuorum)
    .setTimeout(props.timeout)
    .setType(ByteString.copyFromUtf8("test"))
    .setW(props.w)
}