package com.example.provehomefragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TrainingItem(
    val type: String,
    val image: Int,
    val duration: Int,
    val difficulty: String,
    val equipement: String,
    val video: String
)

class BestTrainingAdapter(var data:List<TrainingItem>, var act: MainActivity):RecyclerView.Adapter<BestTrainingAdapter.MyViewHolder>(){
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
        var objects= data[p]
        val chooseTraining= ChooseTrainingFragment(objects)
        //MainActivity.setCurrentFragment(chooseRecipe)
        act.setCurrentFragment(chooseTraining)
    }
}