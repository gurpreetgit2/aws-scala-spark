import org.apache.spark.sql.SparkSession

case class CustomerData(
    customerId: String,
    forename: String,
    surname: String
)

case class CustomerModel(
    customerId: String,
    forename: String,
    surname: String,
    fullname: String
)
case class AccountsData(
    customerId: String,
    accountId: String,
    balance: Long
)

case class CustomerAccountData(
    customerName: String,
    customerId: String,
    customerAccountNumber: String,
    customerBalance: Long
)

object PracticeCode {

  def main(args: Array[String]): Unit = {

    // Create a spark context, using a local master so Spark runs on the local machine
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("TestCode")
      .getOrCreate()

    // importing spark implicits allows functions such as dataframe.as[T]
    import spark.implicits._

    val customerData = spark.read
      .option("header", "true")
      .csv("src/main/resources/customer_data.csv")
      .as[CustomerData]

    customerData.show(5, truncate = false)

// transformation 1 - added full name
    val customerModel = customerData
      .map(customer =>
        CustomerModel(
          customerId = customer.customerId,
          forename = customer.forename,
          surname = customer.surname,
          fullname = customer.forename + " " + customer.surname + " Atwal"
        )
      )

    customerModel.show(5, truncate = false)

// join CustomerModel with Accounts data

    val accountData = spark.read
      .option("header", "true")
      .csv("src/main/resources/account_data.csv")
      .withColumn("balance", $"balance".cast("Long"))
      .as[AccountsData]

    val customerAccountJoined =
      customerModel.joinWith(
        accountData,
        customerModel("customerId") === accountData("customerId"),
        "inner"
      )

    customerAccountJoined.show(5, truncate = false)

    val customerAccountData = customerAccountJoined.map {
      case (customer, account) =>
        CustomerAccountData(
          customerName = customer.fullname,
          customerId = customer.customerId,
          customerAccountNumber = account.accountId,
          customerBalance = account.balance
        )
    }

    customerAccountData.show(5, truncate = false)

//   customerModel.write
//     .mode("overwrite")
//     .parquet("src/main/resources/customerModel.parquet")
  }
}
