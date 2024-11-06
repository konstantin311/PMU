package com.example.pmu1

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.graphics.BitmapFactory
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SpherePhongRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val spherePhong = SpherePhong()

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)  // Итоговая модель-вида-проекции

    private val lightPosition = floatArrayOf(0.0f, 0.0f, 3.0f, 1.0f)  // Источник света перед сферой

    private var rotationAngle = 0f
    private var textureId: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Инициализация объекта сферы
        spherePhong.initialize()

        // Загрузка текстуры луны и установка её в объект SpherePhong
        textureId = loadTexture(context, R.drawable.moon)  // Замените на ваше изображение moon
        spherePhong.setTexture(textureId)

        // Настройка начальной модельной матрицы
        Matrix.setIdentityM(modelMatrix, 0)

        // Настройка камеры (вида) - смотрим на объект
        Matrix.setLookAtM(
            viewMatrix, 0,
            0.0f, 0.0f, 5.0f,  // Позиция камеры
            0.0f, 0.0f, 0.0f,  // Точка, на которую смотрим
            0.0f, 1.0f, 0.0f   // Вектор вверх
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        rotationAngle += 0.5f

        Matrix.setRotateM(modelMatrix, 0, rotationAngle, 0f, 1f, 0f)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        spherePhong.draw(mvpMatrix, lightPosition)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio = width.toFloat() / height.toFloat()

        Matrix.perspectiveM(projectionMatrix, 0, 45.0f, aspectRatio, 1.0f, 10.0f)
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options().apply { inScaled = false }
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }
}

