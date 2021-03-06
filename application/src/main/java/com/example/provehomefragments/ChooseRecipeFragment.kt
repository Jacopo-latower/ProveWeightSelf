package com.example.provehomefragments

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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.onerecipe_layout.*

class ChooseRecipeFragment(objects: RecipeItem, var act: MainActivity) : Fragment() {

    private val imageUrl: String = objects.imageUrl
    val name: String = objects.name
    private val ingredients: String = objects.ingredients
    private val process: String= objects.process
    private val kcal : Int = objects.kcal

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
        proc.text = process
        cal.text = kcal.toString().plus(" kcal")

        btn_back.setOnClickListener {
            val listRecipe= RecipeFragment()
            act.setCurrentFragment(listRecipe, "RecipeFragment")
        }

        eat_recipe.setOnClickListener {
            act.refreshGainedCalories(kcal)
            eat_recipe.isEnabled = false
            Toast.makeText(requireContext(), getString(R.string.gainedCalories), Toast.LENGTH_SHORT).show()
        }

    }

}