package com.example.hw1.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                NavHost(nav, startDestination = "list") {
                    composable("list") {
                        Scaffold(
                        ) { pad ->
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(pad),
                                contentPadding = PaddingValues(60.dp),
                                verticalArrangement = Arrangement.spacedBy(11.dp)
                            ) {
                                items(animals, key = { it.id }) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { nav.navigate("detail/${item.id}") }
                                    ) {
                                        Text(
                                            text = item.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    composable("detail/{id}") { entry ->
                        val id = entry.arguments?.getString("id")?.toIntOrNull() ?: 0
                        val item = animals.find {
                            it.id == id
                        }
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text(item?.name ?: "") },
                                    navigationIcon = {
                                        IconButton(onClick = { nav.popBackStack() }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Назад"
                                            )
                                        }
                                    }
                                )
                            }
                        ) { pad ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(pad)
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            ) {
                                AsyncImage(
                                    model = item?.image,
                                    contentDescription = item?.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )

                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = item?.name ?: "",
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = item?.description ?: "",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}