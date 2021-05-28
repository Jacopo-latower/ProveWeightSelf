package com.example.provehomefragments

import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.user_frag_layout.*
import org.w3c.dom.Document
import java.math.RoundingMode


interface UserFragObserver{
    fun userFragCreatedNotify()
}

class UserFragment : Fragment(), RepositoryAsyncTaskObserver{

    private var bmiCondition : TextView? = null
    private var burnedCalories : TextView? = null
    private var userData : org.bson.Document? = null
    private var lastWeight : TextView? = null
    private var bmiText : TextView?= null
    private var currentStep : TextView?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_frag_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPesati.visibility = View.GONE

        val nameTv = activity?.findViewById<TextView>(R.id.usernameTextView)
        userData = RepositoryManager.instance.loadUserData()
        bmiCondition = activity?.findViewById(R.id.bmi_condition)
        burnedCalories = activity?.findViewById<TextView>(R.id.kcal_burned) //0.5kcal * lastweight * kmpercorsi
        lastWeight = activity?.findViewById<TextView>(R.id.last_weight_tv)
        bmiText = activity?.findViewById<TextView>(R.id.bmi)
        currentStep = activity?.findViewById<TextView>(R.id.userTvStepCounter)

        nameTv?.text = ("${userData!!["name"].toString()} ${userData!!["surname".toString()]}")

        val observer = activity as? UserFragObserver
        observer?.userFragCreatedNotify()

        btnLogout.setOnClickListener {
            btnLogout.isEnabled = false
            logout()
        }

        RepositoryManager.instance.loadWeights(this)

    }

    private fun logout(){
        RepositoryManager.instance.currentUser
            .logOutAsync{r->
                if (r.isSuccess) {
                    Log.v("AUTH", "Successfully logged out.")
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e("AUTH", r.error.toString())
                }}
    }

    //Called in the repository manager when the data is loaded, so we can update the UI
    override fun onAsyncLoadingFinished() {
        //Calcolo BMI
        val hx2 : Double = (Integer.parseInt(userData?.get("height")?.toString()) * (Integer.parseInt(userData?.get("height")?.toString()))).toDouble()
        val weights = RepositoryManager.instance.dataWeights.sortedByDescending { it.date } //tutti i pesi ordinati per data discendente

        if(weights.isNotEmpty()){
            val lastweight = weights[0].weight!!.toDouble() //val ultimo peso = weights[0].weight ultimo peso salvato in DOUBLE
            var bmiCalculate : Double = (lastweight * 10000.00) / hx2
            var currStep = (Integer.parseInt(currentStep?.text.toString())).toDouble()
            var distanceKm : Double = currStep / 1667
            var burnedCal : Double = (0.5 * lastweight * distanceKm).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

            bmiCalculate = bmiCalculate.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

            lastWeight?.text = lastweight.toString().plus(" kg")
            bmiText?.text = bmiCalculate.toString()
            burnedCalories?.text = burnedCal.toString()

            if(bmiCalculate < 18.0) {bmiCondition?.text = getString(R.string.below18)}
            else if (bmiCalculate in 18.0..24.0) {bmiCondition?.text = getString(R.string.between1825)}
            else if (bmiCalculate in 25.0..29.0) {bmiCondition?.text = getString(R.string.between2530)}
            else if (bmiCalculate in 30.0..34.0) {bmiCondition?.text = getString(R.string.between3035)}
            else if (bmiCalculate in 35.0..39.0) {bmiCondition?.text = getString(R.string.between3540)}
            else if (bmiCalculate >= 40.0) {bmiCondition?.text = getString(R.string.above40)}
        }

        else{
            tvPesati.visibility = View.VISIBLE
        }

    }

}
