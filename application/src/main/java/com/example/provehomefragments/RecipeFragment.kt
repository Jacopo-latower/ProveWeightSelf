package com.example.provehomefragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recipe_layout.*

class RecipeFragment : Fragment(), RepositoryAsyncTaskObserver {

    private lateinit var viewModel: RecipeViewModel

    private var data2: MutableList<RecipeItem>? = null
    private var recipeRv:RecyclerView? = null

    private var refreshButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_layout, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RepositoryManager.instance.loadRecipes(this)

        refreshButton = this.activity?.findViewById(R.id.refreshRecipesBtn)

        refreshButton?.setOnClickListener {
            viewModel.init()
            RepositoryManager.instance.loadRecipes(this)
        }

        //show popup menu to do SORT
        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_sortMenu -> {
                    showPopup(view)
                }
            }
        }

        //sort
        btn_sortMenu.setOnClickListener(clickListener)

        //search
        search_recipe.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val recipeListAdapterSearch = RecipeListAdapter(data2!!, activity as MainActivity)
                recipeListAdapterSearch.filter.filter(newText)
                recipeRv?.adapter = recipeListAdapterSearch
                return false
            }
        })

    }

    //Called in the repository manager when the data is loaded, so we can update the UI
    override fun onAsyncLoadingFinished(){
        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        viewModel.init()

        //Convert my List<Recipe> in a List<RecipeItem>
        val myRecipes = viewModel.getLiveRecipes().value
        data2 = listOf<RecipeItem>().toMutableList()
        if (myRecipes != null) {
            for(r in myRecipes){
                data2!!.add(RecipeItem(r.recipeName!!, r.imgUrl!! ,r.kcal?.toInt()!!,arrayToString(r.ingredients), r.description!!))
            }
        }

        recipeRv = activity?.findViewById<RecyclerView>(R.id.rv_recipes)
        recipeRv?.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        viewModel.getLiveRecipes().observe(viewLifecycleOwner, Observer{
            Log.i("data",it.toString())
            recipeRv?.adapter = RecipeListAdapter(data2!!, activity as MainActivity)
        })

    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity?.applicationContext, view)

        //sort
        popup.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.menu_namer -> {
                    val recipeListAdapterByName = RecipeListAdapter(data2!!, activity as MainActivity)
                    recipeListAdapterByName.sortName()
                    recipeRv?.adapter = recipeListAdapterByName
                    true
                };
                R.id.menu_kcal -> {
                    val recipeListAdapterByKcal = RecipeListAdapter(data2!!, activity as MainActivity)
                    recipeListAdapterByKcal.sortName()
                    recipeRv?.adapter = recipeListAdapterByKcal
                    true
                };
                else -> false
            }
        }

        popup.inflate(R.menu.popup_r)
        popup.show()
    }

    private fun arrayToString(list:List<String>) : String{
        var result:String = ""
        for (e in list){
            result = "$result \n $e"
        }
        return result
    }

}