package butterfly.requests

import butterfly.RiakConverter
import com.basho.riak.protobuf.RiakKvPB.{RpbContent, RpbPutReq, RpbGetReq}
import com.google.protobuf.ByteString

object RiakMessageBuilder extends RiakConverter {
  def getRequest(bucket: String, key: String, bucketType: String): RpbGetReq = {
    val props = new FetchProperties()

    RpbGetReq.newBuilder
      .setBucket(bucket)
      .setKey(key)
      .setType(bucketType)
      .setBasicQuorum(props.basicQuorum)
      .setDeletedvclock(props.deletedVclock)
      .setHead(props.head)
      .setIfModified(props.ifModified)
      .setNotfoundOk(props.notFoundOk)
      .setNVal(props.nVal)
      .setPr(props.pr)
      .build()
  }

  def putRequest(value: String, bucket: String, key: String, bucketType: String, vClock: ByteString): RpbPutReq = {
    val content = RpbContent.newBuilder
      .setContentType("application/json")
      .setCharset("utf-8")
      .setValue(value)
      .build

    RpbPutReq.newBuilder
      .setBucket(bucket)
      .setKey(key)
      .setType(bucketType)
      .setContent(content)
      .setVclock(vClock)
      .build()
  }
}
