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

      // Step 1: Read customer data from S3
      val customerDf =
        S3Connection.readCustomers(spark, bucket, region)

      // Step 2: Read account data from the local CSV
      val accountDf =
        S3Connection.readAccounts(spark, bucket, region)

      // Step 3: Process and join the datasets
      val result = DataProcessor.process(
        spark,
        customerDf,
        accountDf
      )

      // Step 4: Display the final output
      result.show(10, truncate = false)

      println(s"Rows in final output: ${result.count()}")

    } finally {
      // Step 5: Stop Spark
      spark.stop()
    }
  }
}
