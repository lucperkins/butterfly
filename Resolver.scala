abstract class SiblingResolver[T] {
  def resolver(t1: T, t2: T): T

  def resolve(siblings: List[T]): T = {
    siblings.reduce(resolver)
  }
}

object Main extends App {
  
}
