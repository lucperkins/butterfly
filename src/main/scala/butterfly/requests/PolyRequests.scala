package butterfly.requests

import butterfly.RiakRequest
import butterfly.yokozuna.Location
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

trait PolyRequests extends RiakRequest with KVRequests {
  def getMany[T](bucket: String, keys: List[String])
                (implicit resolver: SiblingResolver[T]): Option[List[T]] = {
    val tList = new ListBuffer[T]
    keys.map(key => {
      get[T](bucket, key) map {
        case Some(t: T) => tList += t
        case Some(_)    => throw new Exception("Mismatched types on multi-get")
        case None       => throw new Exception("Multi-get error")
      }
    })
    Some(tList.toList)
  }

  def getMany[T](locations: List[Location])
                (implicit resolver: SiblingResolver[T]): Option[List[T]] = {
    val tList = new ListBuffer[T]
    locations.map(loc => {
      get[T](loc.bucket, loc.key, loc.bucketType) map {
        case Some(t: T) => tList += t
        case Some(_)    =>
        case None       =>
      }
    })
    Some(tList.toList)
  }
}