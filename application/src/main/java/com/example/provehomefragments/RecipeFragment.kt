package com.example.provehomefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.recipe_layout.*

class RecipeFragment : Fragment() {

    var data: List<RecipeItem> = listOf(
        RecipeItem("Fagioli Rinforzanti", R.drawable._669_fagioli, 134),
        RecipeItem("Mega Frittura di Pesce", R.drawable.pesce_fritto, 89),
        RecipeItem("Pesche Sciroppate", R.drawable.immagini_2018_mangiare_ricette_pesche_sciroppate, 3422),
        RecipeItem("Torta di Achille Lauro", R.drawable.achille_lauro_2, 2223)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        rv_recipes.adapter = RecipeListAdapter(data)
    }
}