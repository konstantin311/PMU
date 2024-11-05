package com.example.pmu1

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context,private val planetViewModel: PlanetViewModel) : GLSurfaceView.Renderer {
    private val square: Square = Square(context)
    private val sun: Sphere = Sphere(40, 40, 0.4f)
    private val moon: Sphere = Sphere(40, 40, 0.4f)
    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private val mercury: Sphere = Sphere(40, 40, 0.25f)  // Меркурий
    private val venus: Sphere = Sphere(40, 40, 0.25f)    // Венера
    private val earth: Sphere = Sphere(40, 40, 0.35f)    // Земля
    private val mars: Sphere = Sphere(40, 40, 0.35f)     // Марс
    private val jupiter: Sphere = Sphere(40, 40, 0.5f)   // Юпитер
    private val saturn: Sphere = Sphere(40, 40, 0.45f)   // Сатурн
    private val uranus: Sphere = Sphere(40, 40, 0.4f)    // Уран
    private val neptune: Sphere = Sphere(40, 40, 0.35f)   // Нептун

    private var angle = 0f
    private var moonAngle = 0f
    private var cubeAngle = 0f

    private var sunTextureId: Int = 0
    private var moonTextureId: Int = 0
    private var mercuryTextureId: Int = 0
    private var venusTextureId: Int = 0
    private var earthTextureId: Int = 0
    private var marsTextureId: Int = 0
    private var jupiterTextureId: Int = 0
    private var saturnTextureId: Int = 0
    private var uranusTextureId: Int = 0
    private var neptuneTextureId: Int = 0

    private val cube: Cube = Cube(context)
    private var currentPlanetIndex = 3

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        square.init()
        sun.initialize()
        moon.initialize()
        mercury.initialize()
        venus.initialize()
        earth.initialize()
        mars.initialize()
        jupiter.initialize()
        saturn.initialize()
        uranus.initialize()
        neptune.initialize()

        cube.init()

        sunTextureId = loadTexture(context, R.drawable.sun)
        moonTextureId = loadTexture(context, R.drawable.moon)
        mercuryTextureId = loadTexture(context, R.drawable.mercury)
        venusTextureId = loadTexture(context, R.drawable.venus)
        earthTextureId = loadTexture(context, R.drawable.earth)
        marsTextureId = loadTexture(context, R.drawable.mars)
        jupiterTextureId = loadTexture(context, R.drawable.jupiter)
        saturnTextureId = loadTexture(context, R.drawable.saturn)
        uranusTextureId = loadTexture(context, R.drawable.uranus)
        neptuneTextureId = loadTexture(context, R.drawable.neptune)
    }


    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        currentPlanetIndex = planetViewModel.currentPlanetIndex
        Matrix.setLookAtM(
            viewMatrix, 0, 0f, 2f, -7f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        val squareMatrix = FloatArray(16)
        Matrix.setIdentityM(squareMatrix, 0)
        Matrix.translateM(squareMatrix, 0, 0f, -1f, 7f)
        Matrix.scaleM(squareMatrix, 0, 16f, 16f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, squareMatrix, 0)
        square.draw(mvpMatrix)


        val planetData = arrayOf(
            Pair(sun, sunTextureId),         // Солнце
            Pair(mercury, mercuryTextureId), // Меркурий
            Pair(venus, venusTextureId),     // Венера
            Pair(earth, earthTextureId),     // Земля
            //Pair(moon,moonTextureId),
            Pair(mars, marsTextureId),       // Марс
            Pair(jupiter, jupiterTextureId), // Юпитер
            Pair(saturn, saturnTextureId),   // Сатурн
            Pair(uranus, uranusTextureId),   // Уран
            Pair(neptune, neptuneTextureId),   // Нептун
        )
        for (i in planetData.indices) {
            val planetMatrix = FloatArray(16)
            Matrix.setIdentityM(planetMatrix, 0)

            val planetAngle = angle / (i + 1) * 2f
            val distanceFromSun = (i + 1) * 0.4f

            Matrix.rotateM(planetMatrix, 0, planetAngle, 0f, 1f, 0f)

            if (i == 0) {
                Matrix.translateM(planetMatrix, 0, 0f, 0f, 0f)
                Matrix.scaleM(planetMatrix, 0, planetData[i].first.size, planetData[i].first.size, planetData[i].first.size)
            } else if (i == 3) {
                Matrix.translateM(planetMatrix, 0, distanceFromSun, 0f, 0f)
                Matrix.scaleM(planetMatrix, 0, planetData[i].first.size, planetData[i].first.size, planetData[i].first.size)

                moonAngle += 1f
                val moonMatrix = FloatArray(16)
                Matrix.setIdentityM(moonMatrix, 0)

                Matrix.rotateM(moonMatrix, 0, moonAngle, 0f, 1f, 0f)
                Matrix.translateM(moonMatrix, 0, 0.6f, 0f, 0f)
                Matrix.scaleM(moonMatrix, 0, moon.size, moon.size, moon.size)

                Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
                Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, planetMatrix, 0)
                Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, moonMatrix, 0)
                moon.draw(mvpMatrix, moonTextureId)
                if (currentPlanetIndex == planetData.size) {
                    val cubeMatrix = FloatArray(16)
                    Matrix.setIdentityM(cubeMatrix, 0)

                    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
                    Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, planetMatrix, 0)
                    Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, moonMatrix, 0)

                    Matrix.translateM(mvpMatrix, 0, 0f, 0f, 0f)
                    Matrix.rotateM(mvpMatrix, 0, cubeAngle, 0f, 1f, 0f)

                    cube.draw(mvpMatrix)
                }
            }
            else {
                Matrix.translateM(planetMatrix, 0, distanceFromSun, 0f, 0f)
                Matrix.scaleM(planetMatrix, 0, planetData[i].first.size, planetData[i].first.size, planetData[i].first.size)
            }

            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, planetMatrix, 0)
            planetData[i].first.draw(mvpMatrix, planetData[i].second)

            if (i == currentPlanetIndex) {
                cubeAngle = planetAngle + 1f
                val cubeMatrix = FloatArray(16)
                Matrix.setIdentityM(cubeMatrix, 0)

                Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
                Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, planetMatrix, 0)

                Matrix.translateM(mvpMatrix, 0, 0f, 0f, 0f)
                Matrix.rotateM(mvpMatrix, 0, cubeAngle, 0f, 1f, 0f)

                cube.draw(mvpMatrix)
            }

        }
        angle += 1f
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)


        if (textureHandle[0] != 0) {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        return textureHandle[0]
    }
}
