package com.example.provehomefragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.*
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    var activeFrag = 0 //0 -> homefrag , 1 -> trainingfrag, 2 -> recipefrag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val homeFrag = HomeFragment()
        val trainingFrag = TrainingFragment()
        val recipeFrag = Fragment()

        if(null == savedInstanceState) //to prevent rotation problems
        showFragment(homeFrag) //initialize the app with the home fragment

        val homeBtn:Button = findViewById(R.id.home_btn)
        homeBtn.setOnClickListener {
            showFragment(homeFrag)
            activeFrag = 0
        }

        val trainingBtn:Button = findViewById(R.id.training_button)
        trainingBtn.setOnClickListener {
            showFragment(trainingFrag)
            activeFrag = 1
        }

        val recipeBtn:Button = findViewById(R.id.recipe_btn)
        recipeBtn.setOnClickListener {
            showFragment(recipeFrag)
            activeFrag = 2
        }
        
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


    public fun getActiveFrag():Int?{
        return activeFrag
    }

    fun showFragment(f:Fragment){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit()
    }
}