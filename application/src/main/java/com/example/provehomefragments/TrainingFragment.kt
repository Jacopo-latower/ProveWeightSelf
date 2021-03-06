package com.example.provehomefragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.recipe_layout.*
import kotlinx.android.synthetic.main.training_layout.*
import kotlinx.android.synthetic.main.training_layout.btn_sortMenu

class TrainingFragment:Fragment(), RepositoryAsyncTaskObserver{

    private lateinit var data2: MutableList<TrainingItem>
    private lateinit var viewModel: TrainingViewModel

    private var refreshButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.training_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RepositoryManager.instance.loadTrainings(this)

        refreshButton = this.activity?.findViewById(R.id.refreshTrainingButton)

        refreshButton?.setOnClickListener {
            viewModel.init()
            RepositoryManager.instance.loadTrainings(this)
        }

        //show popup menu to do SORT
        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_sortMenu -> {
                    showPopup(view)
                }
            }
        }

        //sort
        btn_sortMenu.setOnClickListener(clickListener)

        //search
        search_training.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val trainingAdapterSearch = BestTrainingAdapter(data2, activity as MainActivity)
                trainingAdapterSearch.filter.filter(newText)
                rv_best_trainings.adapter = trainingAdapterSearch
                return false
            }
        })
    }

    //Called in the repository manager when the data is loaded, so we can update the UI
    override fun onAsyncLoadingFinished(){
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
        viewModel.getLiveTrainings().observe(viewLifecycleOwner,{
            rv_best_trainings.adapter = BestTrainingAdapter(data2, activity as MainActivity)
        })
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity?.applicationContext, view)

        //sort
        popup.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.menu_namet -> {
                    val trainingAdapterByName = BestTrainingAdapter(data2, activity as MainActivity)
                    trainingAdapterByName.sortName()
                    rv_best_trainings.adapter = trainingAdapterByName
                    true
                };
                R.id.menu_level -> {
                    val trainingAdapterByLevel = BestTrainingAdapter(data2, activity as MainActivity)
                    trainingAdapterByLevel.sortLevel()
                    rv_best_trainings.adapter = trainingAdapterByLevel
                    true
                };
                R.id.menu_time -> {
                    val trainingAdapterByTime = BestTrainingAdapter(data2, activity as MainActivity)
                    trainingAdapterByTime.sortTime()
                    rv_best_trainings.adapter = trainingAdapterByTime
                    true
                };
                else -> false
            }
        }

        popup.inflate(R.menu.popup_t)
        popup.show()
    }
}