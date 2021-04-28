package com.example.provehomefragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class LoginActivity : AppCompatActivity(), LogFragmentObserver {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(savedInstanceState==null)
            replaceFragment(Login.createInstance())
    }

    override fun replaceFragment(f:Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.login_fragment_container, f)
            .addToBackStack(null)
            .commit()
    }

    override fun loadNextActivity() {
        val num = supportFragmentManager.backStackEntryCount
        for (i in 0..num){
            supportFragmentManager.popBackStack()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        //TODO: specificare la condizione giusta con un if-else, dove nell'else abbiamo "super.OnBackPressed()"
        if(supportFragmentManager.backStackEntryCount>0)
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }

}