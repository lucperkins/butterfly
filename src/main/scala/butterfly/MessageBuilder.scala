package butterfly

import com.basho.riak.protobuf.RiakKvPB.{RpbContent, RpbPutReq, RpbGetReq}
import com.google.protobuf.ByteString

object MessageBuilder {
  def byteString(str: String): ByteString = ByteString.copyFromUtf8(str)
  def makeContent(value: ByteString) = {
    RpbContent.newBuilder()

  }

  def getRequest(bucket: String,
                 key: String,
                 bucketType: String = "default",
                 props: FetchProperties): RpbGetReq = {
    val messageBuilder: RpbGetReq.Builder = RpbGetReq.newBuilder()
      .setBucket(byteString(bucket))
      .setKey(byteString(key))

    if (bucketType != "default") messageBuilder.setType(byteString(bucketType))
    if (props.basicQuorum) messageBuilder.setBasicQuorum(true)
    if (props.deletedVclock) messageBuilder.setDeletedvclock(true)
    if (props.head) messageBuilder.setHead(true)
    if (!props.notfoundOk) messageBuilder.setNotfoundOk(false)
    if (props.nVal != 3) messageBuilder.setNVal(props.nVal)
    if (props.pr != 0) messageBuilder.setPr(props.pr)

    messageBuilder.build()
  }

  def putRequest(bucket: String,
                 key: String,
                 bucketType: String = "default",
                 contentType: String = "application/json",
                 value: ByteString,
                 props: StoreProperties): RpbPutReq = {
    val messageBuilder: RpbPutReq.Builder = RpbPutReq.newBuilder()
      .setBucket(byteString(bucket))

    if (key != null) messageBuilder.setKey(byteString(key))

    if (bucketType != "default") messageBuilder.setType(byteString(bucketType))
    val content: RpbContent = putContent(value, contentType)
    messageBuilder.setContent(content)
    messageBuilder.build()
  }

  def putContent(value: ByteString,
              contentType: String = "application/json"): RpbContent = {
    val messageBuilder: RpbContent.Builder = RpbContent.newBuilder()
      .setCharset(byteString("utf-8"))
      .setValue(value)

    messageBuilder.build()
  }
}
