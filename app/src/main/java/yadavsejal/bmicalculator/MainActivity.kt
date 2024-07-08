package yadavsejal.bmicalculator

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import yadavsejal.bmicalculator.ui.theme.BMIcalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BMIcalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("bmi_calculator") })
        }
        composable("bmi_calculator") {
            BMICal(navController = navController)
        }
        composable(
            "bmi_category/{bmiValue}",
            arguments = listOf(navArgument("bmiValue") { type = NavType.FloatType })
        ) { backStackEntry ->
            val bmiValue = backStackEntry.arguments?.getFloat("bmiValue") ?: 0.0f
            BMICategoryScreen(navController = navController, bmiValue = bmiValue.toDouble())
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context: Context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login to your account", fontStyle = FontStyle.Normal, fontSize = 28.sp)

        Spacer(modifier = Modifier.height(128.dp))

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text(text = "Enter Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(128.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text(text = "Enter Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(128.dp))

        Button(
            onClick = {
                if (username == "admin" && password == "123") {
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Invalid Username or Password", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "LOGIN")
        }
    }
}

@Composable
fun BMICal(navController: NavController) {
    var userWeight by remember { mutableStateOf("") }
    var userHeight by remember { mutableStateOf("") }
    var bmiAns by remember { mutableStateOf<Double?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    val context: Context = LocalContext.current
    var weightError by remember { mutableStateOf(false) }
    var heightError by remember { mutableStateOf(false) }


    fun bmi(weight: Double, height: Double): Double {
        val mHeight = height / 100
        return weight / (mHeight * mHeight)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Body Mass Index Calculator", fontStyle = FontStyle.Normal, fontSize = 28.sp)

        Spacer(modifier = Modifier.height(128.dp))

        Text(text = "Weight (in kg)", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = userWeight, onValueChange = { s ->
            userWeight = s
            weightError = s.toDoubleOrNull()?.let { it !in 10.0..300.0 } ?: false
        }, label = { Text(text = "Enter weight") }, isError = weightError)

        if (weightError) {
            Text(
                text = "Weight should be between 10kgs and 300kgs",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(128.dp))

        Text(text = "Height (in cm)", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = userHeight, onValueChange = { s ->
            userHeight = s
            heightError = s.toDoubleOrNull()?.let { it !in 50.0..250.0 } ?: false
        }, label = { Text(text = "Enter height") }, isError = heightError)

        if (heightError) {
            Text(
                text = "Height should be between 50cms and 250 cms",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = {
                val uWeight = userWeight.toDoubleOrNull() ?: 0.0
                val uHeight = userHeight.toDoubleOrNull() ?: 0.0
                if (uWeight == 0.0 && uHeight == 0.0) {
                    Toast.makeText(context, "Invalid Inputs", Toast.LENGTH_SHORT).show()
                } else if (uWeight !in 10.0..300.0 && uHeight !in 50.0..250.0) {
                    Toast.makeText(
                        context,
                        "Please enter values within valid ranges",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    bmiAns = bmi(uWeight, uHeight)
                    isVisible = true
                    navController.navigate("bmi_category/${bmiAns!!.toFloat()}")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Calculate")
        }
        Spacer(modifier = Modifier.height(100.dp))

        if (isVisible) {
            Text(
                text = "Your BMI : %.2f".format(bmiAns),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
fun BMICategoryScreen(navController: NavController, bmiValue: Double) {
    val bmiCategory = remember {
        when {
            bmiValue < 18.5 -> "UnderWeight"
            bmiValue < 24.9 -> "Normal weight"
            bmiValue < 29.9 -> "overweight"
            else -> "Obese"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "BMI CATEGORY", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(128.dp))

        Text(text = "Your BMI is %.2f".format(bmiValue), style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(128.dp))

        Text(text = bmiCategory, style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text(text = "Back to Calculator")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun BMICalPreview() {
    BMIcalculatorTheme {
        AppNavHost()
    }
}