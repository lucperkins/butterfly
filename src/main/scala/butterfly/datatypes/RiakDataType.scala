package butterfly.datatypes

import scala.concurrent.Future

trait RiakDataType[T] {
  def reload(bucket: String, key: String, bucketType: String): Option[T]

  def store(bucket: String, key: String, bucketType: String): Future[Boolean]
}