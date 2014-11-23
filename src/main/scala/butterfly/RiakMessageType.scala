package butterfly

sealed trait RiakMessageType

object RiakMessageType {
  case object RpbGetReq extends RiakMessageType
  case object RpbGetResp extends RiakMessageType

  val values = Map(
    9 -> RpbGetReq,
    10 -> RpbGetResp
  )

  def messageTypeToInt(mt: RiakMessageType): Int = {
    values.keys.toList(values.values.toList.indexOf(mt))
  }
  def intToMessageType(int: Int): RiakMessageType = values(int)
}