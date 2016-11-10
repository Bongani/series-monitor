import java.io.{File, PrintWriter}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import domain.TimeValueObject
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
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

  private def printObjectSizes(timeValueList: List[TimeValueObject]): Unit ={
    val sizeOfMap = ObjectSizeCalculator.getObjectSize(expiringMap)
    val sizeOfKeyTrackingList = ObjectSizeCalculator.getObjectSize(keyTrackingList)
    val sizeOfTimeValueList = ObjectSizeCalculator.getObjectSize(timeValueList)
    var sizeTimeValueObject = 0L
    if (!timeValueList.isEmpty){
      sizeTimeValueObject = ObjectSizeCalculator.getObjectSize(timeValueList(0))
    }
    val stringToWrite = sizeOfMap.toString  + " " + sizeOfKeyTrackingList.toString + " " +
      sizeOfTimeValueList.toString + " " + sizeTimeValueObject.toString
    //printWriter.write(stringToWrite + "\n")
    //printWriter.flush()
  }

}
