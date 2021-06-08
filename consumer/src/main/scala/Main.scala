import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.{Calendar, Properties}
import scala.collection.JavaConverters._
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import common.Subreddit

import scala.util.Random


object Main {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  def main(args: Array[String]): Unit = {
    val fileName = Random.alphanumeric.take(10).mkString

    println(s"Will write to timestaps/${fileName}.txt")

    consumeFromKafka("quick-start", fileName)
  }

  def consumeFromKafka(topic: String, fileName: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "consumer-group")
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList(topic))

    while (true) {
      val record = consumer.poll(1).asScala
      for (data <- record.iterator) {
        val str = data.value()

        // Commented out as it will make big latency and same mbps for measuring throughput
        // Thread.sleep(1000)

        // todo use kafka serde
        val subreddit = read[Subreddit](str)
        val bytes = str.getBytes(StandardCharsets.UTF_8).length

        writeToFile(s"${subreddit.time_stamp} ${Calendar.getInstance.getTimeInMillis} ${bytes}", fileName)
      }
    }
  }

  def writeToFile(text: String, fileName: String): Unit = {
    Files.write(
      Paths.get(s"./timestamps/${fileName}.txt"),
      (text + System.lineSeparator).getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND)
  }
}