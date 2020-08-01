object SAMExample {
  def main(args: Array[String]): Unit = {

    //    val ufo = (m: Int) => println(s" flies $m miles!")
    //    ufo.fly(123)   this wont work as ufo type will be (int => unit)

    val ufo: Flyable = (m: Int) => println(s" flies $m , $m , miles!")  //
    ufo.fly(123)
  }
}

//Trait with only one method can be used as concrete object for
// here Int => Unit will be associated to fly method automatically
trait Flyable {
  def fly(miles: Int): Unit
}

