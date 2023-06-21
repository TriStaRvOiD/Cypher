/*
 * This file is part of Cypher.
 *
 * Cypher is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Cypher is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Cypher. If not, see <https://www.gnu.org/licenses/>.
 */

package com.tristarvoid.qrscanner.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tristarvoid.qrscanner.barcodesList
import com.tristarvoid.qrscanner.databinding.FragmentFileBinding
import com.tristarvoid.qrscanner.isActive

class FileFragment(
    private val ctx: Context,
    private val manager: FragmentManager,
    private val uri: Uri
) : Fragment() {
    private lateinit var binding: FragmentFileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileBinding.inflate(inflater, container, false)
        binding.theImage.setImageURI(uri)
        val img = InputImage.fromFilePath(ctx, uri)
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
                        if (!manager.isDestroyed) {
                            BottomResultFragment().show(manager, BottomResultFragment().tag)
                            isActive = true
                        }
                    }
                    else
                        Toast.makeText(ctx, "No QR detected", Toast.LENGTH_LONG).show()
                }
                else
                    Toast.makeText(ctx, "No QR detected", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
            .addOnCompleteListener {
                barcodeScanner.close()
            }
        return binding.root
    }
}