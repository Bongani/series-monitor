import akka.actor.{ ActorSystem, Actor, Props }
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.receiver._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object StreamingApp {

  def main(args: Array[String]) {

    val actorSystem = ActorSystem("SeriesMonitorSystem")
    val stream1Actor = actorSystem.actorOf(Props[Stream1Actor], name = "stream1Actor")
    val stream0Actor = actorSystem.actorOf(Props(new Stream0Actor(stream1Actor)), name = "stream0Actor")
    val numberGenerator = actorSystem.actorOf(Props(new NumberGenerator(stream0Actor)), name = "numberGenerator")

    val caanc = actorSystem.scheduler.scheduleOnce(20 seconds) {
      println("shutdown")
      actorSystem.terminate()
    }

    /*val driverPort = 7777
    val driverHost = "localhost"

    val ssc: StreamingContext = buildSparkStreamingContext(driverPort, driverHost)

    val actorName = "helloer"

    // This is the integration point (from Spark's side) between Spark Streaming and Akka system
    // It's expected that the actor we're now instantiating will `store` messages (to close the integration loop)
    val actorStream = ssc.actorStream[String](Props[SparkStreamActor], actorName)

    // describe the computation on the input stream as a series of higher-level transformations
    actorStream.reduce(_ + " " + _).print()

    // start the streaming context so the data can be processed
    // and the actor gets started
    ssc.start()

    // FIXME wish I knew a better way to handle the asynchrony
    java.util.concurrent.TimeUnit.SECONDS.sleep(3)

    val actorSystem = SparkEnv.get.actorSystem

    val url = s"akka.tcp://sparkDriver@$driverHost:$driverPort/user/Supervisor0/$actorName"
    val helloer = actorSystem.actorSelection(url)
    helloer ! "Hello"
    helloer ! "from"
    helloer ! "Spark Streaming"
    helloer ! "with"
    helloer ! "Scala"
    helloer ! "and"
    helloer ! "Akka"

    scala.io.StdIn.readLine("Press Enter to stop Spark Streaming context and the application...")
    //send poison pill
    ssc.stop(stopSparkContext = true, stopGracefully = true)*/
  }

  /**
   * Used to configure Spark for the application and create the the streaming context
   */
  private def buildSparkStreamingContext(driverPort: Int, driverHost: String): StreamingContext = {
    val sparkConfiguration = new SparkConf(false) // skip loading external settings
      .setMaster("local[*]") // run locally with as many threads as CPUs
      .setAppName("Spark Streaming with Scala and Akka") // name in web UI
      .set("spark.logConf", "true")
      .set("spark.driver.port", driverPort.toString)
      .set("spark.driver.host", driverHost)
      .set("spark.akka.logLifecycleEvents", "true")
    return new StreamingContext(sparkConfiguration, Seconds(1))
  }

  //private def mastermessaging()
}
