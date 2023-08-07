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
import android.os.Build
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.camera.core.Camera
import androidx.core.content.ContextCompat.startActivity
import com.tristarvoid.qrscanner.FileActivity
import kotlin.math.abs

class GestureListener(private val ctx: Context, private val camera: Camera) :
    GestureDetector.SimpleOnGestureListener() {
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (camera.cameraInfo.torchState.value == 0 && camera.cameraInfo.hasFlashUnit())
                camera.cameraControl.enableTorch(true)
            else if (camera.cameraInfo.torchState.value == 1 && camera.cameraInfo.hasFlashUnit())
                camera.cameraControl.enableTorch(false)
        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val intent = Intent(ctx, FileActivity::class.java)
        startActivity(ctx, intent, null)
        return super.onDoubleTap(e)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    //if (diffY > 0)
                    //onDownwardSwipe()
                    //else
                    //onUpwardSwipe()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}