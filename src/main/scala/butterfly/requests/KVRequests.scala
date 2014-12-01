package butterfly.requests

import butterfly.core.{RiakMessageBuilder, RiakConverter, RiakRequest, RiakMessageType}
import com.basho.riak.protobuf.RiakKvPB.{RpbContent, RpbGetResp, RpbPutResp}
import com.google.protobuf.ByteString
import spray.json._

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

abstract class SiblingResolver[T] {
  def resolveFunction(t1: T, t2: T): T

  def resolve(siblings: List[T]): T = {
    siblings.reduce(resolveFunction)
  }
}

trait KVRequests extends RiakRequest with RiakConverter {
  def fetch(bucket: String, key: String, bucketType: String = "default"): Future[RpbGetResp] = {
    val msg = RiakMessageBuilder.Get(bucket, key, bucketType)

    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req.map(resp => {
      RpbGetResp.parseFrom(resp.message)
    })
  }

  def get[T](bucket: String, key: String, bucketType: String = "default")
            (implicit resolver: SiblingResolver[T], format: JsonFormat[T]): Future[Option[T]] =
  {
    val msg = RiakMessageBuilder.Get(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req map { resp =>
      val response = RpbGetResp.parseFrom(resp.message)
      inConflict(response) match {
        case true =>
          val contentList = response.getContentList.asScala.toList
          val siblings = contentListToScalaList[T](contentList)
          val resolved = resolver.resolve(siblings)
          Some(resolved)
        case false =>
          val jsString = response.getContent(0).getValue.toStringUtf8
          jsString.parseJson.asJsObject.convertTo[T] match {
            case t: T => Some(t)
            case _    => None
          }
      }
    }
  }

  def inConflict(res: RpbGetResp): Boolean = {
    res.getContentList.asScala.length > 1
  }

  def hasSiblings(response: RpbGetResp): Boolean = {
    response.getContentList.size > 1
  }

  def contentListToScalaList[T](siblings: List[RpbContent])
                               (implicit format: JsonFormat[T]): List[T] = {
    val tList = new ListBuffer[T]
    siblings.map(sibling => {
      val jsString = sibling.getValue.toStringUtf8
      jsString.parseJson.asJsObject.convertTo[T] match {
        case t: T => tList.append(t)
        case _    => throw new Exception("Conversion error")
      }
    })
    tList.toList
  }

  def unsafeStore[T](t: T, bucket: String, key: String, bucketType: String)
                    (implicit format: JsonFormat[T]): Unit = {
    rawStore(t, bucket, key, bucketType)
  }

  def store[T <: RiakObject](t: T) = {
    val msg = RiakMessageBuilder.UnsafeStore(t, t.bucket, t.key, t.bucketType)
  }

  def rawStore[T](t: T, bucket: String, key: String, bucketType: String, vClock: ByteString = ByteString.EMPTY)
                 (implicit format: JsonFormat[T]): Unit = {
    val msg = RiakMessageBuilder.SafePut(t, bucket, key, bucketType, vClock)
    val req = buildRequest(RiakMessageType.RpbPutReq, msg)
    req map (x => x)
  }

  def safeUpdate[T](t: T, bucket: String, key: String, bucketType: String)
                   (implicit format: JsonFormat[T]): Unit = {
    getVClock(bucket, key, bucketType) map {
      case Some(v) =>
        rawStore(t, bucket, key, bucketType, v)
      case None    => throw new Exception
    }
  }

  def getVClock(bucket: String, key: String, bucketType: String): Future[Option[ByteString]] = {
    val msg = RiakMessageBuilder.Get(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req map { resp =>
      val responseMessage = RpbGetResp.parseFrom(resp.message)
      val vClock = responseMessage.getVclock
      Some(vClock)
    }
  }
}