package com.example.pmu1

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class SpherePhong(
    private val latitudeBands: Int = 40,
    private val longitudeBands: Int = 40,
    private val radius: Float = 1.0f
) {
    var positionX: Float = 0f
    var positionY: Float = 0f
    var positionZ: Float = 0f

    private lateinit var shaderProgram: ShaderProgram
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private var textureId: Int = 0

    private val vertices: FloatArray
    private val indices: ShortArray
    private val normals: FloatArray
    private val textureCoords: FloatArray

    init {
        val vertexList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()
        val normalList = mutableListOf<Float>()
        val textureCoordList = mutableListOf<Float>()

        for (lat in 0..latitudeBands) {
            val theta = lat * Math.PI / latitudeBands
            val sinTheta = sin(theta).toFloat()
            val cosTheta = cos(theta).toFloat()

            for (long in 0..longitudeBands) {
                val phi = long * 2 * Math.PI / longitudeBands
                val sinPhi = sin(phi).toFloat()
                val cosPhi = cos(phi).toFloat()

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertexList.add(x * radius)
                vertexList.add(y * radius)
                vertexList.add(z * radius)

                normalList.add(x)
                normalList.add(y)
                normalList.add(z)

                textureCoordList.add(long / longitudeBands.toFloat())
                textureCoordList.add(lat / latitudeBands.toFloat())
            }
        }

        for (lat in 0 until latitudeBands) {
            for (long in 0 until longitudeBands) {
                val first = (lat * (longitudeBands + 1) + long).toShort()
                val second = (first + longitudeBands + 1).toShort()

                indexList.add(first)
                indexList.add(second)
                indexList.add((first + 1).toShort())

                indexList.add(second)
                indexList.add((second + 1).toShort())
                indexList.add((first + 1).toShort())
            }
        }

        vertices = vertexList.toFloatArray()
        indices = indexList.toShortArray()
        normals = normalList.toFloatArray()
        textureCoords = textureCoordList.toFloatArray()
    }

    fun initialize() {
        shaderProgram = ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(indices)
                position(0)
            }
        }

        normalBuffer = ByteBuffer.allocateDirect(normals.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(normals)
                position(0)
            }
        }

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
                position(0)
            }
        }
    }

    fun setTexture(textureId: Int) {
        this.textureId = textureId
    }

    fun draw(mvpMatrix: FloatArray, lightPos: FloatArray) {
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
        val normalHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Normal")
        val textureCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_TexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")
        val lightPosHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_LightPos")
        val textureHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_Texture")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform3fv(lightPosHandle, 1, lightPos, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 a_Position;
            attribute vec3 a_Normal;
            attribute vec2 a_TexCoord;
            uniform mat4 u_MVPMatrix;
            uniform vec3 u_LightPos;
            varying vec3 v_Normal;
            varying vec3 v_LightDir;
            varying vec2 v_TexCoord;

            void main() {
                gl_Position = u_MVPMatrix * a_Position;
                v_Normal = normalize(a_Normal);
                v_LightDir = normalize(u_LightPos - a_Position.xyz);
                v_TexCoord = a_TexCoord;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            varying vec3 v_Normal;
            varying vec3 v_LightDir;
            varying vec2 v_TexCoord;
            uniform sampler2D u_Texture;

            void main() {
                vec3 normal = normalize(v_Normal);
                float diffuse = max(dot(normal, v_LightDir), 0.0);

                vec3 ambient = vec3(0.1, 0.1, 0.1);
                vec3 lightColor = vec3(1.0, 1.0, 1.0);
                vec3 textureColor = texture2D(u_Texture, v_TexCoord).rgb;
                vec3 color = ambient + diffuse * lightColor * textureColor;

                gl_FragColor = vec4(color, 1.0);
            }
        """
    }
}
