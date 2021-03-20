package com.example.provehomefragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.login.*
import android.content.Context
import android.widget.CalendarView.OnDateChangeListener
import androidx.fragment.app.Fragment
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.util.*

class Login : AppCompatActivity() {

    var email_insert= String()
    var password_insert= String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        btn_login.isEnabled=false

        //save email
        email.doAfterTextChanged {
            btn_login.isEnabled=false
            email_insert=email.text.toString()
            if((email.text.toString().isNotEmpty()) && (password.text.toString().isNotEmpty())){
                btn_login.isEnabled=true
            }
        }

        //save password
        password.doAfterTextChanged {
            btn_login.isEnabled=false
            password_insert=password.text.toString()
            if((email.text.toString().isNotEmpty()) && (password.text.toString().isNotEmpty())){
                btn_login.isEnabled=true
            }
        }

        //control with database

        //Click Login
        btn_login.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            this.finish()
        }

        //Click Register
        btn_register.setOnClickListener{
            val j = Intent(this, Register::class.java)
            startActivity(j)
        }


    }

}