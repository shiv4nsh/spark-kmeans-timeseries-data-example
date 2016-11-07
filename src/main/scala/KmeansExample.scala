import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}

import scala.util.Try


/**
  * Created by shivansh on 6/11/16.
  */
object KmeansExample {
  val conf = new SparkConf().setAppName(s"LDAExample").setMaster("local[*]").set("spark.executor.memory", "2g")
  val spark = SparkSession.builder().config(conf).getOrCreate()
  val sc = spark.sparkContext

  def main(args: Array[String]) {

    val pressureRead = sc.textFile("src/main/resources/PamarcoPressure.txt")
    val vibrationText = sc.textFile("src/main/resources/PamarcoVibration.txt")

    val pressureRDD = pressureRead.map(_.split(","))
    val vibrationRDD = vibrationText.map(_.split("\t"))

    val vibrationVector = vibrationRDD.map { row =>
      Try(Vectors.dense(row(1).toDouble, row(2).toDouble, row(3).toDouble)).toOption
    }.filter(_.isDefined).map(_.get)
    val splittedRDD = vibrationVector.randomSplit(Array(0.6, 0.4))
    val trainRDD = splittedRDD(0)
    val testRDD = splittedRDD(1)
    // Cluster the data into two classes using KMeans
    val numClusters = 2
    val numIterations = 20
    val clusters = KMeans.train(trainRDD, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(vibrationVector)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    // Save and load model
    clusters.save(sc, "target/org/apache/spark/KMeansExample/KMeansModel")
    val sameModel = KMeansModel.load(sc, "target/org/apache/spark/KMeansExample/KMeansModel")

    import spark.implicits._
    val foo = sameModel.predict(testRDD)
    foo.toDF.show
  }
}
