package butterfly.conflict

trait RiakTimestamped {
  def timeUpdated: Long
}

abstract class SiblingResolver[T] {
  def resolver(t1: T, t2: T): T

  implicit def resolve(siblings: List[T]): T = {
    siblings.reduce(resolver)
  }
}