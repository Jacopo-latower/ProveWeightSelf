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

    var tips:List<TipsItem> = listOf(
        TipsItem ("Stabilisciti degli obiettivi realistici", R.drawable.goals),
        TipsItem ("L'idratazione è importante", R.drawable.tipsdrink),
        TipsItem ("Prova sempre qualcosa di nuovo", R.drawable.benefici_stretching_fb),
        TipsItem ("Riposati e riprenditi dopo un allenamento", R.drawable.relaxpost),
        TipsItem ("Costruisciti una routine quotidiana", R.drawable.routine),
        TipsItem ("Segui un'alimentazione corretta e sana", R.drawable.relax),
        TipsItem ("Ricordati di fare stretching", R.drawable.stretching_statico_passivo),
        TipsItem ("Mangia molta frutta e verdura", R.drawable.healtyfood),
        TipsItem ("Non saltare mai un pasto", R.drawable.saltarepasto),
        TipsItem ("Scegli sempre cibi freschi e di stagione", R.drawable.cibistagione)
    )

    //NOT IMPLEMENTED!!
    private var br: BroadcastReceiver? = null //variable for the br
    private var recipeHome: ImageView?= null
    private var trainHome: ImageView?= null

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

        //set Tips random
        val rnds = (tips.indices).random()
        var imageTips:ImageView =view.findViewById(R.id.imageViewTips)
        var textTips: TextView = view.findViewById(R.id.tips_text)
        imageTips.setImageResource(tips[rnds].image)
        textTips.text=tips[rnds].text

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