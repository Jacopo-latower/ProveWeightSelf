package com.example.provehomefragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

data class RecipeItem(val name:String, val image: Int, val kcal:Int, val ingredients: String, val process : String)

class RecipeListAdapter(var data:List<RecipeItem>, var act: MainActivity):RecyclerView.Adapter<RecipeListAdapter.MyViewHolder>(){
    class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val img: ImageView = v.findViewById(R.id.recipe_img)
        val name: TextView = v.findViewById(R.id.recipe_name)
        val kcal: TextView = v.findViewById(R.id.recipe_kcal)

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
        val chooseRecipe= ChooseRecipeFragment(objects, act)
        act.setCurrentFragment(chooseRecipe)
    }

    fun sortName() {
        this.data = this.data.sortedBy { it.name }
        notifyDataSetChanged()
    }

    fun sortKcal() {
        this.data = this.data.sortedBy { it.kcal }
        notifyDataSetChanged()
        Log.d("Gaia","Brugo")
    }

}