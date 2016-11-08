import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import domain.TimeValueObject
import net.jodah.expiringmap.{ExpirationPolicy, ExpiringMap}

import scala.collection.mutable.ListBuffer

class NumberStorageActor(calcActor: ActorRef) extends Actor {

  val expiringMap: ExpiringMap[String, TimeValueObject] = ExpiringMap.builder()
    .expirationPolicy(ExpirationPolicy.CREATED)
    .expiration(10, TimeUnit.SECONDS)
    .build();

  var keyTrackingList = new ListBuffer[Long]

  override def preStart() = {
    println("")
    println("=== CalculateActor Actor is starting up ===")
    println("")
  }

  override def receive: Receive = {
    case message: String => {
      //trigger calc request
      calcActor ! buildList()
    }
    case timeValueObject: TimeValueObject => {
      addToMap(timeValueObject)
    }
  }

  private def buildList(): List[TimeValueObject] ={
    var timeValueList: List[TimeValueObject] = List()
    val tempKeyTrackingList = keyTrackingList

    for (key <- tempKeyTrackingList){
      val timeValueObject = expiringMap.get(key.toString)
      if (timeValueObject != null){
        timeValueList = timeValueObject :: timeValueList
      } else {
        keyTrackingList -= key
      }
    }

    return timeValueList
  }

  private def addToMap(timeValueObject: TimeValueObject){
    keyTrackingList += timeValueObject.time
    expiringMap.put(timeValueObject.time.toString, timeValueObject)
  }

}
