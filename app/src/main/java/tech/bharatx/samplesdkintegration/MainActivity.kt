package tech.bharatx.samplesdkintegration

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tech.bharatx.alternatedata.AlternateDataManager
import tech.bharatx.common.BharatXCommonUtilManager
import tech.bharatx.common.CreditAccessManager
import tech.bharatx.startup.BharatXStartupTierManager

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // testing credentials
    BharatXStartupTierManager.initialize(
        this,
        "testPartnerId",
        "testApiKey",
        Color.parseColor("#4892CB") // This is the same color as BharatX's default theme color
    )

    // enable BharatX credit notifications
    CreditAccessManager.register(this@MainActivity)

    bharatx_pay_button.setOnClickListener {
      // confirm whether user wants to go ahead with the transaction
      BharatXCommonUtilManager.confirmTransactionWithUser(
          this,
          10000, // ask user for confirmation for 100 rupees
          object : BharatXCommonUtilManager.TransactionConfirmationListener {

            override fun onUserConfirmedTransaction() {
              Toast.makeText(this@MainActivity, "Transaction confirmed", Toast.LENGTH_LONG).show()
              startBharatXTransaction()
            }

            override fun onUserAcceptedPrivacyPolicy() {
              AlternateDataManager.register(this@MainActivity)
            }

            override fun onUserCancelledTransaction() {
              Toast.makeText(this@MainActivity, "Transaction cancelled", Toast.LENGTH_LONG).show()
            }
          })
    }
  }

  private fun startBharatXTransaction() {
    BharatXCommonUtilManager.showBharatXProgressDialog(this)
    var transactionId = ""
    Handler(Looper.getMainLooper())
        .postDelayed(
        {
          transactionId = "123" // test transaction id - obtain from server
          CreditAccessManager.registerTransactionId(
              this,
              transactionId,
              object : CreditAccessManager.RegisterTransactionListener {
                override fun onRegistered() {
                  // simulate processing
                  Handler(Looper.getMainLooper())
                      .postDelayed(
                      {
                        // our transaction succeeded!
                        val isTransactionSuccessful = true // obtain from server

                        BharatXCommonUtilManager.closeBharatXProgressDialog()
                        BharatXCommonUtilManager.showTransactionStatusDialog(
                            this@MainActivity,
                            isTransactionSuccessful,
                            object : BharatXCommonUtilManager.TransactionStatusShowListener {
                              override fun onStatusDialogClose() {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Thank you for your purchase. A jar of cookies for your web browser will arrive shortly :)",
                                    Toast.LENGTH_LONG)
                                    .show()
                              }
                            })
                      },
                      1000)
                }
                override fun onFailure() {}
              })
        },
        2000)
  }
}
