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

class ChooseRecipeFragment(objects: RecipeItem, var act: MainActivity) : Fragment() {

    val image: Int = objects.image
    val name: String = objects.name
    val ingredients: String = objects.ingredients
    val process: String= objects.process

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
        return inflater.inflate(R.layout.onerecipe_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val img: ImageView = view.findViewById(R.id.recipe_img)
        val nam: TextView = view.findViewById(R.id.recipe_name)
        val ingr: TextView = view.findViewById(R.id.recipe_ingredients)
        val proc: TextView= view.findViewById(R.id.recipe_process)

        img.setImageResource(image)
        nam.text = name
        ingr.text = ingredients
        proc.text= process

        btn_back.setOnClickListener {
            val listRecipe= RecipeFragment()
            act.setCurrentFragment(listRecipe)
        }

    }

}