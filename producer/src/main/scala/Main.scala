import java.util.Properties
import org.apache.kafka.clients.producer._
import scala.io.Source

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import common.{ Subreddit }
import scala.reflect.io.Directory
import java.io.File

object Main {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  def main(args: Array[String]): Unit = {

    // delete folder with timestamps before write
    val directory = new Directory(new File("./timestamps"))
    directory.deleteRecursively()

    // and create new empty directory
    directory.createDirectory()

    writeToKafkaFromFile("quick-start", "small_reddit_subreddit_data.ndjson")
  }

  def writeToKafkaFromFile(topic: String, path: String): Unit = {
    val source = Source.fromFile(path)

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    println(s"Partitions in topic ${topic} is - ${producer.partitionsFor(topic).size}")

    for (line <- source.getLines()) {
      val subreddit = read[Subreddit](line)

      // todo use kafka serde
      val record = new ProducerRecord[String, String](topic, write(subreddit))
      producer.send(record)
    }

    producer.close()
    source.close()
  }
}