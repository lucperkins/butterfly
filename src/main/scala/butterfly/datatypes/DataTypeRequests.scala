package butterfly.datatypes

import butterfly.core.{RiakConverter, RiakMessageBuilder, RiakMessageType, RiakRequest}
import com.basho.riak.protobuf.RiakDtPB.{MapEntry, DtFetchResp}
import com.google.protobuf.ByteString

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

trait DataType

trait DataTypeRequests extends RiakRequest with RiakConverter {
  def getDataTypeResponse(bucket: String, key: String, bucketType: String): Future[DtFetchResp] = {
    val dtFetchReq = RiakMessageBuilder.dataTypeFetchRequest(bucket, key, bucketType)
    val req = buildRequest(RiakMessageType.DtFetchReq, dtFetchReq)
    req.map(resp =>
      DtFetchResp.parseFrom(resp.message)
    )
  }

  implicit def byteStringConversion(bs: ByteString): String = bs.toStringUtf8

  def getDataType(bucket: String, key: String, bucketType: String): Future[Option[DataType]] = {
    getDataTypeResponse(bucket, key, bucketType).map(resp => {
      resp.getType.getNumber match {
        case 1 =>
          val counterValue = resp.getValue.getCounterValue
          Some(new RiakCounter(counterValue))
        case 2 =>
          val setValue = resp.getValue.getSetValueList.asScala.toList
          Some(new RiakSet(setValue))
        case 3 =>
          val mapEntries = resp.getValue.getMapValueList.asScala.toList
          val map = mapEntriesToMap(mapEntries)
          Some(map)
        case _ => None
      }
    })
  }

  def mapEntriesToMap(entries: List[MapEntry]): RiakMap = {
    val buffer = new ListBuffer[MapField]
    entries.map(entry => {
      val field = entry.getField
      val name = field.getName
      field.getType.getNumber match {
        case 1 =>
          buffer += new MapField(name, new RiakCounter(entry.getCounterValue))
        case 2 =>
          buffer += new MapField(name, new RiakSet(entry.getSetValueList.asScala.toList))
        case 3 =>
          buffer += new MapField(name, new RiakRegister(entry.getRegisterValue.toStringUtf8))
        case 4 =>
          buffer += new MapField(name, new RiakFlag(entry.getFlagValue))
        case 5 =>
          buffer += new MapField(name, mapEntriesToMap(entry.getMapValueList.asScala.toList))
        case _ => throw new Exception("Riak map error")
      }
    })
    new RiakMap(buffer.toList)
  }
}
