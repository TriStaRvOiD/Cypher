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
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tristarvoid.qrscanner.databinding.ActivityFileBinding
import com.tristarvoid.qrscanner.fragments.FileFragment

class FileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted)
                binding.storageText.text = getString(R.string.pick_image)
        }

        val launcher =
            registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {
                val thing = it
                if (thing != null) {
                    binding.storageText.visibility = View.INVISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragContainer,
                            FileFragment(this, supportFragmentManager, thing)
                        ).commit()
                }
            }

        if (binding.storageText.text == "Checking...")
            if (isStoragePermissionGranted())
                binding.storageText.text = getString(R.string.pick_image)
            else
                binding.storageText.text = getString(R.string.storage_permission)

        binding.storageText.setOnClickListener {
            if (!isStoragePermissionGranted())
                handler.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            else {
                launcher.launch("image/*")
            }
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}