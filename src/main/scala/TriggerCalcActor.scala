import akka.actor.{ActorRef, Actor}

class TriggerCalcActor(dataStorageActor: ActorRef) extends Actor {

  override def preStart() = {
    println("")
    println("=== TriggerCalcActor Actor is starting up ===")
    println("")
    self ! "trigger"
  }

  override def receive: Receive = {
    case message: String => {
      pause()
      dataStorageActor ! message
      self ! message
    }
  }

  private def pause() {
    java.util.concurrent.TimeUnit.SECONDS.sleep(1);
  }

}
