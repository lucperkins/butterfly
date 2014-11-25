package butterfly.requests

/*
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

trait SearchRequests extends RiakRequest with RiakConverter {
  def runSearchQuery(index: String, query: String): Future[RpbSearchQueryResp] = {
    val msg = RiakMessageBuilder.searchRequest(index, query)
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

  /*
  def searchAndRetrieveObjects[T(index: String, query: String): Future[Option[List[Any]]] = {
    Future(Some(new ListBuffer[T]().toList))
  }
  */
}
*/