package com.example.provehomefragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recipe_layout.*

class RecipeFragment : Fragment() {

    private lateinit var data2: MutableList<RecipeItem>
    private lateinit var viewModel: RecipeViewModel

    var refreshButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_layout, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        viewModel.init()

        //Convert my List<Recipe> in a List<RecipeItem>
        val myRecipes = viewModel.getLiveRecipes().value
        data2 = listOf<RecipeItem>().toMutableList()
        if (myRecipes != null) {
            for(r in myRecipes){
                data2.add(RecipeItem(r.recipeName!!, r.imgUrl!! ,r.kcal?.toInt()!!,arrayToString(r.ingredients), r.description!!))
            }
        }

        val recipeRv = activity?.findViewById<RecyclerView>(R.id.rv_recipes)
        recipeRv?.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recipeRv?.adapter = RecipeListAdapter(data2, activity as MainActivity)

        viewModel.getLiveRecipes().observe(viewLifecycleOwner, {
            (recipeRv?.adapter as RecipeListAdapter).notifyDataSetChanged()
        })

        refreshButton = this.activity?.findViewById(R.id.refreshBtn)

        refreshButton?.setOnClickListener {
            viewModel.init()
            RepositoryManager.instance.loadRecipes()
        }

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_sortMenu -> {
                    showPopup(view)
                }
            }
        }

        btn_sortMenu.setOnClickListener(clickListener)

        search_recipe.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                RecipeListAdapter(data2, activity as MainActivity).filter.filter(newText)
                return false
            }
        })

    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity?.applicationContext, view)

        //sort
        popup.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.menu_namer -> {
                    RecipeListAdapter(data2, activity as MainActivity).sortName()
                    true
                };
                R.id.menu_kcal -> {
                    RecipeListAdapter(data2, activity as MainActivity).sortKcal()
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