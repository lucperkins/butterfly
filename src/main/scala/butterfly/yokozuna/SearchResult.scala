package butterfly.yokozuna

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

import butterfly.core.{RiakConverter, RiakMessageBuilder, RiakMessageType, RiakRequest}
import butterfly.requests.PolyRequests
import com.basho.riak.protobuf.RiakSearchPB.RpbSearchQueryResp

case class SearchField(key: String, value: String)

case class SearchDoc(bucket: String, bucketType: String, key: String, id: String, value: String)

case class SearchResult(docs: List[SearchDoc], maxScore: Float, numFound: Int) {
  override def toString: String = {
    val sb = new StringBuilder
    sb.append("\n")
    sb.append("Search results:\n")
    sb.append(s"Max score: $maxScore\n")
    sb.append(s"Number found: $numFound\n")
    sb.append("======================\n")
    docs.map(doc => sb.append("  Result\n" +
      "  ------\n" +
      s"  Location: /${doc.bucketType}/${doc.bucket}/${doc.key}\n" +
      s"  Value: ${doc.value}\n"))
    sb.toString()
  }
}

trait SearchRequests extends RiakRequest with PolyRequests with RiakConverter {
  def runSearchQuery(index: String, query: String): Future[RpbSearchQueryResp] = {
    val msg = RiakMessageBuilder.Search(index, query)
    val req = buildRequest(RiakMessageType.RpbSearchQueryReq, msg)
    req map { resp =>
      RpbSearchQueryResp.parseFrom(resp.message) match {
        case r: RpbSearchQueryResp => r
        case _ => throw new Exception("Something went wrong")
      }
    }
  }

  def search(index: String, query: String): Future[Option[SearchResult]] = {
    runSearchQuery(index, query) map {
      case resp: RpbSearchQueryResp =>
        val docsBuffer = new ListBuffer[SearchDoc]
        val rawDocsList = resp.getDocsList.asScala

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

  def resultToLocationsList(result: SearchResult): List[Location] = {
    val locList = new ListBuffer[Location]
    result.docs.map(doc => {
      locList += new Location(doc.bucket, doc.key, doc.bucketType)
    })
    locList.toList
  }

  def searchAndReturnObjects[T](index: String, query: String): Future[Option[List[T]]] = {
    search(index, query) map {
      case Some(result) =>
        val locations = resultToLocationsList(result)
        getMany[T](locations) map (x => x)
      case None =>
        None
    }
  }
}