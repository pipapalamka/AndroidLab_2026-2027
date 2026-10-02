package com.example.hw1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hw1.ui.theme.HW1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HW1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { FirstScreen(openClick = ::openSecondScreen) }
            }
        }
    }


    private fun openSecondScreen() {
        val name = getString(R.string.fio)
        val group = getString(R.string.group)
        val intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("Name_and_Group", "$name/$group")
        startActivity(intent)
    }
}


@Composable
fun FirstScreen(openClick: () -> Unit) {
    val name = stringResource(R.string.fio)
    val group = stringResource(R.string.group)
    val buttonOpen = stringResource(R.string.open_second_screen)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = name, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = group, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = openClick) {
            Text(text = buttonOpen)
        }


    }

}

