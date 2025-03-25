package com.example.dusuncenipaylasapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dusuncenipaylasapp.R
import com.example.dusuncenipaylasapp.model.Paylasim
import com.squareup.picasso.Picasso

class DusunceAdapter(val paylasimlistesi : ArrayList<Paylasim>) : RecyclerView.Adapter<DusunceAdapter.PaylasimHolder>(){
    class PaylasimHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaylasimHolder {
        //kod ile xml birbirine bağlanır
        val inflater = LayoutInflater.from(parent.context)
        val view= inflater.inflate(R.layout.recycyler_row, parent, false)
        return  PaylasimHolder(view)
    }

    override fun getItemCount(): Int {
        //kaç defa yazdırmalı
        return paylasimlistesi.size
    }

    override fun onBindViewHolder(holder: PaylasimHolder, position: Int) {
        val paylasim = paylasimlistesi[position]
        holder.itemView.findViewById<TextView>(R.id.recycler_row_kullanici_adi).text = paylasim.kullaniciAdi
        holder.itemView.findViewById<TextView>(R.id.recycler_row_yorum).text = paylasim.paylasilanYorum

        val imageView = holder.itemView.findViewById<ImageView>(R.id.image_view)

        if (!paylasim.gorselUrl.isNullOrBlank()) {
            imageView.visibility = View.VISIBLE
            Picasso.get().load(paylasimlistesi[position].gorselUrl).into(imageView)

        } else {
            imageView.setImageDrawable(null)
            imageView.visibility = View.GONE
        }
    }



}

