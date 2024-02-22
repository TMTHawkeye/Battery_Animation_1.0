package com.example.batteryanimation.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batteryanimation.Activities.SetWallpaperActivity
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ItemSubAnimationBinding
import com.example.batteryanimation.databinding.ItemWalpaperBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream


class WalpaperAdapter(private val ctxt: Context, private val wallpapersList: ArrayList<String>?) :
    RecyclerView.Adapter<WalpaperAdapter.ViewHolder>() {
    private lateinit var binding: ItemWalpaperBinding

    inner class ViewHolder(val binding: ItemWalpaperBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemWalpaperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return wallpapersList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path: String = wallpapersList?.get(position) ?: ""
        val folderName: String = "battery_wallpapers/"

        CoroutineScope(Dispatchers.Main).launch {
            val drawable = loadImageAsync(folderName + path)
            holder.binding.imageItemId.setImageDrawable(drawable)
        }

        holder.itemView.setOnClickListener {
            ctxt.startActivity(
                Intent(ctxt, SetWallpaperActivity::class.java).putExtra("wallpaperPath", path)
                    .putExtra(
                        "intentFrom","From_Adapter"
                    )
            )
        }
    }

    private suspend fun loadImageAsync(filePath: String): Drawable? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream = ctxt.assets.open(filePath)
                val drawable = Drawable.createFromStream(inputStream, null)
                inputStream.close()
                drawable
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
