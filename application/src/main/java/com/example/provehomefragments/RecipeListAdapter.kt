package com.example.provehomefragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*

data class RecipeItem(val name:String, val imageUrl: String, val kcal:Int, val ingredients: String, val process : String)

class RecipeListAdapter(var data:List<RecipeItem>, var act: MainActivity):RecyclerView.Adapter<RecipeListAdapter.MyViewHolder>(), Filterable{

    private var lastPosition = -1 //for the animation

    class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val img: ImageView = v.findViewById(R.id.recipe_img)
        val name: TextView = v.findViewById(R.id.recipe_name)
        val kcal: TextView = v.findViewById(R.id.recipe_kcal)

        fun bind(i:RecipeItem, callback:(Int) -> Unit){
            Picasso.get().load(i.imageUrl).into(img)
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
        setAnimation(holder.itemView, position)
    }

    override fun onViewRecycled(holder: MyViewHolder) {
        holder.unbind()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    //Method for animate the elements of the recycler view
    private fun setAnimation(view:View, position: Int){
        if(position>lastPosition){
            val animation = AnimationUtils.loadAnimation(act, android.R.anim.slide_in_left)
            view.startAnimation(animation)
            lastPosition = position
        }
    }

    //Get the specific recipe page -> switch to specific recipe fragment
    private fun getRecipePage(pos:Int){
        var objects= data[pos]
        val chooseRecipe= ChooseRecipeFragment(objects, act)
        act.setCurrentFragment(chooseRecipe)
    }

    //Sorting functions
    fun sortName() {
        this.data = this.data.sortedBy { it.name }
        notifyDataSetChanged()
        Log.d("data sort Name", data.toString())
    }

    fun sortKcal() {
        this.data = this.data.sortedBy { it.kcal }
        notifyDataSetChanged()
        Log.d("data sort Kcal", data.toString())
    }

    //Searching and filtering
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    data = data
                } else {
                    var resultList: MutableList<RecipeItem> = mutableListOf()
                    data = data
                    for (row in data) {
                        if (row.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        } else
                            if (row.ingredients.toLowerCase(Locale.ROOT)
                                    .contains(charSearch.toLowerCase(Locale.ROOT))
                            ) {
                                resultList.add(row)
                            } else
                                if (row.kcal.toString().toLowerCase(Locale.ROOT)
                                        .contains(charSearch.toLowerCase(Locale.ROOT))
                                ) {
                                    resultList.add(row)
                                }
                    }
                    data = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = data
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                data = results?.values as MutableList<RecipeItem>
                notifyDataSetChanged()
                Log.d("data search", data.toString())
            }
        }
    }
}