package com.example.provehomefragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_scale_layout.*

class ScaleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scale_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager: ConnectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.setNetworkSpecifier(
            WifiNetworkSpecifier.Builder().apply {
                setSsid("WeightSelf")
                setWpa2Passphrase("myPassword")
            }.build()
        )



        connectBtn?.setOnClickListener {
            try {
                connectivityManager.requestNetwork(
                    builder.build(),
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            connectivityManager.bindProcessToNetwork(network)
                            setCurrentFragment(WeightFragment())
                        }
                    })



            } catch (e: SecurityException) {
                System.err.println(e)
            }
        }
    }

    private fun setCurrentFragment(fragment : Fragment) =
        activity?.supportFragmentManager!!.beginTransaction().apply {
            replace(R.id.login_fragment_container,fragment)
            commit()
        }

}