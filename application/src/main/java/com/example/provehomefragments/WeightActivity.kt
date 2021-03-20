package com.example.provehomefragments

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_weight.*
import okhttp3.*
import java.io.IOException

class WeightActivity : AppCompatActivity() {

    val url = "http://192.168.4.1/peso"
    var client: OkHttpClient? = null
    var resp : String? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)



        client = OkHttpClient()

        val connectivityManager =  getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork
        if(network!=null) {
            val lp: LinkProperties? = connectivityManager.getLinkProperties(network)
            val name = lp?.interfaceName

            println("Il nome del network è: $name")
        }



        //Asynchronous Implementation
        weightBtn.setOnClickListener {

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


                        this@WeightActivity.runOnUiThread { tv_result.text = resp!!.substring(9, 13) }
                    }
                }
            })
        }
    }
}