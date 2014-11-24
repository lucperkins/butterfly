package butterfly.datatypes

import com.basho.riak.protobuf.RiakDtPB.DtFetchResp

import collection.JavaConverters._

import scala.collection.mutable.ListBuffer

abstract class DataType

case class RiakMap(value: List[MapField]) extends DataType
case class RiakCounter(value: Long) extends DataType
case class RiakSet(value: Seq[String]) extends DataType

case class MapField(key: String, value: DataType)

/*

trait RiakDataType {
  def getMap(resp: DtFetchResp): Option[RiakMap] = {
    responseToDataType[RiakMap](resp)
  }

  def responseToDataType[T >: DataType](resp: DtFetchResp): Option[T] = {
    resp.getType.getNumber match {
      case 1 =>
        val count = resp.getValue.getCounterValue
        Some(new RiakCounter(count))
      case 2 =>
        val rawList = resp.getValue.getSetValueList.asScala
        val buffer = new ListBuffer[String]
        rawList.map(item => buffer ++ item.toString)
        Some(new RiakSet(buffer.toSeq))
      case _ => None
    }
  }
}

*/