package butterfly.core

sealed trait RiakMessageType

object RiakMessageType {
  // Error
  case object RpbErrResp extends RiakMessageType
  // Ping
  case object RpbPingReq extends RiakMessageType
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
  // Bucket properties
  case object RpbGetBucketReq extends RiakMessageType
  case object RpbGetBucketResp extends RiakMessageType
  // Search
  case object RpbSearchQueryReq extends RiakMessageType
  case object RpbSearchQueryResp extends RiakMessageType
  // Riak Data Types
  case object DtFetchReq extends RiakMessageType
  case object DtFetchResp extends RiakMessageType
  case object DtUpdateReq extends RiakMessageType
  case object DtUpateResp extends RiakMessageType

  val values = Map(
    0 -> RpbErrResp,
    1 -> RpbPingReq,
    9 -> RpbGetReq,
    10 -> RpbGetResp,
    11 -> RpbPutReq,
    12 -> RpbPutResp,
    13 -> RpbDelReq,
    15 -> RpbListBucketsReq,
    16 -> RpbListBucketsResp,
    17 -> RpbListKeysReq,
    18 -> RpbListKeysResp,
    19 -> RpbGetBucketReq,
    20 -> RpbGetBucketResp,
    27 -> RpbSearchQueryReq,
    28 -> RpbSearchQueryResp,
    80 -> DtFetchReq,
    81 -> DtFetchResp,
    82 -> DtUpdateReq,
    83 -> DtUpateResp
  )

  def messageTypeToInt(mt: RiakMessageType): Int = {
    values.keys.toList(values.values.toList.indexOf(mt))
  }
  def intToMessageType(int: Int): RiakMessageType = values(int)
}