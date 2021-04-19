package com.example.provehomefragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class RecipeItem(val name:String, val image: Int, val kcal:Int, val ingredients: String, val process : String)

class RecipeListAdapter(var data:List<RecipeItem>, var act: MainActivity):RecyclerView.Adapter<RecipeListAdapter.MyViewHolder>(){
    class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val img:ImageView = v.findViewById(R.id.recipe_img)
        val name: TextView = v.findViewById(R.id.recipe_name)
        val kcal: TextView = v.findViewById(R.id.recipe_kcal)
        //val ingredients: TextView = v.findViewById(R.id.recipe_ingredients)
        //val process: TextView = v.findViewById(R.id.recipe_process)

        fun bind(i:RecipeItem, callback:(Int) -> Unit){
            img.setImageResource(i.image)
            name.text = i.name
            kcal.text = ("${i.kcal} kcal")

            img.setOnClickListener{callback(adapterPosition)}
        }

        fun unbind(){
            img.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_card_layout, parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position]){pos:Int -> getRecipePage(pos)}
    }

    override fun onViewRecycled(holder: MyViewHolder) {
        holder.unbind()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getRecipePage(pos:Int){
        //Get the specific recipe page -> switch to specific recipe fragment
        var objects= data[pos]
        val chooseRecipe= ChooseRecipeFragment(objects)
               //MainActivity.setCurrentFragment(chooseRecipe)
        act.setCurrentFragment(chooseRecipe)

    }

}