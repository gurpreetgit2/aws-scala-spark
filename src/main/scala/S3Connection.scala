import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object S3Connection {

  def init(spark: SparkSession, region: String): Unit = {
    val hadoopConf = spark.sparkContext.hadoopConfiguration

    hadoopConf.set(
      "fs.s3a.impl",
      "org.apache.hadoop.fs.s3a.S3AFileSystem"
    )

    hadoopConf.set(
      "fs.s3a.aws.credentials.provider",
      "com.amazonaws.auth.EnvironmentVariableCredentialsProvider"
    )

    hadoopConf.set("fs.s3a.endpoint.region", region)
  }

  private def readCsv(spark: SparkSession, path: String): DataFrame =
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path)

  def writeDataframeAsParquet(
      spark: SparkSession,
      dataframe: DataFrame
  ): Unit = {

    // complete this - write parquet as single unit
  }

  def readCustomers(spark: SparkSession, bucket: String): DataFrame =
    readCsv(spark, s"s3a://$bucket/input/customer_data.csv")

  def readAccounts(spark: SparkSession, bucket: String): DataFrame =
    readCsv(spark, s"s3a://$bucket/input/account_data.csv")

  def writeDatasetAsParquet[T](
      spark: SparkSession,
      bucket: String,
      ds: Dataset[T]
  ): Unit = {
    val path = s"s3a://$bucket/output/customer_account_data"

    ds.write
      .mode("overwrite")
      .parquet(path)

    println(s"Data written to $path")
  }
}
