package butterfly.requests

import butterfly.{RiakConverter, RiakRequest, RiakMessageType}
import com.basho.riak.protobuf.RiakKvPB.{RpbPutResp, RpbGetReq, RpbGetResp}
import spray.json._


import scala.concurrent.Future

trait KVRequests extends RiakRequest with RiakConverter {
  def fetch(bucket: String, key: String, bucketType: String = "default"): Future[RpbGetResp] = {
    val msg = RiakMessageBuilder.getRequest(bucket, key, bucketType)

    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req.map(resp => {
      RpbGetResp.parseFrom(resp.message)
    })
  }

  def get[T](
    bucket: String,
    key: String,
    bucketType: String = "default")(implicit format: JsonReader[T]):
      Future[Option[T]] =
  {
    fetch(bucket, key, bucketType) map { resp =>
      resp.getContent(0).getValue.toStringUtf8 match {
        case value: String =>
          value.parseJson match {
            case js: JsValue =>
              val converted = js.convertTo[T]
              converted match {
                case t => Some(t)
                case _ => None
              }
          }
        case _ => None
      }
    }
  }

  /*
  def store[T](t: T,
               bucket: String,
               key: String,
               bucketType: String = "default")(implicit format: JsonWriter[T]): Future[Boolean] = {
    val json = t.toJson.toString
    val msg = RiakMessageBuilder.storeRequest(json, bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbPutReq, msg)
    req.map(resp => {
      RiakMessageType.messageTypeToInt(resp.messageType) != 0
    })
  }
  */
}
