import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by shivansh on 18/11/16.
  */
object ApplicationContext {
  val conf = new SparkConf().setAppName(s"Pamarco").setMaster("local[*]").set("spark.executor.memory", "2g")
  val spark = SparkSession.builder().config(conf).getOrCreate()
  val sc = spark.sparkContext

}
