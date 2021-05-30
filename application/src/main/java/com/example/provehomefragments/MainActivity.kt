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
import kotlinx.android.synthetic.main.activity_main.*
import java.math.RoundingMode
import java.util.*


data class TipsItem(
    val text: String,
    val image: Int
)

class MainActivity : AppCompatActivity(), SensorEventListener, FragHomeObserver, UserFragObserver {

    private var activeFrag = 0 //0 -> homefrag , 1 -> trainingfrag, 2 -> recipefrag, 3 -> userfrag

    private var ACTIVITY_PERMISSION_CODE = 1
    private var sensorManager  :SensorManager? = null

    private var running = false //the sensor/stepcounter is active or not
    private var totalSteps = 0f
    private var previousTotalSteps = 0f


    private lateinit var currentUser : User
    private lateinit var app : App

    private var homeTvStepCounter : TextView? = null //textView for the step counter;
    // !! this belongs to the HomeFragment, careful if it's destroyed in the switch!!
    private var userTvStepCounter : TextView? = null
    private var userTvDailyStepTarget : TextView? = null
    private var distanceKm : TextView? = null

    private var dailyStepsObjective = 8000 //daily steps to reach for the user; //TODO: implement the User class to have the step objective

    var gainedCalories : Int = 0

    private val HOME_FRAGMENT : String = "HomeFragment"
    private val TRAINING_FRAGMENT : String = "TrainingFragment"
    private val RECIPE_FRAGMENT : String = "RecipeFragment"
    private val USER_FRAGMENT : String = "UserFragment"
    private val SCALE_FRAGMENT : String = "ScaleFragment"


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        homeTvStepCounter = findViewById(R.id.daily_steps)
        //Repository init
        val appId = "prova_weightself-jnubd"
        app = App(AppConfiguration.Builder(appId).build())
        currentUser = app.currentUser()!!

        val recipesConfig = SyncConfiguration.Builder(currentUser, "recipes")
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        val weightsConfig = SyncConfiguration.Builder(currentUser, "weights")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()
        val trainingsConfig = SyncConfiguration.Builder(currentUser, "trainings")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()

        Log.v("EX", "Current User custom data: ${currentUser.customData}")
        RepositoryManager.instance.init(recipesConfig, weightsConfig, trainingsConfig)

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
        val scaleFrag = ScaleFragment(this)

        setCurrentFragment(homeFrag, HOME_FRAGMENT)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home_btn -> setCurrentFragment(homeFrag, HOME_FRAGMENT)
                R.id.training_button -> setCurrentFragment(trainingFrag, TRAINING_FRAGMENT)
                R.id.recipe_btn -> setCurrentFragment(recipeFrag, RECIPE_FRAGMENT)
                R.id.user_button -> setCurrentFragment(userFrag, USER_FRAGMENT)
            }
            true
        }

        val scaleBtn : FloatingActionButton = findViewById(R.id.scale_button)

        scaleBtn.setOnClickListener {
            setCurrentFragment(scaleFrag, SCALE_FRAGMENT)
        }

    }

    fun setCurrentFragment(fragment : Fragment, name : String) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.login_fragment_container,fragment, name)
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
            Toast.makeText(this, getString(R.string.noSensor), Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

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
                Toast.makeText(this, getString(R.string.permissionGranted), Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show()
        }
    }

    //Update the steps when the user is walking
    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            homeTvStepCounter = findViewById(R.id.daily_steps)
            userTvStepCounter = findViewById(R.id.userTvStepCounter)
            userTvDailyStepTarget = findViewById(R.id.userTvDailyStepTarget)
            distanceKm = findViewById(R.id.distance_covered)
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            val currentdistance: Double =  (currentSteps.toDouble()/1667).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            homeTvStepCounter?.text = ("$currentSteps")
            userTvStepCounter?.text = ("$currentSteps")
            userTvDailyStepTarget?.text = ("$dailyStepsObjective")
            distanceKm?.text = ("$currentdistance")
        }

        dataChangedCheck()
    }

    //Check if the last data is changed to reset the steps
    private fun dataChangedCheck(){
        val calendar: Calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lastDateInMillis = sharedPreferences?.getLong("LastDate", -1)
        val lastCalendar: Calendar = Calendar.getInstance()
        if (lastDateInMillis != null) {
            lastCalendar.timeInMillis = lastDateInMillis
        }
        val lastDay = lastCalendar.get(Calendar.DAY_OF_YEAR)

        if (lastDateInMillis != null) {
            if(lastDateInMillis.compareTo(-1) == 0){
                val editor = sharedPreferences.edit()
                editor.putLong("LastDate",calendar.time.time)
                editor.apply()
                Log.v("EX","Data saved")
            }
        }

        if(today > lastDay){
            val editor = sharedPreferences?.edit()
            editor?.putLong("LastDate",calendar.time.time)
            editor?.apply()
            Log.v("EX","Minute higher than last minute")
            stepResetNotify()
        }

        Log.v("DATE","Today is:${calendar.time}")
        Log.v("LAST DATE", "Last Date saved:${lastCalendar.time}")
    }

    //not used for the moment
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used for the moment
    }

    //Callback from the fragment to notify the step reset
    override fun stepResetNotify() {
        homeTvStepCounter = findViewById(R.id.daily_steps)
        previousTotalSteps = totalSteps
        if(homeTvStepCounter == null){
            Log.v("EX","TEXT VIEW NULL")
        }
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

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()

            val homeFragment : HomeFragment? = supportFragmentManager.findFragmentByTag(HOME_FRAGMENT) as? HomeFragment
            val trainingFragment : TrainingFragment? = supportFragmentManager.findFragmentByTag(TRAINING_FRAGMENT) as? TrainingFragment
            val recipeFragment : RecipeFragment? = supportFragmentManager.findFragmentByTag(RECIPE_FRAGMENT) as? RecipeFragment
            val userFragment : UserFragment? = supportFragmentManager.findFragmentByTag(USER_FRAGMENT) as? UserFragment


            if (homeFragment != null) {
                if(homeFragment.isResumed){
                    bottomNavigationView.menu.getItem(0).setCheckable(true).isChecked = true
                }
            }
            if (trainingFragment != null) {
                if(trainingFragment.isResumed){
                    bottomNavigationView.menu.getItem(1).setCheckable(true).isChecked = true
                }
            }
            if (recipeFragment != null) {
                if(recipeFragment.isResumed){
                    bottomNavigationView.menu.getItem(3).setCheckable(true).isChecked = true
                }
            }
            if (userFragment != null) {
                if(userFragment.isResumed){
                    bottomNavigationView.menu.getItem(4).setCheckable(true).isChecked = true
                }
            }
        }
    }
    //Close actives realm on app closing
    override fun onDestroy() {
        Log.v("D", "Activity Destroyed")
        RepositoryManager.instance.onClear()
        super.onDestroy()
    }

    fun refreshGainedCalories(kcal : Int){
        gainedCalories += kcal
    }



}