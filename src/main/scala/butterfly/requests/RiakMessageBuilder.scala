package butterfly.requests

import butterfly.RiakConverter
import com.basho.riak.protobuf.RiakKvPB.RpbGetReq

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
}
