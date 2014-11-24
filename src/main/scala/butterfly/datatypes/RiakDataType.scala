package butterfly.datatypes

object RiakDataType {
  case object RiakCounter extends RiakDataType
  case object RiakSet extends RiakDataType
  case object RiakMap extends RiakDataType

  val values = Map(
    1 -> RiakCounter,
    2 -> RiakSet,
    3 -> RiakMap
  )

  def dataTypeToInt(dt: RiakDataType): Int =
    getValueByIndex[RiakDataType](dt, values)

  private def getValueByIndex[T](t: T, m: Map[Int, T]): Int = {
    m.keys.toList(m.values.toList.indexOf(t))
  }
}

trait RiakDataType {

}
