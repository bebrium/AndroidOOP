package com.example.AndroidOOP1

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.res.ColorStateList

class Calculator : AppCompatActivity() {
    private lateinit var tvResult: TextView

    private var lastNumeric: Boolean = false


    private var isOperatorAdded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        tvResult = findViewById(R.id.tvResult)


        val btn0 = findViewById<Button>(R.id.btn_0)
        val btn1 = findViewById<Button>(R.id.btn_1)
        val btn2 = findViewById<Button>(R.id.btn_2)
        val btn3 = findViewById<Button>(R.id.btn_3)
        val btn4 = findViewById<Button>(R.id.btn_4)
        val btn5 = findViewById<Button>(R.id.btn_5)
        val btn6 = findViewById<Button>(R.id.btn_6)
        val btn7 = findViewById<Button>(R.id.btn_7)
        val btn8 = findViewById<Button>(R.id.btn_8)
        val btn9 = findViewById<Button>(R.id.btn_9)
        val btnDot = findViewById<Button>(R.id.btn_dot)
        val btnAdd = findViewById<Button>(R.id.btn_add)
        val btnSubtract = findViewById<Button>(R.id.btn_subtract)
        val btnMultiply = findViewById<Button>(R.id.btn_multiply)
        val btnDivide = findViewById<Button>(R.id.btn_divide)
        val btnClear = findViewById<Button>(R.id.btn_clear)
        val btnEquals = findViewById<Button>(R.id.btn_equals)


        fun highlightButton(button: Button) {
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D0F0D0"))
        }


        btn0.setOnClickListener {
            highlightButton(btn0)
            onDigit("0")
        }
        btn1.setOnClickListener {
            highlightButton(btn1)
            onDigit("1")
        }
        btn2.setOnClickListener {
            highlightButton(btn2)
            onDigit("2")
        }
        btn3.setOnClickListener {
            highlightButton(btn3)
            onDigit("3")
        }
        btn4.setOnClickListener {
            highlightButton(btn4)
            onDigit("4")
        }
        btn5.setOnClickListener {
            highlightButton(btn5)
            onDigit("5")
        }
        btn6.setOnClickListener {
            highlightButton(btn6)
            onDigit("6")
        }
        btn7.setOnClickListener {
            highlightButton(btn7)
            onDigit("7")
        }
        btn8.setOnClickListener {
            highlightButton(btn8)
            onDigit("8")
        }
        btn9.setOnClickListener {
            highlightButton(btn9)
            onDigit("9")
        }
        btnDot.setOnClickListener {
            highlightButton(btnDot)
            onDigit(".")
        }


        btnAdd.setOnClickListener {
            highlightButton(btnAdd)
            onOperator("+")
        }
        btnSubtract.setOnClickListener {
            highlightButton(btnSubtract)
            onOperator("-")
        }
        btnMultiply.setOnClickListener {
            highlightButton(btnMultiply)
            onOperator("*")
        }
        btnDivide.setOnClickListener {
            highlightButton(btnDivide)
            onOperator("/")
        }

        btnClear.setOnClickListener {
            highlightButton(btnClear)
            onClear()
        }
        btnEquals.setOnClickListener {
            highlightButton(btnEquals)
            onEqual()
        }

    }


    private fun onDigit(digit: String) {
        if (tvResult.text == "0" && digit != ".") {
            tvResult.text = digit
        } else if (digit == ".") {
            if (!tvResult.text.contains('.')) {
                tvResult.append(digit)
            }
        } else {
            tvResult.append(digit)
        }
        lastNumeric = true
    }

    private fun onOperator(op: String) {
        if (lastNumeric && !isOperatorAdded) {
            tvResult.append(op)
            isOperatorAdded = true
            lastNumeric = false
        }
    }

    private fun onClear() {
        tvResult.text = ""
        lastNumeric = false
        isOperatorAdded = false
    }

    private fun onEqual() {
        if (lastNumeric && isOperatorAdded) {
            val expression = tvResult.text.toString()
            try {
                var operatorIndex = -1
                var operator = ' '
                val allOps = "+*/-"

                for (i in 1 until expression.length) {
                    if (expression[i] in allOps) {
                        operator = expression[i]
                        operatorIndex = i
                        break
                    }
                }

                if (operatorIndex == -1) return

                val num1 = expression.substring(0, operatorIndex).toDouble()
                val num2 = expression.substring(operatorIndex + 1).toDouble()
                var result = 0.0

                when (operator) {
                    '+' -> result = num1 + num2
                    '-' -> result = num1 - num2
                    '*' -> result = num1 * num2
                    '/' -> {
                        if (num2 != 0.0) result = num1 / num2
                        else {
                            tvResult.text = "Error"
                            return
                        }
                    }
                }

                tvResult.text = if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }

                isOperatorAdded = false
                lastNumeric = true
            } catch (e: Exception) {
                tvResult.text = "Error"
            }
        }
    }
}