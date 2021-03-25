package com.example.provehomefragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_weight_layout.*
import okhttp3.*
import java.io.IOException

class WeightFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weight_layout, container, false)
    }

    val url = "http://192.168.4.1/peso"
    var client: OkHttpClient? = null
    var resp : String? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = OkHttpClient()

        val connectivityManager =  requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork
        if(network!=null) {
            val lp: LinkProperties? = connectivityManager.getLinkProperties(network)
            val name = lp?.interfaceName

            println("Il nome del network è: $name")
        }



        //Asynchronous Implementation
        weightBtn?.setOnClickListener {

            val request : Request = Request.Builder().url(url).method("GET", null).build()
            var call : Call = client!!.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace();
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if(!response.isSuccessful) throw IOException("Unexpected code $response")

                        resp = response.body!!.string()

                        println("Il body è: $resp")

                        if(response.body == null) println("NULLOOOOO")


                        activity!!.runOnUiThread { tv_result.text = resp!!.substring(9, 13) }
                    }
                }
            })
        }
    }

}