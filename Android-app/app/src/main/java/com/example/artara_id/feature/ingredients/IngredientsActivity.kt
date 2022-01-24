package com.example.artara_id.feature.ingredients

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.artara_id.R
import com.example.artara_id.adapter.ResepAdapter
import com.example.artara_id.databinding.ActivityIngredientsBinding
import com.example.artara_id.feature.history.HistoryActivity
import com.example.artara_id.model.HistoryPlusDetail
import java.lang.Exception

class IngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientsBinding
    private lateinit var adapter: ResepAdapter
    private lateinit var historyPlusDetailLocal: HistoryPlusDetail
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)
        binding = ActivityIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            historyPlusDetailLocal = intent.getParcelableExtra(HistoryActivity.EXTRA_DETAIL)!!
            val source = historyPlusDetailLocal.Sumber.split(",")
            val title = source[1].split("\"")
            adapter = ResepAdapter()
            binding.apply {
                Glide.with(this@IngredientsActivity)
                    .load(historyPlusDetailLocal.imgUrl)
                    .centerCrop()
                    .into(binding.IVPhoto)
                TVValueSource.text = source[0]
                TVValueTitle.text = title[1].substring(0, title[1].length-1)
                TVCakeName.text = historyPlusDetailLocal.classPredict.replace("_", " ")
                rvIngredients.layoutManager = LinearLayoutManager(this@IngredientsActivity,
                    LinearLayoutManager.VERTICAL, false)

                rvIngredients.adapter = adapter
            }
            adapter.change(historyPlusDetailLocal.Resep.split(","))
        }catch (e: Exception){
            Toast.makeText(this@IngredientsActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}