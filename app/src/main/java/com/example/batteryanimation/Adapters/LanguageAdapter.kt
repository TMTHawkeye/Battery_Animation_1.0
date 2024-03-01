package com.example.batteryanimation.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batteryanimation.Activities.LanguageActivity
import com.example.batteryanimation.Interfaces.SelectedLanguageCallback
import com.example.batteryanimation.ModelClasses.Language
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ItemLanguageBinding


class LanguageAdapter(val ctxt: Context, val languagesList: ArrayList<Language>, var selectedPosition:Int?, val callback: SelectedLanguageCallback) :
    RecyclerView.Adapter<LanguageAdapter.viewHolder>() {
    var languageCode="en"
    var savedPosition=selectedPosition
    var positionListner: SelectedLanguageCallback =callback

    lateinit var binding: ItemLanguageBinding

    inner class viewHolder(var binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languagesList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.languageTitleId.text = languagesList.get(position).languageName
        holder.binding.flagImgId.setImageDrawable(languagesList.get(position).languageDrawable)

        holder.itemView.setOnClickListener {
//            ctxt.startActivity(Intent(ctxt,GuideActivity::class.java))
            changeLanguage(languagesList.get(position).languageCode!!)
            setSelectedPosition(position)

        }

//        holder.itemView.setBackgroundResource(
//            if (position == selectedPosition){
//                R.drawable.selected_language_bg
//            } else 0
//        )

        if(position==selectedPosition){
            holder.binding.selectedLanguageImg.setImageDrawable(ctxt.getDrawable(R.drawable.language_selected_img))
        }
        else{
            holder.binding.selectedLanguageImg.setImageDrawable(ctxt.getDrawable(R.drawable.language_unselected_img))

        }

    }

    private fun setSelectedPosition(position: Int) {
        selectedPosition = position
        positionListner.languageSelected(position)
        notifyDataSetChanged()
    }

    fun changeLanguage(languageCode: String) {
        this.languageCode=languageCode
    }


}