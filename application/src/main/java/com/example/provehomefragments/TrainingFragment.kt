package com.example.provehomefragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.recipe_layout.*
import kotlinx.android.synthetic.main.training_layout.*
import kotlinx.android.synthetic.main.training_layout.btn_sortMenu

class TrainingFragment:Fragment() {

    private lateinit var data2: MutableList<TrainingItem>
    private lateinit var viewModel: TrainingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.training_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(TrainingViewModel::class.java)
        viewModel.init()
        val myTrainings = viewModel.getLiveTrainings().value
        data2 = listOf<TrainingItem>().toMutableList()
        if (myTrainings != null) {
            for(t in myTrainings){
               data2.add(TrainingItem(t.trainingName!!, t.imgUrl!!, t.duration?.toInt()!!, t.difficulty!!, t.description!!, t.videoUrl!!))
            }
        }
        rv_best_trainings.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv_best_trainings.adapter = BestTrainingAdapter(data2, activity as MainActivity)

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_sortMenu -> {
                    showPopup(view)
                }
            }
        }

        btn_sortMenu.setOnClickListener(clickListener)

        search_training.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                BestTrainingAdapter(data2, activity as MainActivity).filter.filter(newText)
                return false
            }

        })

    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity?.applicationContext, view)

        //sort
        popup.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.menu_namet -> {
                    BestTrainingAdapter(data2, activity as MainActivity).sortName()
                    true
                };
                R.id.menu_level -> {
                    BestTrainingAdapter(data2, activity as MainActivity).sortLevel()
                    true
                };
                R.id.menu_time -> {
                    BestTrainingAdapter(data2, activity as MainActivity).sortTime()
                    true
                };
                else -> false
            }
        }

        popup.inflate(R.menu.popup_t)
        popup.show()
    }
}