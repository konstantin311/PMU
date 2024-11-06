package com.example.pmu1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pmu1.ui.theme.PMU1Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PMU1Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    //NewsScreen()
                    MainNavHost()
                }
            }
        }
    }
}

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "news") {
        composable("news") { NewsScreen(navController) }
        composable("opengl") { OpenGLScreen(navController) }
        composable("moon_info") { MoonInfoScreen(navController) }
    }
}

class MyGLSurfaceView(context: Context, private val planetViewModel: PlanetViewModel) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(MyGLRenderer(context, planetViewModel))
    }
}

class PlanetViewModel : ViewModel() {
    var currentPlanetIndex by mutableStateOf(3)
        private set

    fun nextPlanet() {
        currentPlanetIndex = (currentPlanetIndex + 1) % 10
    }

    fun previousPlanet() {
        currentPlanetIndex = if (currentPlanetIndex - 1 < 0) 9 else currentPlanetIndex - 1
    }
}

@Composable
fun OpenGLScreen(navController: NavController, planetViewModel: PlanetViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context -> MyGLSurfaceView(context, planetViewModel) },
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { planetViewModel.previousPlanet() }, modifier = Modifier.size(80.dp, 40.dp)) {
                Text("<--")
            }
            Button(onClick = { planetViewModel.nextPlanet() }, modifier = Modifier.size(80.dp, 40.dp)) {
                Text("-->")
            }
            Button(
                onClick = {
                    if(planetViewModel.currentPlanetIndex == 9){
                        navController.navigate("moon_info")
                    }
                },
                modifier = Modifier.size(80.dp, 40.dp)
            ) {
                Text("Инф")
            }
        }
    }
}

@Composable
fun MoonInfoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    if (dragAmount > 50) {
                        navController.popBackStack("news", inclusive = false)
                    }
                }
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Луна",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Луна — единственный естественный спутник Земли.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AndroidView(
            factory = { context ->
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(SpherePhongRenderer(context))
                }
            },
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )
    }
}

