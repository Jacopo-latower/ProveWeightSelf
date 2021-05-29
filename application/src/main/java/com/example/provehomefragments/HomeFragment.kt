package com.example.provehomefragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.user_frag_layout.*

// interface implemented by the main activity to notify changes
interface FragHomeObserver{
    fun stepResetNotify()
    fun fragCreatedNotify()
}

class HomeFragment : Fragment(R.layout.fragment_home_layout), RepositoryAsyncTaskObserver {

    private var stepCounter:TextView? = null
    private var lastWeight:TextView? = null

    var tips:List<TipsItem> = listOf(

        TipsItem ("Stabilisciti degli obiettivi realistici", R.drawable.goals),
        TipsItem ("L'idratazione è importante", R.drawable.tipsdrink),
        TipsItem ("Prova sempre qualcosa di nuovo", R.drawable.benefici_stretching_fb),
        TipsItem ("Riposati e riprenditi dopo un allenamento", R.drawable.relaxpost),
        TipsItem ("Costruisciti una routine quotidiana", R.drawable.routine),
        TipsItem ("Segui un'alimentazione corretta e sana", R.drawable.relax),
        TipsItem ("Ricordati di fare stretching", R.drawable.stretching_statico_passivo),
        TipsItem ("Mangia molta frutta e verdura", R.drawable.healtyfood),
        TipsItem ("Non saltare mai un pasto", R.drawable.saltarepasto),
        TipsItem ("Scegli sempre cibi freschi e di stagione", R.drawable.cibistagione)
    )

    private var recipeHome: ImageView?= null
    private var trainHome: ImageView?= null
    private var lineChart: LineChart?= null

    private var weights:List<Weights>? = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stepCounter = view.findViewById(R.id.daily_steps)
        lastWeight = view.findViewById(R.id.last_weight)

        //Notify to the main activity that the fragment has been created
        val observer = activity as? FragHomeObserver
        observer?.fragCreatedNotify()

        //Initialized the listener for the text view to reset the steps
        resetStepsInit()

        //set Tips random
        val rnds = (tips.indices).random()
        val imageTips:ImageView =view.findViewById(R.id.imageViewTips)
        val textTips: TextView = view.findViewById(R.id.tips_text)
        imageTips.setImageResource(tips[rnds].image)
        textTips.text=tips[rnds].text

        //Creazione Grafico
        lineChart = view.findViewById(R.id.line_chart)

        RepositoryManager.instance.loadWeights(this)

    }

    //Called in the repository manager when the data is loaded, so we can update the UI
    override fun onAsyncLoadingFinished(){
        weights = RepositoryManager.instance.dataWeights.sortedBy {it.date} //init all the weights

        //rimuovo i pesi dalla lista tranne gli ultimi 7, per evitare null pointer nel grafico




        if(weights!!.isNotEmpty()){
            val peso= weights!![weights!!.size - 1].weight.toString()
            lastWeight?.text =  ("$peso Kg")
            weights!!.sortedBy { it.date }
            setLineChartData()
        }

        else{
            tv_trend.text = getString(R.string.pesatiGrafico)
        }

    }

    //Valori Grafico
    private fun setLineChartData() {

        //Testi Ascisse
        val xvalues = ArrayList<String>()
        xvalues.add("Lunedì")
        xvalues.add("Martedì")
        xvalues.add("Mercoledi")
        xvalues.add("Giovedì")
        xvalues.add("Venerdì")
        xvalues.add("Sabato")
        xvalues.add("Domenica")

        //Valori Grafico Ordinate
        val lineEntry = ArrayList<Entry>();


        // prendo gli ultimi 7 elementi per non eccedere rispetto ai valori che il grafico può ospitare sulle ascisse
        for((i, w) in weights!!.takeLast(7).withIndex()){
            w.weight?.let { Entry(it.toFloat(), i) }?.let { lineEntry.add(it) }
        }

       //Layout Grafico
        val lineDataSet = LineDataSet(lineEntry, "First")
        lineDataSet.color = resources.getColor(R.color.light_blue)
        lineDataSet.lineWidth = 2f
        lineChart?.setDescription("")
        lineChart?.axisLeft?.textColor = resources.getColor((R.color.trasparent))
        lineChart?.axisLeft?.textSize=24f
        lineChart?.axisRight?.textColor = resources.getColor((R.color.trasparent))
        lineChart?.axisRight?.textSize=24f
        lineChart?.xAxis?.setDrawLabels(false)
        lineChart?.legend?.isEnabled = false
        lineDataSet.circleRadius =5f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor=resources.getColor(R.color.blue)
        lineDataSet.fillAlpha= 40
        lineDataSet.valueTextColor = resources.getColor(R.color.white)
        lineDataSet.valueTextSize = 20f
        val data = LineData(xvalues, lineDataSet)
        lineChart?.data= data
        lineChart?.setBackgroundResource(R.drawable.background3)
        lineChart?.animateXY(1000, 1000)
    }

    private fun resetStepsInit(){
        //if the user click on the textView, notify to him that the long tap is requested
        stepCounter?.setOnClickListener{
            Toast.makeText(activity, getString(R.string.longTapToReset), Toast.LENGTH_SHORT).show()
        }
        //if the user long tap the textView, notify to the main activity to reset the values with "stepResetNotify()"
        stepCounter?.setOnLongClickListener{
            //Callback to the main activity
            val observer = activity as? FragHomeObserver
            observer?.stepResetNotify()

            true
        }
    }

    fun getLastWeight(): String {
        return lastWeight.toString()
    }
}