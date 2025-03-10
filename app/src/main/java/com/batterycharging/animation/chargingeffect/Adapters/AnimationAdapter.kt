package com.batterycharging.animation.chargingeffect.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.batterycharging.animation.chargingeffect.Activities.SubAnimationActivity
import com.batterycharging.animation.chargingeffect.ModelClasses.CategoryModel
import com.batterycharging.animation.chargingeffect.databinding.ItemCardBinding

class AnimationAdapter(
    val ctxt: Context,
    val animationsList: ArrayList<CategoryModel>
) : RecyclerView.Adapter<AnimationAdapter.viewHolder>() {
    lateinit var binding: ItemCardBinding


    inner class viewHolder(var binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)

    }

    override fun getItemCount(): Int {
        return animationsList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val holderItem=animationsList.get(position)

        holder.binding.animationTV.text=holderItem.categoryName
        holder.binding.imageItemId.setImageResource(holderItem.imageResource)

//        when(holderItem.categoryName){

//            ctxt.getString(R.string.title_battery)->{
        holder.itemView.setOnClickListener {
               ctxt.startActivity(Intent(ctxt, SubAnimationActivity::class.java).putExtra("intentFrom",holderItem.categoryName))
        }

//            }
//        }

    }
}