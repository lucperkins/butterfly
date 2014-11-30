package butterfly.requests

import scala.collection.mutable.ListBuffer

import butterfly.core.RiakRequest
import butterfly.yokozuna.Location

trait PolyRequests extends RiakRequest with KVRequests {
  def getMany[T](bucket: String, keys: List[String])
                (implicit resolver: SiblingResolver[T]): List[T] = {
    for {
      key <- keys
      obj <- get[T](bucket, key) map {
        case Some(t: T) => t
        case _ =>
      }
    } yield obj
  }

  def getMany[T](locations: List[Location])
                (implicit resolver: SiblingResolver[T]): List[T] = {
    for {
      loc <- locations
      obj <- get[T](loc.bucket, loc.key, loc.bucketType) map {
        case Some(t: T) => t
        case _ =>
      }
    } yield obj
  }
}