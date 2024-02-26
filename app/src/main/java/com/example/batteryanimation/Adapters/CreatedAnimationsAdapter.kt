package com.example.batteryanimation.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batteryanimation.Activities.CreatedAnimationsActivity
import com.example.batteryanimation.Activities.SetCreatedAnimationActivity
import com.example.batteryanimation.Interfaces.NoDataCallBack
import com.example.batteryanimation.ModelClasses.CreatedWallpaperModel
import com.example.batteryanimation.databinding.ItemCreatedWallpaperBinding
import io.paperdb.Paper

class CreatedAnimationsAdapter(
    val ctxt: Context,
    val createdWallpaperList: ArrayList<CreatedWallpaperModel>?,
    val noDataCallBack: NoDataCallBack
) : RecyclerView.Adapter<CreatedAnimationsAdapter.viewHolder>() {
    lateinit var binding:ItemCreatedWallpaperBinding

    inner class viewHolder(val binding: ItemCreatedWallpaperBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemCreatedWallpaperBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return createdWallpaperList?.size?:0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val wallpaperModel = createdWallpaperList?.get(position)

        wallpaperModel?.let {
            holder.binding.imageItemId.setImageBitmap(it.bitmap)

            holder.itemView.setOnClickListener {
                ctxt.startActivity(
                    Intent(
                        ctxt,
                        SetCreatedAnimationActivity::class.java).putExtra("selected_wallpaper_position", position)
                        .putExtra(
                        "intentFrom","From_Adapter")
                )
            }

            holder.binding.deleteItemId.setOnClickListener {
                createdWallpaperList?.removeAt(position)
                Paper.book().write("wallpaperModels", createdWallpaperList?:ArrayList())
                notifyItemRemoved(position)
                notifyDataSetChanged()

                if (createdWallpaperList?.isEmpty() == true) {
                    noDataCallBack.noDataFound(true)
                }
            }
        }
    }
}