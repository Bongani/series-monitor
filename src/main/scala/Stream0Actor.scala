import akka.actor.{ ActorRef, Actor }
import akka.actor.Actor.Receive
import domain.TimeValueObject

/**
 * Akka actor representing stream 0. The stream will receive the message and send to the respective actors for calculations
 * of stream and stream 2
 */
class Stream0Actor(stream1Ref: ActorRef) extends Actor {

  override def preStart() = {
    println("")
    println("=== Stream0 Actor is starting up ===")
    println("")
  }

  override def receive: Receive = {
    case number: Int => {
      val timestamp: Long = System.currentTimeMillis()
      stream1Ref ! number
      val timeValue = new TimeValueObject(timestamp, number)
      //stream2Ref ! timeValue
    }
  }
}