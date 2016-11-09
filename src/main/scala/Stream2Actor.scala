import java.io.{ File, PrintWriter }

import akka.actor.Actor
import akka.actor.Actor.Receive
import domain.StreamData

/**
 * Actor representing data that will be outputted for stream 2
 */
class Stream2Actor extends Actor {

  val printWriter = new PrintWriter(new File("stream2.txt"));
  val timeWriter = new PrintWriter(new File("stream2_time.txt"));

  override def preStart() = {
    println("")
    println("=== Stream2 Actor is starting up ===")
    println("")
  }

  override def postStop() = {
    println("")
    println("=== Stream2 Actor is shutting down ===")
    println("")
    printWriter.close;
  }

  override def receive: Receive = {
    case data: StreamData => {
      writeToStream2(data)
      writeToStream2Time(data)
    }
  }

  private def round(value: Double): String = {
    return BigDecimal(value).setScale(1).toString()
  }

  private def writeToStream2(data: StreamData){
    val computedString = data.average + " " + data.quantizedTime +
      " " + data.oldestAge.toString + " " + data.youngestAge.toString
    printWriter.write(computedString + "\n")
    printWriter.flush()
  }

  private def writeToStream2Time(data: StreamData){
    val endTimeStamp: Long = System.nanoTime()
    timeWriter.write(data.youngestAge.toString + " " + endTimeStamp.toString + "\n")
    timeWriter.flush()
  }
}
