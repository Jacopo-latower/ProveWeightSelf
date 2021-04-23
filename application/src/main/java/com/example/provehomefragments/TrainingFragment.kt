package com.example.provehomefragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.training_layout.*

class TrainingFragment:Fragment() {

    //Example Data
    var data:List<TrainingItem> = listOf(
        TrainingItem("Cardio", R.drawable.allenamento, 30, "Facile", "Nessuno1", "video"),
        TrainingItem("Gambe", R.drawable._rx1dvez640yxwlwbixdvncui, 10, "Intermedio", "Nessuno2", "video"),
        TrainingItem("Braccia", R.drawable.functional2, 15, "Difficile", "Nessuno3", "video"),
        TrainingItem("Cardio", R.drawable.allenamento, 30, "Facile", "Nessuno4", "video"),
        TrainingItem("Gambe", R.drawable._rx1dvez640yxwlwbixdvncui, 10, "Intermedio", "Nessuno5", "video"),
        TrainingItem("Braccia", R.drawable.functional2, 15, "Difficile", "Nessuno6", "video")

    )

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
        return inflater.inflate(R.layout.training_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_best_trainings.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv_best_trainings.adapter = BestTrainingAdapter(data, activity as MainActivity)

    }
}