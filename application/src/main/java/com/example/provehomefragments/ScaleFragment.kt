package com.example.provehomefragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_scale.*

class ScaleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_scale, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.setNetworkSpecifier(
            WifiNetworkSpecifier.Builder().apply {
                setSsid("WeightSelf")
                setWpa2Passphrase("myPassword")
            }.build()
        )

        val connectivityManager: ConnectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectBtn?.setOnClickListener {
            try {
                connectivityManager.requestNetwork(
                    builder.build(),
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            connectivityManager.bindProcessToNetwork(network)
                            findNavController().navigate(R.id.action_scaleFragment_to_weightFragment)
                        }
                    })
            } catch (e: SecurityException) {
                System.err.println(e)
            }
        }
    }

}