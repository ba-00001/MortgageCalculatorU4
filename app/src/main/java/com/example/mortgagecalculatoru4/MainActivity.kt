package com.example.mortgagecalculatoru4

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.example.mortgagecalculatoru4.model.Loan
import java.text.NumberFormat

@RequiresApi(api = Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    private var purchaseAmount = 0.0
    private var downPaymentAmount = 0.0
    private var interestRate = 1.0
    private var duration = 1
    private lateinit var textViewPurchasePrice: TextView
    private lateinit var textViewDownPaymentAmount: TextView
    private lateinit var textViewInterestRate: TextView
    private lateinit var textViewDuration: TextView
    private lateinit var calculate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewPurchasePrice = findViewById(R.id.textViewPurchasePrice)
        textViewDownPaymentAmount = findViewById(R.id.textViewDownPaymentAmount)
        textViewInterestRate = findViewById(R.id.textViewInterestRate)
        textViewDuration = findViewById(R.id.textViewDuration)

        val editTextPurchasePrice = findViewById<EditText>(R.id.editTextPurchasePrice)
        editTextPurchasePrice.addTextChangedListener(getEditableTextWatcher(textViewPurchasePrice, "PP"))

        val editTextDownPaymentAmount = findViewById<EditText>(R.id.editTextDownPaymentAmount)
        editTextDownPaymentAmount.addTextChangedListener(getEditableTextWatcher(textViewDownPaymentAmount, "DPA"))

        val editTextInterestRate = findViewById<EditText>(R.id.editTextInterestRate)
        editTextInterestRate.addTextChangedListener(getEditableTextWatcher(textViewInterestRate, "IR"))

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                duration = progress
                textViewDuration.text = progress.toString()
                if (progress <= 0) {
                    seekBar.progress = 1
                    textViewDuration.text = "1"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        calculate = findViewById(R.id.calculate)
        calculate.setOnClickListener {
            val loanAmount = purchaseAmount - downPaymentAmount
            val loan = Loan(interestRate, duration, loanAmount)
            val dialog = AppCompatDialog(this@MainActivity)
            dialog.setContentView(R.layout.dialog)
            dialog.setTitle("Result")
            val monthlyPayment = dialog.findViewById<TextView>(R.id.monthly_payment)
            monthlyPayment.text = currencyFormat.format(loan.getMonthlyPayment())
            val totalPayment = dialog.findViewById<TextView>(R.id.total_payment)
            totalPayment.text = currencyFormat.format(loan.getTotalPayment())

            // reset loan data
            loan.annualInterestRate = 0.0
            loan.numberOfYears = 1
            loan.loanAmount = 0.0

            val dialogButtonOk = dialog.findViewById<Button>(R.id.dialogButtonOK)
            dialogButtonOk.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    private fun getEditableTextWatcher(textView: TextView, type: String): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    val value = s.toString().toDouble() / 100.0
                    when (type) {
                        "PP" -> {
                            purchaseAmount = value
                            textView.text = currencyFormat.format(value)
                        }
                        "DPA" -> {
                            downPaymentAmount = value
                            textView.text = currencyFormat.format(value)
                        }
                        "IR" -> {
                            interestRate = value
                            textView.text = value.toString()
                        }
                    }
                } catch (e: NumberFormatException) {
                    textView.text = ""
                }
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }

    companion object {
        private val currencyFormat = NumberFormat.getCurrencyInstance()
    }
}
