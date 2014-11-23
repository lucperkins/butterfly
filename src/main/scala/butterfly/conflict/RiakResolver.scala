package butterfly.conflict

abstract class RiakResolver[T] {
  implicit def resolve(siblings: List[T]): T
}