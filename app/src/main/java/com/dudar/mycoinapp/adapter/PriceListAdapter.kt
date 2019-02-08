package com.dudar.mycoinapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dudar.mycoinapp.NUMBER_OF_MILLISECONDS
import com.dudar.mycoinapp.R
import com.dudar.mycoinapp.model.PriceData
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class PriceListAdapter(val items: List<PriceData>?) : RecyclerView.Adapter<PriceListAdapter.PriceDataViewHolder>() {

    lateinit var context :Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceDataViewHolder {
        context = parent.context
        return PriceDataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false))
    }

    override fun getItemCount(): Int {
        return items?.size?:0
    }

    override fun onBindViewHolder(holder: PriceDataViewHolder, position: Int) {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        holder.txtDate.text = sdf.format(items?.get(position)?.timestamp?.times(NUMBER_OF_MILLISECONDS))
        val curPrice = items?.get(position)?.price
        holder.txtPrice.text= context.getString(R.string.item_price_text,curPrice)
    }

    class PriceDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtPrice = itemView.txtPrice
        val  txtDate = itemView.txtDate
    }
}