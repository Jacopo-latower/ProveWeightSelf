package com.example.provehomefragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.recipe_layout.*

class RecipeFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    val data: List<RecipeItem> = listOf(
        RecipeItem("Fagioli Rinforzanti", R.drawable._669_fagioli, 1234, "blblblblbllbblblbl 1", "blblblblbllbblblbl 1"),
        RecipeItem("Mega Frittura di Pesce", R.drawable.pesce_fritto, 89, "blblblblbllbblblbl 2", "blblblblbllbblblbl 2"),
        RecipeItem("Pesche Sciroppate", R.drawable.immagini_2018_mangiare_ricette_pesche_sciroppate, 3422, "blblblblbllbblblbl 3", "blblblblbllbblblbl 3"),
        RecipeItem("Torta di Achille Lauro", R.drawable.achille_lauro_2, 2223, "blblblblbllbblblbl 4", "blblblblbllbblblbl 4")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_recipes.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv_recipes.adapter = RecipeListAdapter(data, activity as MainActivity)


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
}