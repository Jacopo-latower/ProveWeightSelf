package com.example.provehomefragments

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_frag_layout.*

class MainActivity : AppCompatActivity(), SensorEventListener, FragHomeObserver, UserFragObserver{

    private var activeFrag = 0 //0 -> homefrag , 1 -> trainingfrag, 2 -> recipefrag, 3 -> userfrag

    private var ACTIVITY_PERMISSION_CODE = 1
    private var sensorManager:SensorManager?= null

    private var running = false //the sensor/stepcounter is active or not
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private lateinit var currentUser:User

    private var homeTvStepCounter : TextView? = null //textView for the step counter;
    // !! this belongs to the HomeFragment, careful if it's destroyed in the switch!!
    private var userTvStepCounter : TextView? = null
    private var userTvDailyStepTarget : TextView? = null

    private var dailyStepsObjective = 1200 //daily steps to reach for the user; //TODO: implement the User class to have the step objective

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appId:String = "prova_weightself-jnubd"
        val app = App(AppConfiguration.Builder(appId).build())

        val recipesConfig = SyncConfiguration.Builder(app.currentUser(), "recipes")
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        val weightConfig = SyncConfiguration.Builder(app.currentUser(), "weights")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()
        val trainingsConfig = SyncConfiguration.Builder(app.currentUser(), "recipes")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()

        currentUser = app.currentUser()!!
        RepositoryManager.instance.init(recipesConfig, weightConfig, trainingsConfig)

        //Tolgo le ombre sul tab delle icone
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager //sensor manager for the stepcounter

        requestUserPermission() //ask if the user give the permission for the step counter
        loadData() //load the previous total steps in the preferences; the default value is 0f

        val homeFrag = HomeFragment()
        val trainingFrag = TrainingFragment()
        val recipeFrag = RecipeFragment()
        val userFrag = UserFragment()
        val scaleFrag = ScaleFragment()

        setCurrentFragment(homeFrag)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home_btn -> setCurrentFragment(homeFrag)
                R.id.training_button -> setCurrentFragment(trainingFrag)
                R.id.recipe_btn -> setCurrentFragment(recipeFrag)
                R.id.user_button -> setCurrentFragment(userFrag)
            }
            true
        }

        val scaleBtn : FloatingActionButton = findViewById(R.id.scale_button)

        scaleBtn.setOnClickListener {
            setCurrentFragment(scaleFrag)
        }

    }

    fun setCurrentFragment(fragment : Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.login_fragment_container,fragment)
                addToBackStack(null)
                commit()
            }

    //Set up for the sensor listener
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onResume() {
        super.onResume()

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        running = true

        if(stepSensor == null){
            Toast.makeText(this, "No sensor detected", Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /*
    //Switch to the selected fragment
    private fun showFragment(f:Fragment){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit()
    }

     */

    //Request permission to user
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestUserPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_PERMISSION_CODE)
    }

    //What to visualize if the permission is granted or denied
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==ACTIVITY_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    //Update the steps when the user is walking
    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            homeTvStepCounter = findViewById(R.id.daily_steps)
            userTvStepCounter = findViewById(R.id.userTvStepCounter)
            userTvDailyStepTarget = findViewById(R.id.userTvDailyStepTarget)
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            homeTvStepCounter?.text = ("$currentSteps")
            userTvStepCounter?.text = ("$currentSteps")
            userTvDailyStepTarget?.text = ("$dailyStepsObjective")
        }
    }

    //not used for the moment
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used for the moment
    }

    //Callback from the fragment to notify the step reset
    override fun stepResetNotify() {
        homeTvStepCounter = findViewById(R.id.daily_steps)
        previousTotalSteps = totalSteps
        homeTvStepCounter?.text = ("0")
        saveData()
    }

    //Callback from the home fragment to notify the creation of the fragment
    override fun fragCreatedNotify() {
        homeTvStepCounter = findViewById(R.id.daily_steps)
        val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
        homeTvStepCounter?.text = ("$currentSteps")
    }

    //Callback from the user fragment to notify the creation of the fragment
    override fun userFragCreatedNotify() {
        userTvStepCounter = findViewById(R.id.userTvStepCounter)
        userTvDailyStepTarget = findViewById(R.id.userTvDailyStepTarget)
        val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
        userTvStepCounter?.text = ("$currentSteps")
        userTvDailyStepTarget?.text = "$dailyStepsObjective"
    }

    //Save the previousTotalSteps in the preferences. This is called when the user reset the stepcounter.
    private fun saveData(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    //Load the previousTotalSteps saved from the preferences
    private fun loadData(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        previousTotalSteps = savedNumber
    }

    //when the back button is pressed, delete the top fragment from the stack is the entries are > 0
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount>0)
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }

    override fun onDestroy() {
        Log.v("D", "Activity Destroyed")
        RepositoryManager.instance.onClear()
        super.onDestroy()
    }

}