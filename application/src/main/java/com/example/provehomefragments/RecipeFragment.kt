package com.example.provehomefragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recipe_layout.*

class RecipeFragment : Fragment() {

    val data: List<RecipeItem> = listOf(
        RecipeItem("Fagioli Rinforzanti", R.drawable._669_fagioli, 1234, "blblblblbllbblblbl 1", "blblblblbllbblblbl 1"),
        RecipeItem("Mega Frittura di Pesce", R.drawable.pesce_fritto, 89, "blblblblbllbblblbl 2", "blblblblbllbblblbl 2"),
        RecipeItem("Pesche Sciroppate", R.drawable.immagini_2018_mangiare_ricette_pesche_sciroppate, 3422, "blblblblbllbblblbl 3", "blblblblbllbblblbl 3"),
        RecipeItem("Torta di Achille Lauro", R.drawable.achille_lauro_2, 2223, "blblblblbllbblblbl 4", "blblblblbllbblblbl 4")
    )

    private lateinit var data2: MutableList<RecipeItem>
    private lateinit var viewModel: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_layout, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        viewModel.init()
        val myRecipes = viewModel.getLiveRecipes().value
        data2 = listOf<RecipeItem>().toMutableList()
        if (myRecipes != null) {
            for(r in myRecipes){
                data2.add(RecipeItem(r.recipeName!!, R.drawable.app_icon_big,r.kcal?.toInt()!!,arrayToString(r.ingredients), r.description!!))
            }
        }
        val recipeRv = activity?.findViewById<RecyclerView>(R.id.rv_recipes)
        recipeRv?.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recipeRv?.adapter = RecipeListAdapter(data2, activity as MainActivity)

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_sortMenu -> {
                    showPopup(view)
                }
            }
        }

        btn_sortMenu.setOnClickListener(clickListener)

        search_recipe.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                RecipeListAdapter(data, activity as MainActivity).filter.filter(newText)
                return false
            }
        })

    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity?.applicationContext, view)

        //sort
        popup.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.menu_namer -> {
                    RecipeListAdapter(data, activity as MainActivity).sortName()
                    true
                };
                R.id.menu_kcal -> {
                    RecipeListAdapter(data, activity as MainActivity).sortKcal()
                    true
                };
                else -> false
            }
        }

        popup.inflate(R.menu.popup_r)
        popup.show()
    }

    private fun arrayToString(list:List<String>) : String{
        var result:String = ""
        for (e in list){
            result = "$result \n $e"
        }
        return result
    }

}