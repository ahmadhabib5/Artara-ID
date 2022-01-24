package com.example.artara_id.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artara_id.databinding.HistoryItemBinding
import com.example.artara_id.feature.predict.PredictActivity
import com.example.artara_id.model.DetailCake
import com.example.artara_id.model.HistoryPlusDetail
import com.example.artara_id.model.HistoryPredict

class HistoryAdapter(
    private val context: Context,
    private val onclick: OnClickItem,
    private val onDelete: OnDelete
): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private val historyPlusDetail:ArrayList<HistoryPlusDetail> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun change(hisPlusDet : ArrayList<HistoryPlusDetail>){
        historyPlusDetail.clear()
        historyPlusDetail.addAll(hisPlusDet)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(historyPlusDetailLocal: HistoryPlusDetail, context: Context,
                 clickItem: OnClickItem, onDelete: OnDelete, position: Int) {
            Glide.with(context)
                .load(historyPlusDetailLocal.imgUrl)
                .centerCrop()
                .into(binding.IVPhoto)
            binding.apply {
                TVKey.text = historyPlusDetailLocal.key
                val classHist = historyPlusDetailLocal.classPredict.replace("_", " ")
                TVCakeName.text = classHist
                val percentNumbers = historyPlusDetailLocal.scorePredict * 100
                var percentString: String = String.format("%.1f", percentNumbers)
                percentString = "$percentString %"
                TVScore.text = percentString
                menuCake.setOnClickListener {
                    Toast.makeText(context, "Cake name : ${historyPlusDetailLocal.classPredict}", Toast.LENGTH_SHORT).show()
                }

                menuScore.setOnClickListener {
                    Toast.makeText(context, "Prediction score : ${historyPlusDetailLocal.scorePredict}", Toast.LENGTH_SHORT).show()
                }

                menuTutorial.setOnClickListener {
                    val urlUri = Uri.parse(historyPlusDetailLocal.URL)
                    val i = Intent(Intent.ACTION_VIEW, urlUri)
                    context.startActivity(i)
                }

                menuDetail.setOnClickListener {
                    clickItem.onClickItem(historyPlusDetailLocal)
                }

                menuPredictAgain.setOnClickListener {
                    val gotoPredict = Intent(context, PredictActivity::class.java)
                    context.startActivity(gotoPredict)
                }

                menuDelete.setOnClickListener {
                    onDelete.onDelete(HistoryPredict(
                            key = historyPlusDetailLocal.key,
                            imgUrl = historyPlusDetailLocal.imgUrl,
                            classPredict = historyPlusDetailLocal.classPredict,
                            scorePredict = historyPlusDetailLocal.scorePredict
                    ), position)
                    historyPlusDetail.removeAt(position)
                    notifyDataSetChanged()
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(historyPlusDetail[position], context, onclick, onDelete, position)
    }

    override fun getItemCount(): Int {
        return historyPlusDetail.size
    }

    interface OnClickItem {
        fun onClickItem(historyPlusDetail: HistoryPlusDetail)
    }

    interface OnDelete{
        fun onDelete(historyPredict: HistoryPredict, position: Int)
    }
}