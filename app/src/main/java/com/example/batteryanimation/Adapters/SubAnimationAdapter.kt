package com.example.batteryanimation.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.batteryanimation.Activities.SetAnimationAcivity
import com.example.batteryanimation.databinding.ItemCardBinding
import com.example.batteryanimation.databinding.ItemSubAnimationBinding

class SubAnimationAdapter(
    val ctxt: Context,
    val animationsList: ArrayList<Int>
) : RecyclerView.Adapter<SubAnimationAdapter.viewHolder>() {
    lateinit var binding: ItemSubAnimationBinding


    inner class viewHolder(var binding: ItemSubAnimationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemSubAnimationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)

    }

    override fun getItemCount(): Int {
        return animationsList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val holderItem = animationsList.get(position)
        holder.binding.imageItemId.setAnimation(holderItem)

        holder.binding.imageItemId.repeatCount = LottieDrawable.INFINITE
        holder.binding.imageItemId.playAnimation()

        holder.itemView.setOnClickListener {
            ctxt.startActivity(Intent(ctxt,SetAnimationAcivity::class.java).putExtra("animationId",holderItem).putExtra("fromAdapter","From_Adapter"))
        }


    }
}