import org.apache.spark.sql.SparkSession

object Main {

  def main(args: Array[String]): Unit = {

    require(
      args.length == 2,
      "Usage: Main <bucket_name> <region>"
    )

    val bucket = args(0)
    val region = args(1)

    val spark = SparkSession
      .builder()
      .appName("Customer Account Processing")
      .master("local[*]")
      .getOrCreate()

    try {

      // Step 1: Configure S3 connection
      S3Connection.init(spark, region)

      // Step 2: Read customer data from S3
      val customerDf =
        S3Connection.readCustomers(spark, bucket)

      // Step 3: Read account data from S3
      val accountDf =
        S3Connection.readAccounts(spark, bucket)

      // Step 4: Process and join the datasets
      val result = DataProcessor.process(
        spark,
        customerDf,
        accountDf
      )

      // Step 5: Display the final output
      result.show(10, truncate = false)

      println(s"Rows in final output: ${result.count()}")

      // Step 6: Write result to S3 as Parquet
      S3Connection.writeDatasetAsParquet(spark, bucket, result)

    } finally {
      // Step 6: Stop Spark
      spark.stop()
    }
  }
}
