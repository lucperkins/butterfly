package butterfly.datatypes

case class MapField[T <: DataType](name: String, value: T)

class RiakMap(fields: List[MapField]) extends DataType {

}

class MapUpdate(removes: List[MapField]) {

}
