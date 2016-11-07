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
      dataList.sortBy(r => (r.time))
      if (dataList.size > 0) {
        val streamData = new StreamData(Calculations.slideAverage(dataList), Calculations.quantizedTime(dataList),
          Calculations.maximumTime(dataList),Calculations.minimumTime(dataList))
        stream2Actor ! streamData
      } else if (dataList.size == 0){
        val streamData = new StreamData(0, 0, 0,0)
        stream2Actor ! streamData
      }
    }
  }



}
