package butterfly.requests

import butterfly.RiakMessage
import butterfly.conflict.{SiblingResolver}
import butterfly.{RiakConverter, RiakRequest, RiakMessageType}
import com.basho.riak.protobuf.RiakKvPB
import com.basho.riak.protobuf.RiakKvPB.{RpbContent, RpbPutResp, RpbGetReq, RpbGetResp}
import com.google.protobuf.ByteString
import spray.json._

import scala.collection.JavaConverters._
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

  def fetchAsString(bucket: String, key: String, bucketType: String = "default"): Future[Option[String]] = {
    fetch(bucket, key, bucketType) map {
      case res: RpbGetResp =>
        val str = res.getContent(0).getValue.toStringUtf8
        Some(str)
      case _ =>
        None
    }
  }

  def get[T](bucket: String, key: String, bucketType: String = "default")
            (implicit resolver: SiblingResolver[T], format: JsonFormat[T]): Future[Option[T]] =
  {
    val msg = RiakMessageBuilder.getRequest(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req map { resp =>
      val response = RpbGetResp.parseFrom(resp.message)
      hasSiblings(response) match {
        case false =>
          siblingToObject(response.getContent(0))
        case true =>
          val siblingList = response.getContentList
          contentListToObjectList[T](siblingList) map (x => x) match {
            case Some(siblings) => resolver.resolve(siblings)
            case None           => None
          }
      }

      val jsString = response.getContent(0).getValue.toStringUtf8
      jsString.parseJson.asJsObject.convertTo[T] match {
        case t => Some(t)
        case _ => None
      }
    }
  }

  def hasSiblings(response: RpbGetResp): Boolean = {
    response.getContentList.size > 1
  }

  def contentListToObjectList[T](siblingValues: java.util.List[RpbContent]): Option[List[T]] = {
    val buffer = new ListBuffer[T]
    siblingValues.asScala.map(sib => {
      siblingToObject(sib) match {
        case t: T => buffer += t
        case _    => None
      }
    })
    Some(buffer.toList)
  }

  def siblingToObject[T](sibling: RpbContent)
                        (implicit format: JsonFormat[T]): Option[T] = {
   sibling.getContentType.toString == "application/json" match {
      case true =>
        val json = sibling.getValue.toStringUtf8
        json.toJson match {
          case t: T => Some(t)
          case _  => None
        }
      case false =>
        None
    }
  }

  def store[T](t: T, bucket: String, key: String, bucketType: String, vClock: Option[ByteString])
              (implicit format: JsonFormat[T]): Future[Boolean] = {
    val msg = RiakMessageBuilder.storeRequest(t, bucket, key, bucketType, vClock.map(v => v))
    val req = buildRequest(RiakMessageType.RpbPutReq, msg)
    req map { resp =>
      RpbPutResp.parseFrom(resp.message) match {
        case res: RpbPutResp =>
          res.hasVclock
        case _               => false
      }
    }
  }

  def safeUpdate[T](t: T, bucket: String, key: String, bucketType: String)
                   (implicit format: JsonFormat[T]): Future[Boolean] = {
    getVClock(bucket, key, bucketType) map {
      case Some(v) =>
        store(t, bucket, key, bucketType, Some(v))
        true
      case None    => false
    }
  }

  def getVClock(bucket: String, key: String, bucketType: String): Future[Option[ByteString]] = {
    val msg = RiakMessageBuilder.getRequest(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req map { resp =>
      val responseMessage = RpbGetResp.parseFrom(resp.message)
      val vClock = responseMessage.getVclock
      Some(vClock)
    }
  }
  /*
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
  */
}