/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.data.ButtonAnimationState
import com.example.androiddevchallenge.data.TransitionData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun Home() {
    var state: ButtonAnimationState by remember { mutableStateOf(ButtonAnimationState.START) }
    val totalTime: Long by mutableStateOf(6)
    var timeLeft: Long by mutableStateOf(0)
    var start: Boolean by mutableStateOf(false)
    val coroutineScope = rememberCoroutineScope()

    fun startCountdown() {
        start = true
        coroutineScope.launch {
            timeLeft = totalTime
            while (timeLeft > 0) {
                if (timeLeft != 0L) {
                    state = when (state) {
                        ButtonAnimationState.START -> ButtonAnimationState.END
                        ButtonAnimationState.END -> ButtonAnimationState.START
                        ButtonAnimationState.FINISH -> ButtonAnimationState.END
                    }
                    delay(1000)
                }
                timeLeft--
            }
            start = false
        }
    }

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircleButton(state, modifier = Modifier.size(200.dp), num = timeLeft.toInt())
            AnimatedVisibility(visible = !start) {
                Button(onClick = { startCountdown() }) {
                    Text(text = "Start 6s countdown")
                }
            }
        }
    }
}

@Composable
fun CircleButton(state: ButtonAnimationState, num: Int, modifier: Modifier = Modifier) {
    val transitionData = updateTransitionData(state)
    Surface {
        Canvas(
            modifier = modifier
        ) {
            rotate(transitionData.degrees) {
                drawCircle(

                    color = lerp(Color.Yellow, Color.White, 0.5f),
                    radius = size.minDimension / 2.2f,
                )
                // size.width/0.2f
                drawCircle(
                    center = Offset(size.width / 2f, size.width * 0.1f / 2),
                    color = transitionData.color,
                    radius = 5.dp.toPx()
                )
            }
        }
        Box(
            modifier = modifier.background(Color.Transparent)
        ) {
            Text(text = num.toString(), Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun updateTransitionData(state: ButtonAnimationState): TransitionData {

    val transition = updateTransition(state)
    val color = transition.animateColor(
        transitionSpec = { tween(durationMillis = 1000) }
    ) {
        when (state) {
            ButtonAnimationState.START -> lerp(Color.Red, Color.White, 0.5f)
            ButtonAnimationState.END -> lerp(Color.Blue, Color.White, 0.5f)
            ButtonAnimationState.FINISH -> lerp(Color.Blue, Color.White, 0.5f)
        }
    }
    val degrees = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1000) }

    ) {
        when (state) {
            ButtonAnimationState.START -> 0f
            ButtonAnimationState.END -> 360f
            ButtonAnimationState.FINISH -> 0f
        }
    }
    return remember(transition) { TransitionData(color, degrees) }
}
