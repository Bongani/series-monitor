import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import domain.TimeValueObject
import net.jodah.expiringmap.{ExpirationPolicy, ExpiringMap}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object StreamingApp {

  def main(args: Array[String]) {

    val actorSystem = ActorSystem("SeriesMonitorSystem")
    val stream1Actor = actorSystem.actorOf(Props[Stream1Actor], name = "stream1Actor")
    val stream2Actor = actorSystem.actorOf(Props[Stream2Actor], name = "stream2Actor")

    val calcActor = actorSystem.actorOf(Props(new CalculateActor(stream2Actor)), name = "calculateActor")
    val numberStorageActor = actorSystem.actorOf(Props(new NumberStorageActor(calcActor)), name = "numberStorageActor")

    val stream0Actor = actorSystem.actorOf(Props(new Stream0Actor(stream1Actor, numberStorageActor)), name = "stream0Actor")
    val numberGenerator = actorSystem.actorOf(Props(new NumberGenerator(stream0Actor)), name = "numberGenerator")
    val triggerCalcActor = actorSystem.actorOf(Props(new TriggerCalcActor(numberStorageActor)), name = "triggerCalcActor")

    scala.io.StdIn.readLine("Press Enter to terminate the application...")
    println("Shutting down")
    actorSystem.terminate()
  }

}
