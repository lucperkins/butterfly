package butterfly.conflict

trait RiakTimestamped {
  def timeUpdated: Long
}

abstract class SiblingResolver[T] {
  def resolve(siblings: List[T]): T

  def resolveByTimestamp(siblings: List[RiakTimestamped]): RiakTimestamped = {
    def moreRecent(a: RiakTimestamped, b: RiakTimestamped): RiakTimestamped = {
      if (a.timeUpdated > b.timeUpdated) a else b
    }
    siblings.reduce(moreRecent)
  }

  def eliminate(siblings: List[T], eliminator: T => Boolean): List[T] = {
    siblings.filter(eliminator)
  }

  def eliminateAndResolve(siblings: List[T], eliminator: T => Boolean) = {
    resolve(eliminate(siblings, eliminator))
  }
}