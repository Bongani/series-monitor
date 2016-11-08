import java.util

import akka.actor.{ActorRef, Actor}
import domain.{Calculations, StreamData, TimeValueObject}

class CalculateActor(stream2Actor: ActorRef)  extends Actor {

  override def preStart() = {
    println("")
    println("=== CalculateActor Actor is starting up ===")
    println("")
  }

  override def receive: Receive = {
    case dataList: List[TimeValueObject] => {
      if (dataList.size > 0) {
        val sortedList = dataList.sortBy(r => r.time)
        val streamData = new StreamData(Calculations.slideAverage(sortedList), Calculations.quantizedTime(sortedList),
          Calculations.maximumTime(sortedList),Calculations.minimumTime(sortedList))
        stream2Actor ! streamData
      } else if (dataList.size == 0){
        val streamData = new StreamData(0, 0, 0,0)
        stream2Actor ! streamData
      }
    }
  }



}
