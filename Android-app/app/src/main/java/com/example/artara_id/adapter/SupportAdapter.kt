package com.example.artara_id.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artara_id.R
import com.example.artara_id.databinding.ItemSupportBinding
import com.example.artara_id.model.SupportCake

class SupportAdapter(
    private val context: Context,
) : RecyclerView.Adapter<SupportAdapter.ViewHolder>() {

    private val item: ArrayList<SupportCake> = ArrayList()
    fun setList(users: ArrayList<SupportCake>) {
        item.clear()
        item.addAll(users)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemSupportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(supportCake: SupportCake, context: Context) {
            Glide.with(context)
                .load(supportCake.url)
                .centerCrop()
                .into(binding.imgItemPhoto)
            binding.apply {
                TVCakeName.text = supportCake.name
                TVCakeName.gravity = Gravity.START
                val colorID = context.resources.getColor(R.color.transparent, context.theme)
                listParent.setCardBackgroundColor(colorID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item[position], context)
    }

    override fun getItemCount(): Int {
        return item.size
    }
}