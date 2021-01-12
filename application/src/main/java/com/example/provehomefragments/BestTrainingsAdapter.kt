package com.example.provehomefragments

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TrainingItem(val type:String, val image: Int, val duration:Int, val difficulty:String)

class BestTrainingsAdapter(var data:List<TrainingItem>):RecyclerView.Adapter<BestTrainingsAdapter.MyViewHolder>(){
    class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val img:ImageView = v.findViewById(R.id.training_img)
        val duration:TextView = v.findViewById(R.id.training_time)
        val type:TextView = v.findViewById(R.id.type_training)
        val difficulty:TextView = v.findViewById(R.id.difficulty)

        fun bind(i:TrainingItem){
            img.setImageResource(i.image)
            duration.text = "${i.duration} minuti"
            type.text = i.type
            difficulty.text = i.difficulty
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_layout, parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}