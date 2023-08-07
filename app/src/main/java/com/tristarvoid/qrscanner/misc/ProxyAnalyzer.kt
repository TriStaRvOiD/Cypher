/*
 * This file is part of Cypher.
 *
 * Cypher is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Cypher is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Cypher. If not, see <https://www.gnu.org/licenses/>.
 */

package com.tristarvoid.qrscanner.misc

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tristarvoid.qrscanner.barcodesList
import com.tristarvoid.qrscanner.presentation.fragments.BottomResultFragment
import com.tristarvoid.qrscanner.isActive

class ProxyAnalyzer(fragmentManager: FragmentManager) : ImageAnalysis.Analyzer {
    private var manager = fragmentManager

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val barcodeScanner = BarcodeScanning.getClient()
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodesList = barcodes.distinct().toMutableList()
                    if (!isActive && barcodesList.isNotEmpty()) {
                        val iterator = barcodesList.iterator()
                        while (iterator.hasNext()) {
                            if (iterator.next().valueType != Barcode.TYPE_URL)
                                iterator.remove()
                        }
                        if (barcodesList.isNotEmpty()) {
                            if (!manager.isDestroyed) {
                                BottomResultFragment().show(manager, BottomResultFragment().tag)
                                isActive = true
                            }
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
                .addOnCompleteListener {
                    barcodeScanner.close()
                    imageProxy.close()
                }
        }
    }
}