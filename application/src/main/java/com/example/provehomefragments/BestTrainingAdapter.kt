package com.example.provehomefragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

data class TrainingItem(
    val type: String,
    val image: Int,
    val duration: Int,
    val difficulty: String,
    val equipement: String,
    val video: String
)

class BestTrainingAdapter(var data:List<TrainingItem>, var act: MainActivity):RecyclerView.Adapter<BestTrainingAdapter.MyViewHolder>(),
    Filterable {
    class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val img:ImageView = v.findViewById(R.id.recipe_img)
        val duration:TextView = v.findViewById(R.id.training_time)
        val type:TextView = v.findViewById(R.id.recipe_name)
        val difficulty:TextView = v.findViewById(R.id.training_level)

        fun bind(i:TrainingItem, callback:(Int) -> Unit){
            img.setImageResource(i.image)
            duration.text = ("${i.duration} minuti")
            type.text = i.type
            difficulty.text = i.difficulty

            img.setOnClickListener{callback(adapterPosition)}
        }

        fun unbind(){
            img.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_layout, parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position]){pos:Int -> getTrainingPage(pos)}
    }

    override fun onViewRecycled(holder: MyViewHolder) {
        holder.unbind()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getTrainingPage(p:Int){
        val objects= data[p]
        val chooseTraining= ChooseTrainingFragment(objects,act)
        //MainActivity.setCurrentFragment(chooseRecipe)
        act.setCurrentFragment(chooseTraining)
    }

    fun sortName() {
        this.data = this.data.sortedBy { it.type }
        notifyDataSetChanged()
        Log.d("data sort Name", data.toString())
    }

    fun sortTime() {
        this.data = this.data.sortedBy { it.duration }
        notifyDataSetChanged()
        Log.d("data sort Time", data.toString())
    }

    fun sortLevel() {
        this.data = this.data.sortedBy { it.difficulty }
        notifyDataSetChanged()
        Log.d("data sort Level", data.toString())
    }

    //search
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    data = data
                } else {
                    var resultList: MutableList<TrainingItem> = mutableListOf()
                    data = data
                    for (row in data) {
                        if (row.type.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                        if (row.duration.toString().toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        } else
                            if (row.difficulty.toString().toLowerCase(Locale.ROOT)
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
                data = results?.values as MutableList<TrainingItem>
                notifyDataSetChanged()
                Log.d("data", data.toString())
            }

        }
    }
}


