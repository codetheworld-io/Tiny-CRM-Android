package io.codetheworld.tinycrm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                if (currentState == TelephonyManager.EXTRA_STATE_IDLE &&
                    state == TelephonyManager.EXTRA_STATE_OFFHOOK
                ) {
                    Log.d(TAG, "onReceive: Outgoing call")
                    return
                }

                val isIgnoreState = state != TelephonyManager.EXTRA_STATE_RINGING &&
                        state != TelephonyManager.EXTRA_STATE_OFFHOOK &&
                        state != TelephonyManager.EXTRA_STATE_IDLE

                if (incomingNumber.isNullOrBlank() || isIgnoreState) {
                    return
                }

                Log.d(TAG, "onReceive: $state")

                currentState = state!!

                postStateToServer(incomingNumber, currentState)
            }
        } catch (ignore: Exception) {
            Log.e(TAG, "onReceive: Exception", ignore)
        }
    }

    private fun postStateToServer(number: String, state: String) {
        val jsonObject = JSONObject()
        jsonObject.put("phone_number", number)
        jsonObject.put("state", state)
        jsonObject.put("timestamp", System.currentTimeMillis())
        val jsonObjectString = jsonObject.toString()
        val requestBody =
            jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        ApiInterface.retrofit().postPhoneState(requestBody).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    response.body().let {
                        Log.d(TAG, "onResponse: ${it.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e(TAG, "onReceive: postStateToServer", t)
            }
        })

    }

    companion object {
        private const val TAG = "CallReceiver"

        var currentState: String = TelephonyManager.EXTRA_STATE_IDLE
    }
}
