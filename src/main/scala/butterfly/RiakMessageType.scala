package butterfly

sealed trait RiakMessageType

object RiakMessageType {
  // Error
  case object RpbErrResp extends RiakMessageType
  // GET
  case object RpbGetReq extends RiakMessageType
  case object RpbGetResp extends RiakMessageType
  // PUT
  case object RpbPutReq extends RiakMessageType
  case object RpbPutResp extends RiakMessageType
  // DELETE
  case object RpbDelReq extends RiakMessageType
  // List buckets
  case object RpbListBucketsReq extends RiakMessageType
  case object RpbListBucketsResp extends RiakMessageType
  // List keys
  case object RpbListKeysReq extends RiakMessageType
  case object RpbListKeysResp extends RiakMessageType
  // Search
  case object RpbSearchQueryReq extends RiakMessageType
  case object RpbSearchQueryResp extends RiakMessageType


  val values = Map(
    0 -> RpbErrResp,
    9 -> RpbGetReq,
    10 -> RpbGetResp,
    11 -> RpbPutReq,
    12 -> RpbPutResp,
    13 -> RpbDelReq,
    15 -> RpbListBucketsReq,
    16 -> RpbListBucketsResp,
    17 -> RpbListKeysReq,
    18 -> RpbListKeysResp,
    27 -> RpbSearchQueryReq,
    28 -> RpbSearchQueryResp
  )

  def messageTypeToInt(mt: RiakMessageType): Int = {
    values.keys.toList(values.values.toList.indexOf(mt))
  }
  def intToMessageType(int: Int): RiakMessageType = values(int)
}