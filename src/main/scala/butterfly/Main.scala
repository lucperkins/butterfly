package butterfly

case class Person(name: String, age: Int)

abstract class SiblingResolver[T] {
  def resolver(t1: T, t2: T): T

  def resolve(siblings: List[T]): T = siblings.reduce(resolver)
}

class PersonResolver extends SiblingResolver[Person] {
  def resolver(p1: Person, p2: Person): Person = {
    if (p1.age > p2.age) p1 else p2
  }
}

object Main extends App {
  val luc = Person("Luc", 32)
  val bill = Person("Bill", 97)
  val people = List(luc, bill)
  val resolver = new PersonResolver
  val person = resolver.resolve(people)
  System.out.println(person.age)
}