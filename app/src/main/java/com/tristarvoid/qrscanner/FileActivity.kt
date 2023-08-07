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

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tristarvoid.qrscanner.databinding.ActivityFileBinding
import com.tristarvoid.qrscanner.presentation.fragments.BottomResultFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.storageText.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val img = InputImage.fromFilePath(this, uri)
                val barcodeScanner = BarcodeScanning.getClient()
                barcodeScanner.process(img)
                    .addOnSuccessListener { barcodes ->
                        barcodesList = barcodes.distinct().toMutableList()
                        if (barcodesList.isNotEmpty()) {
                            val iterator = barcodesList.iterator()
                            while (iterator.hasNext()) {
                                if (iterator.next().valueType != Barcode.TYPE_URL)
                                    iterator.remove()
                            }
                            if (barcodesList.isNotEmpty()) {
                                if (!supportFragmentManager.isDestroyed) {
                                    BottomResultFragment().show(
                                        supportFragmentManager,
                                        BottomResultFragment().tag
                                    )
                                    isActive = true
                                }
                            } else
                                Toast.makeText(this, "No QR detected", Toast.LENGTH_LONG).show()
                        } else
                            Toast.makeText(this, "No QR detected", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                    }
                    .addOnCompleteListener {
                        barcodeScanner.close()
                    }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
}