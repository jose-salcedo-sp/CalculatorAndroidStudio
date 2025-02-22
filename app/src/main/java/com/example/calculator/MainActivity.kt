package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import java.math.BigDecimal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var currentInput by remember { mutableStateOf("0") }
    var oldInput by remember { mutableStateOf("") }
    var currentOperator by remember { mutableStateOf<Operator>(Operator.NONE) }
    var operand1 by remember { mutableStateOf<BigDecimal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(text = oldInput, fontSize = 24.sp, color = Color.Gray)
            Text(text = currentInput, fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        // Buttons Grid
        val buttons = listOf(
            listOf("AC", "C", "⌫", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf(".", "0", "=", "")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    if (label.isNotEmpty()) {
                        CalculatorButton(label, Modifier.weight(1f)) {
                            handleInput(
                                label,
                                currentInput,
                                oldInput,
                                currentOperator,
                                operand1,
                                updateValues = { newInput, newOperator, newOperand1, newOldInput ->
                                    currentInput = newInput
                                    currentOperator = newOperator
                                    operand1 = newOperand1
                                    oldInput = newOldInput
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .background(Color.Gray, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Text(text = label, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

fun handleInput(
    label: String,
    currentInput: String,
    oldInput: String,
    currentOperator: Operator,
    operand1: BigDecimal?,
    updateValues: (String, Operator, BigDecimal?, String) -> Unit
) {
    when (label) {
        "AC" -> updateValues("0", Operator.NONE, null, "")
        "C" -> updateValues("", currentOperator, operand1, oldInput)
        "⌫" -> updateValues(currentInput.dropLast(1).ifEmpty { "0" }, currentOperator, operand1, oldInput)
        "÷", "×", "-", "+" -> {
            if (operand1 == null) {
                updateValues("", getOperator(label), BigDecimal(currentInput), currentInput)
            }
        }
        "=" -> {
            if (operand1 != null && currentInput.isNotEmpty()) {
                val result = calculateResult(operand1, BigDecimal(currentInput), currentOperator)
                updateValues(result, Operator.NONE, null, "")
            }
        }
        else -> {
            val newInput = if (currentInput == "0") label else currentInput + label
            updateValues(newInput, currentOperator, operand1, oldInput)
        }
    }
}

fun getOperator(symbol: String): Operator {
    return when (symbol) {
        "+" -> Operator.ADD
        "-" -> Operator.SUBTRACT
        "×" -> Operator.MULTIPLY
        "÷" -> Operator.DIVIDE
        else -> Operator.NONE
    }
}

fun calculateResult(operand1: BigDecimal, operand2: BigDecimal, operator: Operator): String {
    return try {
        when (operator) {
            Operator.ADD -> operand1.add(operand2)
            Operator.SUBTRACT -> operand1.subtract(operand2)
            Operator.MULTIPLY -> operand1.multiply(operand2)
            Operator.DIVIDE -> if (operand2 != BigDecimal.ZERO) operand1.divide(operand2, 10, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO
            Operator.NONE -> operand2
        }.toString()
    } catch (e: Exception) {
        "Error"
    }
}

enum class Operator {
    NONE, ADD, SUBTRACT, MULTIPLY, DIVIDE
}