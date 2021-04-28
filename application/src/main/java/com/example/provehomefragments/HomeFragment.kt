package com.example.provehomefragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.user_frag_layout.*

// interface implemented by the main activity to notify changes
interface FragHomeObserver{
    fun stepResetNotify()
    fun fragCreatedNotify()
}

class HomeFragment : Fragment(R.layout.fragment_home_layout) {

    private var stepCounter:TextView? = null

    //NOT IMPLEMENTED!!
    private var br: BroadcastReceiver? = null //variable for the br
    private var recipeHome: ImageView?= null
    private var trainHome: ImageView?= null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stepCounter = view.findViewById(R.id.daily_steps)

        //NOT IMPLEMENTED
        br = MyBroadcastReceiver() //example to try

        //Notify to the main activity that the fragment has been created
        val observer = activity as? FragHomeObserver
        observer?.fragCreatedNotify()

        //Initialized the listener for the text view to reset the steps
        resetSteps()

        //change in one recipe layout
        recipeHome = view.findViewById(R.id.HImageView_Recipes)
        recipeHome?.setOnClickListener {
            //passaggio pagina ma mancano i file
        }

        //change in one train layout
        trainHome = view.findViewById(R.id.HImageView_Training)
        trainHome?.setOnClickListener {
            //passaggio pagina ma mancano i file
        }



    }

    private fun resetSteps(){
        //if the user click on the textView, notify to him that the long tap is requested
        stepCounter?.setOnClickListener{
            Toast.makeText(activity, "Long Tap to reset", Toast.LENGTH_SHORT).show()
        }
        //if the user long tap the textView, notify to the main activity to reset the values with "stepResetNotify()"
        stepCounter?.setOnLongClickListener{
            //Callback to the main activity
            val observer = activity as? FragHomeObserver
            observer?.stepResetNotify()

            true
        }
    }

    //Ex with broadcast receiver: NOT IMPLEMENTED
    class MyBroadcastReceiver() : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var  result = intent?.getStringExtra("Giorgio")
            //do something else
        }

    }
}