package butterfly.requests

import com.google.protobuf.ByteString

trait RiakObject[T] {
  def bucket: String
  def key: String
  def bucketType: String = {
    if (bucketType == null) "default" else bucketType
  }
  def contentType: String = "application/json"
  def valueConstructor(self: T): ByteString
}

case class Person(name: String) extends RiakObject[Person] {
  def bucket = "people"
  def key = name.toLowerCase
  def valueConstructor(p: Person): ByteString = {
    ByteString.copyFromUtf8(s"My name is $name")
  }
}