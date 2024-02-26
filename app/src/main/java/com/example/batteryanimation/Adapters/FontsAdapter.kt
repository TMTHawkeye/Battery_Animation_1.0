package com.example.batteryanimation.Adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batteryanimation.Interfaces.FontCallback
import com.example.batteryanimation.databinding.ItemRowFontsBinding


class FontsAdapter(
    val context: Context,
    val fonts_list: Array<String>?,
    val fontCallback: FontCallback
) : RecyclerView.Adapter<FontsAdapter.viewHolder>() {
    lateinit var binding:ItemRowFontsBinding

    inner class viewHolder(val binding: ItemRowFontsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemRowFontsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fonts_list?.size?:0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        val font = "font/"
        val typeface1 =
            Typeface.createFromAsset(context.assets, font + fonts_list?.get(position))
        holder.binding.textFont.setTypeface(typeface1)


        holder.itemView.setOnClickListener(View.OnClickListener {
            Log.d("font path is : ", "onClick: " + font + fonts_list?.get(position))
            val typeface =
                Typeface.createFromAsset(context.assets, font + fonts_list?.get(position))
            if (typeface != null) {
                Log.d("font path is : ", "onClick: $typeface")
                fontCallback.addFont(typeface)
            } else {
                Log.d("font path is : ", "onClick: $typeface")
            }
        })

    }
}