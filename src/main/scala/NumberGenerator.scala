import akka.actor.{ ActorRef, Actor }
import akka.actor.Actor.Receive

/**
 * generates the random numbers
 */
class NumberGenerator(stream0Ref: ActorRef) extends Actor {

  val random = scala.util.Random

  override def preStart() = {
    println("")
    println("=== NumberGenerator Actor is starting up ===")
    println("")
    val timestamp: Long = System.currentTimeMillis()
    random.setSeed(timestamp)
    val numberToGetStarted = numberGenerator()
    self ! numberToGetStarted
  }

  override def receive: Receive = {
    case number: Int => {
      val generatedNumber = numberGenerator()
      stream0Ref ! generatedNumber
      self ! generatedNumber
    }
  }

  private def numberGenerator(): Int = {
    val number = (random.nextFloat() * (math.pow(10, exponentialOperation()))).toInt
    val sleepTime = math.abs(random.nextInt())
    java.util.concurrent.TimeUnit.NANOSECONDS.sleep(sleepTime)
    return number
  }

  private def exponentialOperation(): Float ={
    return 1 + (random.nextFloat() * 4)
  }
}
