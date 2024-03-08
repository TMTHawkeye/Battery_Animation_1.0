package com.batterycharging.animation.chargingeffect.Adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.batterycharging.animation.chargingeffect.Activities.SetCreatedAnimationActivity
import com.batterycharging.animation.chargingeffect.Interfaces.NoDataCallBack
import com.batterycharging.animation.chargingeffect.ModelClasses.CreatedWallpaperModel
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.CustomDialogDeleteImageBinding
import com.batterycharging.animation.chargingeffect.databinding.ItemCreatedWallpaperBinding
import io.paperdb.Paper

class CreatedAnimationsAdapter(
    val ctxt: Context,
    val createdWallpaperList: ArrayList<CreatedWallpaperModel>?,
    val noDataCallBack: NoDataCallBack
) : RecyclerView.Adapter<CreatedAnimationsAdapter.viewHolder>() {
    lateinit var binding: ItemCreatedWallpaperBinding

    inner class viewHolder(val binding: ItemCreatedWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding =
            ItemCreatedWallpaperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return createdWallpaperList?.size ?: 0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val wallpaperModel = createdWallpaperList?.get(position)

        wallpaperModel?.let {
            if (wallpaperModel.imagePath.equals("default_custom_img")) {
                try {
                    Glide.with(ctxt).load(ctxt.getDrawable(R.drawable.default_custom_img))
                        .into(holder.binding.imageItemId)
                } catch (e: Exception) {
                    Log.e("CreatedAnimationsAdapter", "Error loading image: ${e.message}")
                }
            } else {
                Log.d("TAG_path", "selected Wallpaper: ${wallpaperModel.imagePath}")
                try {
                    Glide.with(ctxt)
                        .load(wallpaperModel.imagePath)
                        .error(R.drawable.battery_icon)
                        .into(holder.binding.imageItemId)
                } catch (e: Exception) {
                    Log.e("CreatedAnimationsAdapter", "Error loading image: ${e.message}")
                }
            }


            holder.itemView.setOnClickListener {
//                Log.e("CreatedAnimationsAdapter", "image path : ${wallpaperModel.imagePath}")

                Intent(ctxt, SetCreatedAnimationActivity::class.java).apply {
                    putExtra("selected_wallpaper_position", position)
                    putExtra("intentFrom", "From_Adapter")
                }.also { intent ->
                    ctxt.startActivity(intent)
                }
            }

            holder.binding.deleteItemId.setOnClickListener {
                showSaveCreationDialog(position)
            }
        }
    }

    private fun showSaveCreationDialog(position: Int) {
        val dialog_binding = CustomDialogDeleteImageBinding.inflate(LayoutInflater.from(ctxt))
        val dialog = Dialog(ctxt)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)


        dialog_binding.cardYes.setOnClickListener {
            createdWallpaperList?.removeAt(position)
            Paper.book().write("wallpaperModels", createdWallpaperList ?: ArrayList())
            notifyItemRemoved(position)
            notifyDataSetChanged()

            if (createdWallpaperList!!.isEmpty()) {
                noDataCallBack.noDataFound(true)
            }
            dialog.dismiss()
        }

        dialog_binding.cardNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

}