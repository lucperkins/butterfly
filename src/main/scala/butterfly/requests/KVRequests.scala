package butterfly.requests

import butterfly.conflict.RiakResolver
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

  def fetchAsString(bucket: String, key: String, bucketType: String = "default"): Future[String] = {
    fetch(bucket, key, bucketType) map {
      case res: RpbGetResp => res.getContent(0).getValue.toStringUtf8
      case _               => throw new Exception("Not found")
    }
  }

  def get[T](bucket: String, key: String, bucketType: String = "default")
            (implicit format: JsonFormat[T]): Future[T] =
  {
    val msg = RiakMessageBuilder.getRequest(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.RpbGetReq, msg)
    req.map(resp => {
      RpbGetResp.parseFrom(resp.message) match {
        case getResp: RpbGetResp =>
          val jsString = getResp.getContent(0).getValue.toStringUtf8
          jsString.parseJson match {
            case js: JsValue =>
              js.convertTo[T] match {
                case t => t
                case _ => throw new Exception("Couldn't parse JSON to appropriate type")
              }
            case _ => throw new Exception("Couldn't parse JSON")
          }
        case _ => throw new Exception("Not found")
      }
    })
  }
}