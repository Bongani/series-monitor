import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import domain.TimeValueObject
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object StreamingApp {

  var stream2Actor: ActorRef = null;

  def main(args: Array[String]) {

    val actorSystem = ActorSystem("SeriesMonitorSystem")
    stream2Actor = actorSystem.actorOf(Props[Stream2Actor], name = "stream2Actor")

    val driverPort = 7777
    val driverHost = "localhost"

    //spark
    val ssc: StreamingContext = buildSparkStreamingContext(driverPort, driverHost)
    val actorName = "SparkStreamActor"
    // This is the integration point (from Spark's side) between Spark Streaming and Akka system
    // It's expected that the actor we're now instantiating will `store` messages (to close the integration loop)
    val sparkStreamActor= ssc.actorStream[String](Props[SparkStreamActor], actorName)
    // describe the computation on the input stream as a series of higher-level transformations
    //sparkStreamActor.reduce(_ + " " + _).print()
    //sparkStreamActor.print()
    performOperationsOnSparkStream(sparkStreamActor)
    // start the streaming context so the data can be processed
    // and the actor gets started
    ssc.start()
    // FIXME wish I knew a better way to handle the asynchrony
    java.util.concurrent.TimeUnit.SECONDS.sleep(3)
    val sparkActorSystem = SparkEnv.get.actorSystem
    val sparkUrl = s"akka.tcp://sparkDriver@$driverHost:$driverPort/user/Supervisor0/$actorName"
    val sparkActorRef = sparkActorSystem.actorSelection(sparkUrl)


    val stream1Actor = actorSystem.actorOf(Props[Stream1Actor], name = "stream1Actor")
    val stream0Actor = actorSystem.actorOf(Props(new Stream0Actor(stream1Actor, sparkActorRef)), name = "stream0Actor")
    val numberGenerator = actorSystem.actorOf(Props(new NumberGenerator(stream0Actor)), name = "numberGenerator")

    scala.io.StdIn.readLine("Press Enter to stop Spark Streaming context and the application...")
    ssc.stop(stopSparkContext = true, stopGracefully = true)

    actorSystem.scheduler.scheduleOnce(4 seconds) {
      println("shutdown")
      //actorSystem.terminate()
    }
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

  private def performOperationsOnSparkStream(actorStream: ReceiverInputDStream[String]) {
    val hello = "hello"
    val windowStream = actorStream.window(Seconds(10), Seconds(1))
    val transformedStream = windowStream.map(line => {
      Array(line.split(" ")(0).toLong, line.split(" ")(1).toLong)
    }) //first in array is the value, second in array is the time


    windowStream.transform( rdd =>  rdd.map(line => line.split(" ")))

    val smallest: DStream[Double]  = transformedStream.reduce((a,b) => {
      Array(0, math.min(a(1), b(1)))
    }).map(u => u(1).toDouble)

    val biggest  = transformedStream.reduce((a,b) => {
      Array(0, math.max(a(1), b(1)))
    }).map(u => u(1).toDouble)

    val averageOfMean = transformedStream.reduce((a, b) => Array( (a(0) + b(0))/2 )).
      map(u => u(0).toDouble)

    //smallest.transformWith(biggest, (rdd1, rdd2: RDD[Double]) => rdd1.)

    smallest.print()

    //averageOfMean.print()
    //averageOfMean.foreachRDD( rdd =>
    //rdd.foreach(record => shootMessage(record) )
    //)

    //val quantTime = transformedStream.reduce((a, b) =>
    //Array( (b(1) - a(1))*b(0) )).map(u => u(0).toDouble)
    //quantTime.print()

  }

  def shootMessage(record: Double){
    stream2Actor ! record
  }

  def transformStreamInput(stream: String): TimeValueObject = {
    val splitStream = stream.split(" ")
    return new TimeValueObject(splitStream(0).toInt, splitStream(1).toLong)
  }
}
