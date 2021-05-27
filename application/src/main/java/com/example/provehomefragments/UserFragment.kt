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


interface UserFragObserver{
    fun userFragCreatedNotify()
}

class UserFragment : Fragment() {

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
        val userData = RepositoryManager.instance.loadUserData()
        val bmi = activity?.findViewById<TextView>(R.id.bmi)
        val bmiCondition= activity?.findViewById<TextView>(R.id.bmi_condition)
        val gainCalories= activity?.findViewById<TextView>(R.id.gain_calories) //0.5kcal*lastweight*kmpercorsi

        nameTv?.text = ("${userData["name"].toString()} ${userData["surname".toString()]}")

        val observer = activity as? UserFragObserver
        observer?.userFragCreatedNotify()

        btnLogout.setOnClickListener {
            btnLogout.isEnabled = false
            logout()
        }

        //Calcolo BMI
        val hx2 = Integer.parseInt(userData["height"].toString())*Integer.parseInt(userData["height"].toString())
        val lastweight= 70 //al posto del 70 ci dovrebbe essere l'ultimo peso salvato
        val bmiCalculate = (lastweight*10000)/hx2

        bmi?.text= (bmiCalculate.toString())

        if(bmiCalculate<18) {bmiCondition?.text = getString(R.string.below18)}
        else if (bmiCalculate in 18..24) {bmiCondition?.text= getString(R.string.between1825)}
        else if (bmiCalculate in 25..29) {bmiCondition?.text= getString(R.string.between2530)}
        else if (bmiCalculate in 30..34) {bmiCondition?.text= getString(R.string.between3035)}
        else if (bmiCalculate in 35..39) {bmiCondition?.text= getString(R.string.between3540)}
        else if (bmiCalculate>=40) {bmiCondition?.text= getString(R.string.above40)}
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

}
