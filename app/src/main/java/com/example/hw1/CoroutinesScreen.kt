package com.example.hw1

import android.widget.SeekBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TASK_DELAY_MS = 1000L
private const val MIN_COUNT = 1
private const val MAX_COUNT = 100

enum class RunMode { SEQUENTIAL, PARALLEL }

class CoroutinesState(private val scope: CoroutineScope) {

    var count by mutableIntStateOf(5)
        private set
    var total by mutableIntStateOf(0)
        private set
    var completed by mutableIntStateOf(0)
        private set
    var isRunning by mutableStateOf(false)
        private set

    private var job: Job? = null

    fun onCountChange(value: Int) {
        count = value.coerceIn(MIN_COUNT, MAX_COUNT)
    }

    fun start(mode: RunMode) {
        if (isRunning) return

        val n = count
        total = n
        completed = 0
        isRunning = true

        job = scope.launch {
            try {
                when (mode) {
                    RunMode.SEQUENTIAL -> runSequential(n)
                    RunMode.PARALLEL -> runParallel(n)
                }
            } finally {
                isRunning = false
                job = null
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }

    private suspend fun runSequential(n: Int) {
        for (i in 1..n) {
            doWork()
            completed = i
        }
    }

    private suspend fun runParallel(n: Int) = coroutineScope {
        val tasks = (1..n).map {
            async {
                doWork()
            }
        }

        for (task in tasks) {
            task.await()
            completed++
        }
    }

    private suspend fun doWork() {
        delay(TASK_DELAY_MS)
    }
}

@Composable
fun rememberCoroutinesState(): CoroutinesState {
    val scope = rememberCoroutineScope()
    return remember(scope) { CoroutinesState(scope) }
}

@Composable
fun CoroutinesScreen(modifier: Modifier = Modifier) {
    val state = rememberCoroutinesState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Количество корутин: ${state.count}",
            style = MaterialTheme.typography.titleMedium
        )

        AndroidView(
            factory = { context ->
                SeekBar(context).apply {
                    max = MAX_COUNT - MIN_COUNT
                    progress = state.count - MIN_COUNT
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            if (fromUser) {
                                state.onCountChange(progress + MIN_COUNT)
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })
                }
            },
            update = { seekBar ->
                seekBar.isEnabled = !state.isRunning
                val expected = state.count - MIN_COUNT
                if (seekBar.progress != expected) {
                    seekBar.progress = expected
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { state.start(RunMode.SEQUENTIAL) },
            enabled = !state.isRunning,
            modifier = Modifier.fillMaxWidth()
        ) { Text("1. Последовательно") }

        Button(
            onClick = { state.start(RunMode.PARALLEL) },
            enabled = !state.isRunning,
            modifier = Modifier.fillMaxWidth()
        ) { Text("2. Параллельно") }

        Button(
            onClick = { state.cancel() },
            enabled = state.isRunning,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier.fillMaxWidth()
        ) { Text("3. Отменить все") }

        if (state.isRunning) {
            LinearProgressIndicator(
                progress = {
                    if (state.total > 0) state.completed / state.total.toFloat() else 0f
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}