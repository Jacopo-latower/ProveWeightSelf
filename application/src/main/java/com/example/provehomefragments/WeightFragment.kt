package com.example.provehomefragments

import android.content.Context
import android.net.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.fragment_weight_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException

class WeightFragment(var act: MainActivity) : Fragment(), RepositoryAsyncTaskObserver {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weight_layout, container, false)
    }

    private val url = "http://192.168.4.1/peso"
    var client: OkHttpClient? = null
    var resp: String? = null
    var peso: String? = null

    var nc : ConnectivityManager.NetworkCallback? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveBtn.isEnabled = false

        tv_loading.visibility = View.INVISIBLE
        weightSaved.visibility = View.INVISIBLE


        client = OkHttpClient()

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork
        if (network != null) {
            val lp: LinkProperties? = connectivityManager.getLinkProperties(network)
            val name = lp?.interfaceName

            println("Il nome del network è: $name")
        }


        //Asynchronous Implementation
        weightBtn?.setOnClickListener {

            saveBtn.isEnabled = true

            val request: Request = Request.Builder().url(url).method("GET", null).build()
            var call: Call = client!!.newCall(request)


            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace();
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")


                        resp = response.body!!.string()

                        println("Il body è: $resp")

                        if (response.body == null) println("Il body della risposta è vuoto.")
                        peso = resp!!.substring(9, 14)

                        tv_result.text = peso

                        activity!!.runOnUiThread { peso }

                    }
                }
            })
        }

        /**
         * la pressione del bottone Salva disattiva la connessione con la bilancia e,
         * nonappena il dispositivo ritrova la connessione di rete, invia il peso al server
         */

        saveBtn.setOnClickListener {

            showLoading()

            var c = 0

            connectivityManager.bindProcessToNetwork(null)

            nc?.let { connectivityManager.unregisterNetworkCallback(it) }

            runBlocking {

                for(i in 1..40){

                    if(c == 3) c = 0

                    if(c == 0) tv_loading.text = getString(R.string.loading1)
                    if(c == 1) tv_loading.text = getString(R.string.loading2)
                    else tv_loading.text = getString(R.string.loading3)

                    val activeNetwork : Network? = connectivityManager.activeNetwork

                    if(activeNetwork != null) {
                        RepositoryManager.instance.writeWeight(peso!!, this@WeightFragment)

                        delay(3000)

                        break
                    }


                    c++

                    delay(500)
                }
            }
        }
    }


    /**
     * recupero della networkCallback istanziata in ScaleFragment
     */
    fun setCallback(nc : ConnectivityManager.NetworkCallback){
        this.nc = nc
    }

    override fun onAsyncLoadingFinished() {
        tv_loading.visibility = View.INVISIBLE
        weightSaved.visibility = View.VISIBLE

        act.setCurrentFragment(HomeFragment())
    }

    private fun showLoading(){
        tv_loading.visibility = View.VISIBLE
    }
}

