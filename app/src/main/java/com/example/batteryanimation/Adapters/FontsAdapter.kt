package com.example.batteryanimation.Adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batteryanimation.Interfaces.FontCallback
import com.example.batteryanimation.databinding.ItemRowFontsBinding

class FontsAdapter(
    private val context: Context,
    private val fontsList: Array<String>?,
    private val fontCallback: FontCallback
) : RecyclerView.Adapter<FontsAdapter.ViewHolder>() {

    private val fontPathPrefix = "font/"
    private val typefaceCache = HashMap<String, Typeface>()

    inner class ViewHolder(val binding: ItemRowFontsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowFontsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fontsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fontName = fontsList?.get(position) ?: return
        val fontPath = "$fontPathPrefix$fontName"

        val typeface = getTypeface(fontPath)
        if (typeface != null) {
            holder.binding.textFont.setTypeface(typeface)
        }

        holder.itemView.setOnClickListener {
            fontCallback.addFont(typeface,fontPath)
        }
    }

    private fun getTypeface(fontPath: String): Typeface? {
        if (typefaceCache.containsKey(fontPath)) {
            return typefaceCache[fontPath]
        }

        try {
            val typeface = Typeface.createFromAsset(context.assets, fontPath)
            typefaceCache[fontPath] = typeface
            return typeface
        } catch (e: Exception) {
            // Handle the error gracefully (e.g., log it)
            return null
        }
    }
}
