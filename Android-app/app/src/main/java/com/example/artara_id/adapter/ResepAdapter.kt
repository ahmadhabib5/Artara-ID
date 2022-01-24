package com.example.artara_id.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.artara_id.databinding.ResepLayoutBinding

class ResepAdapter: RecyclerView.Adapter<ResepAdapter.ViewHolder>() {

    private var item: ArrayList<String> = ArrayList()

    class ViewHolder(private val binding:ResepLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(resep:String,position: Int){
            val number = "${position + 1}."
            binding.apply {
                TVIndex.text = number
                TVResep.text = resep
            }
        }

    }
    fun change(list:List<String>){
        item.clear()
        item.addAll(list)
        notifyDataSetChanged()
    }
    fun delete(){
        item.clear()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ResepLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item[position],position)
    }

    override fun getItemCount(): Int {
        return item.size
    }
}