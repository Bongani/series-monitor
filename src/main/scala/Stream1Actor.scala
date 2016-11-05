import java.io.{ File, FileOutputStream, PrintWriter }

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * Stream 1 actor that writes to file
 */
class Stream1Actor extends Actor {

  val printWriter = new PrintWriter(new File("stream1.txt"));

  override def preStart() = {
    println("")
    println("=== Stream1 Actor is starting up ===")
    println("")
  }

  override def postStop() = {
    println("")
    println("=== Stream1 Actor is shutting down ===")
    println("")
    printWriter.close;
  }

  override def receive: Receive = {
    case number: Int =>
      printWriter.write(number + "\n")
  }
}
