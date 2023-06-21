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
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.tristarvoid.qrscanner.BuildConfig
import org.json.JSONObject

class UrlScanner(context: Context, url: String?) {
    private val requestQueue = Volley.newRequestQueue(context)
    private val requestUrl =
        "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=${BuildConfig.safebrowsing_api_key}"
    val jsonBody = JSONObject(
        """
             {
             "client": {
                "clientId":      "tristarvoid_qrscannner",
                "clientVersion": "1.0.0"
             },
             "threatInfo": {
                "threatTypes":      ["MALWARE"],
                "platformTypes":    ["WINDOWS", "ANDROID"],
                "threatEntryTypes": ["URL"],
                "threatEntries": [
                    {"url": "$url"}
                    ]
                }
             }
            """
    )
    private val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
        Method.POST, requestUrl, jsonBody, Response.Listener {
            if (it.toString() == "{}")
                Toast.makeText(context, "Not malicious", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, "Malicious", Toast.LENGTH_LONG)
                    .show()
        }, Response.ErrorListener {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
        }) {
        @Throws(AuthFailureError::class)
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json; charset=utf-8"
            return headers
        }
    }

    fun execute() {
        requestQueue.add(jsonRequest)
    }
}