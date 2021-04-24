package com.example.provehomefragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.onerecipe_layout.*

class ChooseTrainingFragment(objects: TrainingItem, var act: MainActivity) : Fragment(){

    val image: Int = objects.image
    val type: String = objects.type
    val difficulty: String = objects.difficulty
    val time: Int = objects.duration
    val equipement: String = objects.equipement

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onetraining_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val img: ImageView = view.findViewById(R.id.training_img)
        val nam: TextView = view.findViewById(R.id.training_name)
        val lev: TextView = view.findViewById(R.id.training_level)
        val dur: TextView = view.findViewById(R.id.training_time)
        val equip: TextView = view.findViewById(R.id.training_equipment)


        img.setImageResource(image)
        nam.text = type
        lev.text = difficulty
        dur.text= time.toString()
        equip.text= equipement

        btn_back.setOnClickListener {
            val listTrain= TrainingFragment()
            act.setCurrentFragment(listTrain)
        }
    }
}