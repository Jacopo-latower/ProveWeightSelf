package com.example.provehomefragments

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

// interface implemented by the main activity to notify changes
interface FragHomeObserver{
    fun stepResetNotify()
    fun fragCreatedNotify()
}

class HomeFragment : Fragment(R.layout.fragment_home_layout), RepositoryAsyncTaskObserver {

    private var stepCounter:TextView? = null
    private var lastWeight:TextView? = null

    var tips:List<TipsItem> = listOf(
            /*
        TipsItem ("Stabilisciti degli obiettivi realistici", R.drawable.goals),
        TipsItem ("L'idratazione è importante", R.drawable.tipsdrink),
        TipsItem ("Prova sempre qualcosa di nuovo", R.drawable.benefici_stretching_fb),
        TipsItem ("Riposati e riprenditi dopo un allenamento", R.drawable.relaxpost),
        TipsItem ("Costruisciti una routine quotidiana", R.drawable.routine),
        TipsItem ("Segui un'alimentazione corretta e sana", R.drawable.relax),
        TipsItem ("Ricordati di fare stretching", R.drawable.stretching_statico_passivo),
        TipsItem ("Mangia molta frutta e verdura", R.drawable.healtyfood),
        TipsItem ("Non saltare mai un pasto", R.drawable.saltarepasto),

             */
        TipsItem ("Scegli sempre cibi freschi e di stagione", R.drawable.cibistagione)
    )

    private var recipeHome: ImageView?= null
    private var trainHome: ImageView?= null
    private var line_chart: LineChart?= null

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
        line_chart = view.findViewById(R.id.line_chart)

        RepositoryManager.instance.loadWeights(this)

    }

    //Called in the repository manager when the data is loaded, so we can update the UI
    override fun onAsyncLoadingFinished(){
        weights = RepositoryManager.instance.dataWeights.sortedByDescending{it.date} //init all the weights
        lastWeight?.text = weights!![0].weight.toString()
        weights!!.sortedBy { it.date }
        setLineChartData()
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
        val line_entry = ArrayList<Entry>();
        var i = 0
        for(w in weights!!){
            w.weight?.let { Entry(it.toFloat(), i) }?.let { line_entry.add(it) }
            i++
        }

        //Layout Grafico
        val line_data_set = LineDataSet(line_entry, "First")
        line_data_set.color = resources.getColor(R.color.light_blue)
        line_data_set.lineWidth = 2f
        line_chart?.setDescription("")
        line_chart?.axisLeft?.textColor = resources.getColor((R.color.trasparent))
        line_chart?.axisRight?.textColor = resources.getColor((R.color.trasparent))
        line_chart?.xAxis?.setDrawLabels(false)
        line_chart?.legend?.isEnabled = false

        line_data_set.circleRadius =5f
        line_data_set.setDrawFilled(true)
        line_data_set.fillColor=resources.getColor(R.color.blue)
        line_data_set.fillAlpha= 40

        line_data_set.valueTextColor = resources.getColor(R.color.white)
        line_data_set.valueTextSize = 20f

        val data = LineData(xvalues, line_data_set)


        line_chart?.data= data
        line_chart?.setBackgroundColor(resources.getColor(R.color.trasparent))

        line_chart?.animateXY(2000, 2000)


    }

    private fun resetStepsInit(){
        //if the user click on the textView, notify to him that the long tap is requested
        stepCounter?.setOnClickListener{
            Toast.makeText(activity, "Long Tap to reset", Toast.LENGTH_SHORT).show()
        }
        //if the user long tap the textView, notify to the main activity to reset the values with "stepResetNotify()"
        stepCounter?.setOnLongClickListener{
            //Callback to the main activity
            val observer = activity as? FragHomeObserver
            observer?.stepResetNotify()

            true
        }
    }
}