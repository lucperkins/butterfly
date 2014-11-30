package butterfly.core

import butterfly.requests.properties.FetchProperties
import com.basho.riak.protobuf.RiakDtPB.{DtUpdateReq, DtFetchReq}
import com.basho.riak.protobuf.RiakKvPB._
import com.basho.riak.protobuf.RiakPB.RpbGetBucketReq
import com.basho.riak.protobuf.RiakSearchPB.RpbSearchQueryReq
import com.google.protobuf.ByteString
import spray.json._

object RiakMessageBuilder extends RiakConverter {
  def Get(bucket: String,
                 key: String,
                 bucketType: String): RpbGetReq = {
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

  def SafePut(value: String,
                 bucket: String,
                 key: String,
                 bucketType: String,
                 vClock: ByteString): RpbPutReq = {
    val content = RpbContent.newBuilder
      .setContentType("application/json")
      .setCharset("utf-8")
      .setValue(value)
      .build

    RpbPutReq.newBuilder
      .setBucket(bucket)
      .setKey(key)
      .setType(bucketType)
      .setReturnBody(true)
      .setContent(content)
      .setVclock(vClock)
      .build()
  }

  def UnsafeStore[T](value: T,
                      bucket: String,
                      key: String,
                      bucketType: String,
                      vClock: Option[ByteString])
                     (implicit format: JsonWriter[T]): RpbPutReq = {
    val content = RpbContent.newBuilder
      .setContentType("application/json")
      .setCharset("utf-8")
      .setValue(value.toJson.compactPrint)
      .build

    RpbPutReq.newBuilder
      .setBucket(bucket)
      .setKey(key)
      .setType(bucketType)
      .setReturnBody(true)
      .setContent(content)
      .setVclock(vClock.getOrElse(ByteString.EMPTY))
      .build()
  }

  def Search(index: String,
             query: String): RpbSearchQueryReq = {
    RpbSearchQueryReq.newBuilder
      .setIndex(index)
      .setQ(query)
      .build
  }

  def FetchDataType(bucket: String,
                    key: String,
                    bucketType: String): DtFetchReq = {
    DtFetchReq.newBuilder
      .setBucket(bucket)
      .setKey(key)
      .setType(bucketType)
      .build
  }

  def UpdateDataType(bucket: String,
                     key: String,
                     bucketType: String): DtUpdateReq = {
    DtUpdateReq.newBuilder
      .setBucket(bucket)
      .setKey(key)
      .setType(bucketType)
      .build
  }

  def ListBuckets(bucketType: String = "default",
                  streaming: Boolean = false,
                  timeout: Int = 10): RpbListBucketsReq = {
    RpbListBucketsReq.newBuilder
      .setStream(streaming)
      .setType(bucketType)
      .setTimeout(timeout)
      .build
  }

  def ListKeys(bucket: String,
               bucketType: String = "default",
               timeout: Int = 10): RpbListKeysReq = {
    RpbListKeysReq.newBuilder
      .setBucket(bucket)
      .setTimeout(timeout)
      .setType(bucketType)
      .build
  }

  def GetBucketProperties(bucket: String,
                          bucketType: String = "default"): RpbGetBucketReq = {
    RpbGetBucketReq.newBuilder
      .setBucket(bucket)
      .setType(bucketType)
      .build
  }
}
