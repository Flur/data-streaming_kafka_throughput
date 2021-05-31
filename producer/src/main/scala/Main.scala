import java.util.Properties
import org.apache.kafka.clients.producer._
import scala.io.Source

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import common.{ Subreddit }

object Main {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  def main(args: Array[String]): Unit = {
    writeToKafkaFromFile("quick-start", "small_reddit_subreddit_data.ndjson")
  }

  def writeToKafkaFromFile(topic: String, path: String): Unit = {
    val source = Source.fromFile(path)

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    for (line <- source.getLines()) {
      val subreddit = read[Subreddit](line)

      val record = new ProducerRecord[String, String](topic, "key", write(subreddit))
      producer.send(record)
    }

    producer.close()
    source.close()
  }
}