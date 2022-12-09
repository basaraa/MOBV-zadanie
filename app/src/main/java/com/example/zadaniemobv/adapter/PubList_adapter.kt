package com.example.zadaniemobv.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.zadaniemobv.R
import com.example.zadaniemobv.datas.*

import com.example.zadaniemobv.fragments.PubList_fragmentDirections


class PubList_adapter : RecyclerView.Adapter<PubList_adapter.PubListAdapterHolder>() {
    var dataset: List<PubUser> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class PubListAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        val lay: CardView = view.findViewById(R.id.item_lay)
        val users: TextView = view.findViewById(R.id.item_users)
        val distance: TextView = view.findViewById(R.id.item_distance)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubListAdapterHolder {
        val layout = LayoutInflater.from (parent.context).inflate (R.layout.list_item,parent,false)
        return PubListAdapterHolder(layout)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PubListAdapterHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = item.name
        holder.users.text=item.users.toString()
        if (item.distance==0.0)
            holder.distance.text=""
        else holder.distance.text="%.3f km".format((item.distance)/1000)
        holder.lay.setOnClickListener {
            val action = PubList_fragmentDirections.actionPubListFragmentToShowPub(item.id)
           holder.itemView.findNavController().navigate(action)
        }
    }


    override fun getItemCount(): Int = dataset.size





}