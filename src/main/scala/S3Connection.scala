import org.apache.spark.sql.{DataFrame, SparkSession}

object S3Connection {

  def readCustomers(
      spark: SparkSession,
      bucket: String,
      region: String
  ): DataFrame = {

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

    val path = s"s3a://$bucket/input/customer_data.csv"

    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path)
  }

  def readAccounts(
      spark: SparkSession,
      bucket: String,
      region: String
  ): DataFrame = {

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

    val path = s"s3a://$bucket/input/account_data.csv"

    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path)
  }
}
