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

import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.camera.core.Camera
import com.tristarvoid.qrscanner.FileActivity

class GestureListener(private val context: Context, private val camera: Camera) :
    GestureDetector.SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (camera.cameraInfo.torchState.value == 0 && camera.cameraInfo.hasFlashUnit())
            camera.cameraControl.enableTorch(true)
        else if (camera.cameraInfo.torchState.value == 1 && camera.cameraInfo.hasFlashUnit())
            camera.cameraControl.enableTorch(false)
        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val intent = Intent(context, FileActivity::class.java)
        context.startActivity(intent, null)
        return super.onDoubleTap(e)
    }
}