import akka.actor.Actor
import akka.actor.Actor.Receive
import org.apache.spark.streaming.receiver.ActorHelper

/**
 * Akka actor used to store messages for stream 2
 */
class SparkStreamActor extends Actor with ActorHelper {

  override def preStart() = {
    println("")
    println("=== Spark Stream Actor is starting up ===")
    println("")
  }

  override def receive = {
    // store() method allows us to store the message so Spark Streaming knows about it
    // This is the integration point (from Akka's side) between Spark Streaming and Akka
    case s => store(s)
  }
}
