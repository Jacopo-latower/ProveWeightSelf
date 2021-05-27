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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.onerecipe_layout.*

class ChooseRecipeFragment(objects: RecipeItem, var act: MainActivity) : Fragment() {

    val imageUrl: String = objects.imageUrl
    val name: String = objects.name
    val ingredients: String = objects.ingredients
    val process: String= objects.process
    val kcal: String = ("${objects.kcal} kcal")

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
        val cal: TextView= view.findViewById(R.id.recipe_kcal)

        Picasso.get().load(imageUrl).into(img)
        nam.text = name
        ingr.text = ingredients
        proc.text= process
        cal.text = kcal

        btn_back.setOnClickListener {
            val listRecipe= RecipeFragment()
            act.setCurrentFragment(listRecipe)
        }

        eat_recipe.setOnClickListener {
            //TODO: incrementare le calorie acquisite
        }

    }

}