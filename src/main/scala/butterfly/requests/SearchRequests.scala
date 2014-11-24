package butterfly.requests

import butterfly.{RiakException, RiakMessageType, RiakConverter, RiakRequest}
import com.basho.riak.protobuf.RiakKvPB.RpbIndexResp
import com.basho.riak.protobuf.RiakSearchPB.{RpbSearchDoc, RpbSearchQueryResp}
import com.google.protobuf.ByteString

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

case class SearchField(key: String, value: String)
case class SearchDoc(bucket: String, bucketType: String, key: String, id: String, value: String)
case class SearchResult(docs: List[SearchDoc], maxScore: Float, numFound: Int)

trait SearchRequests extends RiakRequest with RiakConverter {
  def runSearchQuery(index: String, query: String): Future[RpbSearchQueryResp] = {
    val msg = RiakMessageBuilder.searchRequest(query, index)
    val req = buildRequest(RiakMessageType.RpbSearchQueryReq, msg)
    req map { resp =>
      RpbSearchQueryResp.parseFrom(resp.message) match {
        case r: RpbSearchQueryResp => r
        case _ => throw new RiakException("Something went wrong")
      }
    }
  }

  def search(index: String, query: String): Future[Option[SearchResult]] = {
    runSearchQuery(index, query) map {
      case resp: RpbSearchQueryResp =>
        val docsBuffer = new ListBuffer[SearchDoc]
        val rawDocsList = resp.getDocsList

        rawDocsList.map(searchDoc => {
          val fieldsList = searchDoc.getFieldsList

          val bucket = fieldsList.get(1).getValue.toStringUtf8
          val bucketType = fieldsList.get(2).getValue.toStringUtf8
          val key = fieldsList.get(3).getValue.toStringUtf8
          val id = fieldsList.get(4).getValue.toStringUtf8
          val value = fieldsList.get(5).getValue.toStringUtf8
          val doc = new SearchDoc(bucket, bucketType, key, id, value)
          docsBuffer += doc
        })
        val searchResult = new SearchResult(docsBuffer.toList, resp.getMaxScore, resp.getDocsCount)
        Some(searchResult)
      case _ => None
    }
  }

  def searchAndRetrieveObjects(index: String, query: String): Future[Option[List[Any]]] = {
    Future(Some(new ListBuffer[T]().toList))
  }
}