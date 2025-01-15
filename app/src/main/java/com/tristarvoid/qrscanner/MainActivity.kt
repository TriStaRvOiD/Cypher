/*
 * This file is part of Cypher.
 *
 * Cypher is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Cypher is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Cypher. If not, see <https://www.gnu.org/licenses/>.
 */

package com.tristarvoid.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.tristarvoid.qrscanner.databinding.ActivityMainBinding
import com.tristarvoid.qrscanner.misc.GestureListener
import com.tristarvoid.qrscanner.misc.ProxyAnalyzer
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

var isActive: Boolean = false
lateinit var barcodesList: MutableList<Barcode>

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val permissionHelper =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted ->
                if (isGranted)
                    camSetup()
                else {
                    Toast.makeText(this, "This app won't work without camera permission", Toast.LENGTH_SHORT).show()
                    this.finishAffinity()
                }
            }

        if (isCameraPermissionGranted())
            camSetup()
        else
            permissionHelper.launch(Manifest.permission.CAMERA)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun camSetup() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val preview = Preview.Builder()
                .build()
                .also { mPreview ->
                    mPreview.surfaceProvider = binding.scanView.surfaceProvider
                }
            val imageCapture = ImageCapture.Builder().build()
            val imageAnalyzer = ProxyAnalyzer(supportFragmentManager)
            val imageAnalysis = ImageAnalysis.Builder().build()
            imageAnalysis.setAnalyzer(cameraExecutor, imageAnalyzer)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis
                )
                gestureDetector = GestureDetector(this, GestureListener(this, camera))
            } catch (e: Exception) {
                Log.d("CameraX", "Starting the camera failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }
}