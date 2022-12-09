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
import com.example.zadaniemobv.datas.Pub
import com.example.zadaniemobv.viewModels.NearbyPubsModel

class NearbyPubs_adapter (private val nearbyPubViewModel : NearbyPubsModel): RecyclerView.Adapter<NearbyPubs_adapter.NearbyPubsAdapterHolder>() {
    var dataset: List<Pub> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class NearbyPubsAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        val lay: CardView = view.findViewById(R.id.item_lay)
        val distance: TextView = view.findViewById(R.id.distance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyPubsAdapterHolder {
        val layout = LayoutInflater.from (parent.context).inflate (R.layout.nearby_pubs_item,parent,false)
        return NearbyPubsAdapterHolder(layout)

    }

    override fun onBindViewHolder(holder: NearbyPubsAdapterHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = item.name
        holder.distance.text="%.3f m".format(item.distance)
        holder.lay.setOnClickListener {
            nearbyPubViewModel.pub.postValue(item)
        }
    }
    override fun getItemCount(): Int = dataset.size
}