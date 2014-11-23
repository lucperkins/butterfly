package butterfly.requests

import butterfly.RiakMessage
import butterfly.conflict.RiakResolver
import butterfly.{RiakConverter, RiakRequest, RiakMessageType}
import com.basho.riak.protobuf.RiakKvPB
import com.basho.riak.protobuf.RiakKvPB.{RpbContent, RpbPutResp, RpbGetReq, RpbGetResp}
import spray.json._


import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

trait KVRequests extends RiakRequest with RiakConverter {
  def fetch(bucket: String, key: String, bucketType: String = "default"): Future[RpbGetResp] = {
    val msg = RiakMessageBuilder.getRequest(bucket, key, bucketType)

    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req.map(resp => {
      RpbGetResp.parseFrom(resp.message)
    })
  }

  def fetchAsString(bucket: String, key: String, bucketType: String = "default"): Future[String] = {
    fetch(bucket, key, bucketType) map {
      case res: RpbGetResp => res.getContent(0).getValue.toStringUtf8
      case _               => throw new Exception("Not found")
    }
  }

  def get[T](bucket: String, key: String, bucketType: String = "default")
            (implicit format: JsonFormat[T]): Future[Option[T]] =
  {
    val msg = RiakMessageBuilder.getRequest(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req map { resp =>
      val response = RpbGetResp.parseFrom(resp.message)
      println("Response parsed")
      val jsString = response.getContent(0).getValue.toStringUtf8
      println(s"String: $jsString")
      jsString.parseJson.asJsObject.convertTo[T] match {
        case t => Some(t)
        case _ => None
      }
    }
  }

  def store[T](t: T, bucket: String, key: String, bucketType: String = "default")
              (implicit format: JsonFormat[T]): Future[Boolean] = {
    val msg = RiakMessageBuilder.storeRequest(t, bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbPutReq, msg)
    req map { resp =>
      RpbPutResp.parseFrom(resp.message) match {
        case res: RpbPutResp =>
          res.hasVclock
        case _               => false
      }
    }
  }

  implicit def resolveSiblingContent[T](message: RpbGetResp)
                                       (implicit resolver: RiakResolver[T], format: JsonFormat[T]): Option[T] = {
    val siblings = message.getContentList
    val numSiblings = siblings.size
    if (numSiblings == 0) {
      None
    } else if (numSiblings == 1) {
      val json = siblings.get(0).getValue.toStringUtf8.toJson
      json.convertTo[T] match {
        case t => Some(t)
        case _ => None
      }
    } else {
      val list = contentListToType[T](message.getContentList)
      resolver.resolve(list)
    }
  }

  def contentListToType[T](siblings: List[RpbContent])(implicit format: JsonFormat[T]): List[T] = {
    val lb = new ListBuffer[T]
    siblings.map(s => {
      val json = s.getValue.toStringUtf8.toJson
      val t = json.convertTo[T]
      t match {
        case value => lb += value
        case _     =>
      }
    })
    lb.toList
  }
}