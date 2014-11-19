package butterfly

import java.net.InetSocketAddress

import akka.actor.Actor
import akka.util.ByteString
import butterfly.props.{StoreProperties, FetchProperties}

object Commands {
  case class Get(bucket: String, key: String, bucketType: String, props: FetchProperties)
  case class Put(bucket: String, key: String, bucketType: String, value: ByteString, props: StoreProperties)
  case class Delete(bucket: String, key: String, bucketType: String)

  case class ListBuckets(bucketType: String)
  case class ListKeys(bucketType: String, bucket: String)
}

object RiakClient {
  def apply(host: String = "localhost", port: Int = 8087): RiakClient =
    new RiakClient(host, port)
}

class RiakClient(host: String, port: Int) extends Actor {
  import Commands._

  val address = new InetSocketAddress(host, port)

  def receive: Receive = {
    case Get(bucket, key, props, bucketType) =>
      println("GET request")
    case Put(bucket, key, bucketType, value, props) =>
      println("PUT request")
    case Delete(bucket, key, bucketType) =>
      println("DELETE request")
    case ListBuckets(bucketType) =>
      println("LIST_BUCKETS request")
    case ListKeys(bucketType, bucket) =>
      println("LIST_KEYS request")
  }
}