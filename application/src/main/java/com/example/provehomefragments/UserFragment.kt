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


interface UserFragObserver{
    fun userFragCreatedNotify()
}

class UserFragment : Fragment(), RepositoryAsyncTaskObserver{

    var bmi: TextView? = null
    var bmiCondition: TextView? = null
    var gainCalories: TextView? = null
    var userData: org.bson.Document? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_frag_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTv = activity?.findViewById<TextView>(R.id.usernameTextView)
        userData = RepositoryManager.instance.loadUserData()
        bmi = activity?.findViewById<TextView>(R.id.bmi)
        bmiCondition= activity?.findViewById<TextView>(R.id.bmi_condition)
        gainCalories= activity?.findViewById<TextView>(R.id.gain_calories) //0.5kcal*lastweight*kmpercorsi

        nameTv?.text = ("${userData!!["name"].toString()} ${userData!!["surname".toString()]}")

        val observer = activity as? UserFragObserver
        observer?.userFragCreatedNotify()

        btnLogout.setOnClickListener {
            btnLogout.isEnabled = false
            logout()
        }

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
        val hx2 = Integer.parseInt(userData?.get("height")?.toString())*Integer.parseInt(userData?.get("height")?.toString())
        val weights = RepositoryManager.instance.dataWeights.sortedByDescending { it.date } //tutti i pesi ordinati per data discendente
        //val ultimo peso = weights[0].weight ultimo peso salvato in DOUBLE
        val lastweight = 70  //al posto del 70 ci dovrebbe essere l'ultimo peso salvato
        val bmiCalculate = (lastweight*10000)/hx2

        bmi?.text= (bmiCalculate.toString())

        if(bmiCalculate<18) {bmiCondition?.text = getString(R.string.below18)}
        else if (bmiCalculate in 18..24) {bmiCondition?.text= getString(R.string.between1825)}
        else if (bmiCalculate in 25..29) {bmiCondition?.text= getString(R.string.between2530)}
        else if (bmiCalculate in 30..34) {bmiCondition?.text= getString(R.string.between3035)}
        else if (bmiCalculate in 35..39) {bmiCondition?.text= getString(R.string.between3540)}
        else if (bmiCalculate>=40) {bmiCondition?.text= getString(R.string.above40)}
    }

}
