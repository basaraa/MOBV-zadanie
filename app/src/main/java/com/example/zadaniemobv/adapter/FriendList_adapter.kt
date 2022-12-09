package com.example.zadaniemobv.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.zadaniemobv.R
import com.example.zadaniemobv.datas.Friend
import com.example.zadaniemobv.fragments.FriendList_fragmentDirections

class FriendList_adapter : RecyclerView.Adapter<FriendList_adapter.FriendListAdapterHolder>() {
    var dataset: List<Friend> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }



    class FriendListAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        val lay: CardView = view.findViewById(R.id.item_lay)
        val barName: TextView = view.findViewById(R.id.item_bar_name)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListAdapterHolder {
        val layout = LayoutInflater.from (parent.context).inflate (R.layout.friend_item,parent,false)
        return FriendListAdapterHolder(layout)

    }


    override fun onBindViewHolder(holder: FriendListAdapterHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = item.friend_username
        holder.barName.text=item.pub_name
        holder.lay.setOnClickListener {
            if (item.pub_id!="") {
                val action =
                    FriendList_fragmentDirections.actionFriendListFragmentToShowPub(item.pub_id)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }
    override fun getItemCount(): Int = dataset.size
}