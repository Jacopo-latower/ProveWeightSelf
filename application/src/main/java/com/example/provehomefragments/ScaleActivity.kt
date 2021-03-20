package com.example.provehomefragments

import android.content.Intent
import android.content.Context
import android.net.*
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import java.net.URL
import java.nio.charset.Charset

class ScaleActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scale)

        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.setNetworkSpecifier(
            WifiNetworkSpecifier.Builder().apply {
                setSsid("WeightSelf")
                setWpa2Passphrase("myPassword")
            }.build()
        )

        val connectivityManager: ConnectivityManager = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val button: Button = this.findViewById(R.id.MyButton)

        button.setOnClickListener() {
            try {
                connectivityManager.requestNetwork(
                    builder.build(),
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            connectivityManager.bindProcessToNetwork(network)
                            val intent = Intent(this@ScaleActivity, WeightActivity::class.java)
                            startActivity(intent)
                        }
                    })
            } catch (e: SecurityException) {
                System.err.println(e)
            }
        }
    }
}