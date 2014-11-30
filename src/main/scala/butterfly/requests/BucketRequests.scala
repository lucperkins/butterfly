package butterfly.requests

import butterfly.core.{RiakMessageBuilder, RiakMessageType, RiakConverter, RiakRequest}
import com.basho.riak.protobuf.RiakKvPB.{RpbListKeysResp, RpbListBucketsResp}
import com.basho.riak.protobuf.RiakPB.RpbGetBucketResp
import com.google.protobuf.ByteString
import collection.JavaConverters._
import scala.concurrent.Future

trait BucketRequests extends RiakRequest with RiakConverter {
  implicit def byteStringListToStringList(lbs: java.util.List[ByteString]): List[String] =
    lbs.asScala.map(_.toStringUtf8).toList

  def listBuckets(bucketType: String = "default",
                  streaming: Boolean = false,
                  timeout: Int = 10): Future[Option[List[String]]] = {
    val listBucketsReq = RiakMessageBuilder.ListBuckets(bucketType, streaming, timeout)
    val req = buildRequest(RiakMessageType.RpbListBucketsReq, listBucketsReq)
    req.map(resp => {
      val response = RpbListBucketsResp.parseFrom(resp.message)
      Some(response.getBucketsList)
    })
  }

  def listKeys(bucket: String,
               bucketType: String = "default",
               timeout: Int = 10): Future[Option[List[String]]] = {
    val listKeysReq = RiakMessageBuilder.ListKeys(bucket, bucketType, timeout)
    val req = buildRequest(RiakMessageType.RpbListKeysReq, listKeysReq)
    req.map(resp => {
      val response = RpbListKeysResp.parseFrom(resp.message)
      Some(response.getKeysList)
    })
  }

  sealed trait Backend

  object Backend {
    case object LevelDB extends Backend
    case object Bitcask extends Backend
    case object Memory extends Backend
    case object Multi extends Backend
  }

  case class BucketProperties(allowMult: Boolean,
                              backend: Backend,
                              basicQuorum: Boolean,
                              bigVClock: Int,
                              consistent: Boolean)

  def getBucketProperties(bucket: String,
                          bucketType: String = "default"): Future[Option[BucketProperties]] = {
    val getBucketPropsReq = RiakMessageBuilder.GetBucketProperties(bucket, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetBucketReq, getBucketPropsReq)
    req.map(resp => {
      val response = RpbGetBucketResp.parseFrom(resp.message)
      val props = response.getProps
      val bucketProperties = new BucketProperties(
        props.getAllowMult,
        props.getBackend,
        props.getBasicQuorum,
        props.getBigVclock,
        props.getConsistent
      )
      Some(bucketProperties)
    })
  }

  implicit def backendConversion(name: ByteString): Backend = {
    name.toStringUtf8 match {
      case "leveldb" => Backend.LevelDB
      case "bitcask" => Backend.Bitcask
      case "memory"  => Backend.Memory
      case "multi"   => Backend.Memory
    }
  }
}