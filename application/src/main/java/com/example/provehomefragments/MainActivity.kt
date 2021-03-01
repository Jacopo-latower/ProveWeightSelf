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
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), SensorEventListener {

    var activeFrag = 0 //0 -> homefrag , 1 -> trainingfrag, 2 -> recipefrag

    private var ACTIVITY_PERMISSION_CODE = 1
    private var sensorManager:SensorManager?= null

    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    var homeFrag : HomeFragment? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        homeFrag = HomeFragment()
        val trainingFrag = TrainingFragment()
        val recipeFrag = Fragment()

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
            showFragment(trainingFrag)
            activeFrag = 1
        }

        val recipeBtn:Button = findViewById(R.id.recipe_btn)
        recipeBtn.setOnClickListener {
            showFragment(recipeFrag)
            activeFrag = 2
        }

        requestUserPermission() //ask if the user give the permission for the step counter
        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onResume() {
        super.onResume()

        resetSteps() //initialization of the listeners for the steps reset

        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor == null){
            Toast.makeText(this, "No sensor detected", Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    //switch to the selected fragment
    private fun showFragment(f:Fragment){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestUserPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==ACTIVITY_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            val tv = findViewById<TextView>(R.id.tv_step_counter)
            tv.text = ("Contapassi: $currentSteps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used for the moment
    }

    private fun resetSteps(){
        val tv = findViewById<TextView>(R.id.tv_step_counter)
       tv.setOnClickListener{
            Toast.makeText(this, "Long Tap to reset", Toast.LENGTH_SHORT).show()
        }
        tv.setOnLongClickListener{
            previousTotalSteps = totalSteps
            tv.text = ("Contapassi: 0")
            saveData()

            true
        }
    }
    private fun saveData(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        previousTotalSteps = savedNumber
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val tv = findViewById<TextView>(R.id.tv_step_counter)
        outState.putString("Steps", tv.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val tv = findViewById<TextView>(R.id.tv_step_counter)
        tv.text = savedInstanceState.getString("Steps")
    }
}