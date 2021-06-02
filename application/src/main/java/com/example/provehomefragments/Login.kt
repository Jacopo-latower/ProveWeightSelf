package com.example.provehomefragments

import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.login.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import org.bson.Document

class Login : Fragment() {

    private var fogliaBottomAnim : Animation? = null;
    private var textBottomAnim : Animation? = null;

    companion object{
        fun createInstance() = Login()
    }

    lateinit var app: App

    private var emailInsert= String()
    private var passwordInsert= String()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login.isEnabled = false


        fogliaBottomAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.foglia_bottom_animation)
        textBottomAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.text_bottom_animation)

        logo?.animation = fogliaBottomAnim
        weightself?.animation = textBottomAnim

        progressBar.visibility = View.INVISIBLE

        val appId:String = "prova_weightself-jnubd"
        app = App(AppConfiguration.Builder(appId).build())

        //save email
        email.doAfterTextChanged {
            btn_login.isEnabled = false
            emailInsert = email.text.toString()
            if((email.text.toString().isNotEmpty()) && (password.text.toString().isNotEmpty())){
                btn_login.isEnabled=true
            }
        }

        //save password
        password.doAfterTextChanged {
            btn_login.isEnabled = false
            passwordInsert = password.text.toString()
            if((email.text.toString().isNotEmpty()) && (password.text.toString().isNotEmpty())){
                btn_login.isEnabled = true
            }
        }

        //control with database
        //Click Login
        btn_login.setOnClickListener{
            progressBar.visibility = View.VISIBLE

            btn_login.isEnabled = false

            if(emailInsert.isNotEmpty() && passwordInsert.isNotEmpty())
            signInUser(emailInsert, passwordInsert)
        }

        //Click Register
        btn_register.setOnClickListener{
            val observer = activity as LogFragmentObserver
            observer.replaceFragment(SignUp.createInstance())
        }

    }

    private fun signInUser(email:String, password:String){

        val creds : Credentials = Credentials.emailPassword(email, password)
        var user : User? = null

        app.loginAsync(creds){
            if(it.isSuccess){
                Log.v("AUTH", "Login Successful")
                user = app.currentUser()
                val customUserData : Document? = user?.customData
                Log.v("EXAMPLE", "Fetched custom user data: $customUserData")
                val observer = activity as LogFragmentObserver
                observer.loadNextActivity()
            }
            else{
                progressBar.visibility = View.INVISIBLE
                Log.e("AUTH", "Error in the login: ${it.error}")
                Toast.makeText(activity, getString(R.string.loginError), Toast.LENGTH_SHORT).show()
            }
        }
    }
}