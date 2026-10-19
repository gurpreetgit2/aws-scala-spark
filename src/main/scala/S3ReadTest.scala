import org.apache.spark.sql.SparkSession

object S3ReadTest {
  def main(args: Array[String]): Unit = {
    require(
      args.length == 2,
      "Usage: S3ReadTest <bucket_name> <region>"
    )

    val bucket = args(0)
    val region = args(1)

    val spark = SparkSession
      .builder()
      .appName("S3 Read Test")
      .master("local[*]")
      .getOrCreate()

    try {
      val hadoopConf = spark.sparkContext.hadoopConfiguration

      hadoopConf.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      hadoopConf.set(
        "fs.s3a.aws.credentials.provider",
        "com.amazonaws.auth.EnvironmentVariableCredentialsProvider"
      )
      hadoopConf.set("fs.s3a.endpoint.region", region)

      val path = s"s3a://$bucket/input/customer_data.csv"

      val customers = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(path)

      customers.show(10, truncate = false)
      println(s"Rows read: ${customers.count()}")
    } finally {
      spark.stop()
    }
  }
}
