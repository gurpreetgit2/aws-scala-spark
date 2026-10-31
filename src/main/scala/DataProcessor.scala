import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.col

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

object DataProcessor {

  def process(
      spark: SparkSession,
      customerDf: DataFrame,
      accountDf: DataFrame
  ): Dataset[CustomerAccountData] = {

    import spark.implicits._

    // Convert customer DataFrame into CustomerData objects
    val customerData = customerDf.as[CustomerData]

    // Transformation 1: Add full name
    val customerModel = customerData.map { customer =>
      CustomerModel(
        customerId = customer.customerId,
        forename = customer.forename,
        surname = customer.surname,
        fullname = customer.forename + " " + customer.surname + " Atwal"
      )
    }

    // Convert account DataFrame into AccountsData objects
    val accountData = accountDf
      .withColumn("balance", col("balance").cast("Long"))
      .as[AccountsData]

    // Join customers with accounts
    val customerAccountJoined = customerModel.joinWith(
      accountData,
      customerModel("customerId") === accountData("customerId"),
      "inner"
    )

    // Transformation 2: Create the final output model
    val customerAccountData = customerAccountJoined.map {
      case (customer, account) =>
        CustomerAccountData(
          customerName = customer.fullname,
          customerId = customer.customerId,
          customerAccountNumber = account.accountId,
          customerBalance = account.balance
        )
    }

    customerAccountData
  }
}
