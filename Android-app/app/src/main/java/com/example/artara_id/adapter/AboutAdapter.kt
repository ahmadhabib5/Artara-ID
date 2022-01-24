package com.example.artara_id.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artara_id.R
import com.example.artara_id.databinding.ItemMemberBinding
import com.example.artara_id.model.Member

class AboutAdapter(
    private val context: Context,
) : RecyclerView.Adapter<AboutAdapter.ViewHolder>(){

    private val item: ArrayList<Member> = ArrayList()
    fun setList(users: ArrayList<Member>) {
        item.clear()
        item.addAll(users)
        notifyDataSetChanged()
    }
    class ViewHolder(private val binding:ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member, context: Context) {
            Glide.with(context)
                .load(member.url)
                .centerCrop()
                .into(binding.imgItemPhoto)
            binding.apply {
                tvItemUsername.text = member.name
                tvUniversity.text = member.university
                val colorID = context.resources.getColor(R.color.transparent, context.theme)
                listParent.setCardBackgroundColor(colorID)
                tvEmail.text = member.email
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item[position], context)
    }

    override fun getItemCount(): Int {
        return item.size
    }
}