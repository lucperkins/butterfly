
class CRDTSpec extends ButterflySpec {
  "A Riak map" should {
    "be fetched successfully" in {
      node.getDataType("maps", "customers", "idris_elba") map {
        case Some(map) =>
          
      }
    }
  }
}
