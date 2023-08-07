/*
 * This file is part of Cypher.
 *
 * Cypher is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Cypher is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Cypher. If not, see <https://www.gnu.org/licenses/>.
 */

package com.tristarvoid.qrscanner.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tristarvoid.qrscanner.barcodesList
import com.tristarvoid.qrscanner.databinding.FragmentBottomSheetBinding
import com.tristarvoid.qrscanner.domain.ClipboardViewModel
import com.tristarvoid.qrscanner.domain.ToastViewModel
import com.tristarvoid.qrscanner.isActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomResultFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private var items: ArrayList<String?> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        if (barcodesList.size > 1)
            binding.title.text = "Urls:"
        else
            binding.title.text = "Url:"
        for (barcode in barcodesList) {
            val theUrl = barcode.url?.url
            if (theUrl != null && theUrl != "")
                items.add(theUrl)
        }
        binding.urlListView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(items[position])
            startActivity(intent)
        }
        binding.urlListView.setOnItemLongClickListener { _, _, position, _ ->
            val clipboardViewModel by lazy {
                ViewModelProvider(this)[ClipboardViewModel::class.java]
            }
            val toastViewModel by lazy {
                ViewModelProvider(this)[ToastViewModel::class.java]
            }
            clipboardViewModel.copyToClipboard(items[position])
            toastViewModel.displayToast(message = "Copied to clipboard", length = 4)
            true
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        binding.urlListView.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        isActive = false
        super.onDestroyView()
    }
}