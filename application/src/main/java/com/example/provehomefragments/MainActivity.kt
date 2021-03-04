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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), SensorEventListener, FragHomeObserver{

    var activeFrag = 0 //0 -> homefrag , 1 -> trainingfrag, 2 -> recipefrag

    private var ACTIVITY_PERMISSION_CODE = 1
    private var sensorManager:SensorManager?= null

    private var running = false //the sensor/stepcounter is active or not
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private var homeFrag : HomeFragment? = null
    private var trainingFrag : TrainingFragment? = null
    private var recipeFrag : Fragment? = null //not implemented yet -> create RecipeFragment()
    // **THINGS TO DO**
    //private var weightFrag: WeightFragment? = null
    //private var userFrag: UserFragment? = null

    private var tvStepCounter : TextView? = null //textView for the step counter;
    // !! this belongs to the HomeFragment, careful if it's destroyed in the switch!!

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager //sensor manager for the stepcounter

        requestUserPermission() //ask if the user give the permission for the step counter
        loadData() //load the previous total steps in the preferences; the default value is 0f

        homeFrag = HomeFragment()
        trainingFrag = TrainingFragment()
        recipeFrag = Fragment()

        if(null == savedInstanceState) { //to prevent rotation problems
            showFragment(homeFrag!!) //initialize the app with the home fragment
        }

        val homeBtn:Button = findViewById(R.id.home_btn)
        homeBtn.setOnClickListener {
            showFragment(homeFrag!!)
            activeFrag = 0
        }

        val trainingBtn:Button = findViewById(R.id.training_button)
        trainingBtn.setOnClickListener {
            showFragment(trainingFrag!!)
            activeFrag = 1
        }

        val recipeBtn:Button = findViewById(R.id.recipe_btn)
        recipeBtn.setOnClickListener {
            showFragment(recipeFrag!!)
            activeFrag = 2
        }

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

    //Switch to the selected fragment
    private fun showFragment(f:Fragment){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit()
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    //update the steps when the user is walking
    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            tvStepCounter = findViewById(R.id.tv_step_counter)
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            tvStepCounter?.text = ("Contapassi: $currentSteps")
        }
    }

    //not used for the moment
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used for the moment
    }

    //Callback from the fragment to notify the step reset
    override fun stepResetNotify() {
        tvStepCounter = findViewById(R.id.tv_step_counter)
        previousTotalSteps = totalSteps
        tvStepCounter?.text = ("Contapassi: 0")
        saveData()
    }

    //Callback from the fragment to notify the creation of the fragment
    override fun fragCreatedNotify() {
        tvStepCounter = findViewById(R.id.tv_step_counter)
        val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
        tvStepCounter?.text = ("Contapassi: $currentSteps")
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



}