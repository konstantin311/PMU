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
                    //TO DO
                },
                modifier = Modifier.size(80.dp, 40.dp)
            ) {
                Text("Инф")
            }
        }
    }
}


