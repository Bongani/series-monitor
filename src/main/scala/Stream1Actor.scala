import java.io.{ File, FileOutputStream, PrintWriter }

import akka.actor.Actor
import akka.actor.Actor.Receive
import domain.TimeValueObject

/**
 * Stream 1 actor that writes to file
 */
class Stream1Actor extends Actor {

  val printWriter = new PrintWriter(new File("stream1.txt"));
  val timeWriter = new PrintWriter(new File("stream1_time.txt"));

  override def preStart() = {
    println("")
    println("=== Stream1 Actor is starting up ===")
    println("")
  }

  override def postStop() = {
    println("")
    println("=== Stream1 Actor is shutting down ===")
    println("")
    printWriter.close();
    timeWriter.close();
  }

  override def receive: Receive = {
    case number: Int =>
      writeToStream1(number)
    case timeValueObject: TimeValueObject =>
      writeToStream1(timeValueObject.value)
      writeToStream1Time(timeValueObject.time)
  }

  private def writeToStream1(number: Int){
    printWriter.write(number.toString + "\n")
    printWriter.flush()
  }

  private def writeToStream1Time(startTimeStamp: Long){
    val endTimeStamp: Long = System.nanoTime()
    timeWriter.write(startTimeStamp.toString + " " + endTimeStamp.toString + "\n")
    timeWriter.flush()
  }
}
