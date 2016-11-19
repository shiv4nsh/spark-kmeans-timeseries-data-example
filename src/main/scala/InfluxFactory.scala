/**
  * Created by shivansh on 18/11/16.
  */

import java.text.SimpleDateFormat

import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.spark._
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.util.Try

object InfluxFactory {

  import ApplicationContext._

  def main(args: Array[String]) = {
    saveVibrationData
    savePressureData
    sc.stop()
  }

  def savePressureData = {
    val pressureRead: RDD[String] = sc.textFile("src/main/resources/PamarcoPressure.txt")
    implicit val params = ReactiveInfluxDbName("chirpanywhere")
    implicit val awaitAtMost = 10.second
    processPressureData(pressureRead).saveToInflux()
  }

  def processPressureData(pressureRead: RDD[String]) = {
    pressureRead.map(_.split(",")).map { row =>
      val date = getDate(row(0).replaceAll("TimeStamp:", ""))
      val pressure: Int = row(1).split(":")(1).replaceAll("'", "").toInt
      val point = Try(Point(
        time = date, measurement = "Pamarco",
        tags = Map("typeOfData" -> "Pressure"),
        fields = Map("feature" -> pressure))).toOption
      point
    }.filter(_.isDefined).map(_.get)
  }

  def getDate(dateAsString: String) = new DateTime(new SimpleDateFormat("dd/MM/yy hh:mm:ss:SSSSSSS").parse(dateAsString).getTime)

  def saveVibrationData = {
    val vibrationText = sc.textFile("src/main/resources/PamarcoVibration.txt")
    val vibrationRDD = vibrationText.map(_.split("\\s++")).persist()
    implicit val params = ReactiveInfluxDbName("chirpanywhere")
    implicit val awaitAtMost = 10.second
    val vibrationVector = vibrationRDD.map { row =>
      val date = getDate(row(0) + " " + row(1))
      val point = Try(Point(
        time = date, measurement = "Pamarco",
        tags = Map("typeOfData" -> "Vibration"),
        fields = Map("featureOne" -> row(2).toDouble, "featureTwo" -> row(3).toDouble, "featureThree" -> row(4).toDouble))).toOption
      point
    }.filter(_.isDefined).map(_.get)
    vibrationVector.saveToInflux()
  }
}
