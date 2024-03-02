package com.batterycharging.animation.chargingeffect.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.batterycharging.animation.chargingeffect.Activities.SetAnimationAcivity
import com.batterycharging.animation.chargingeffect.databinding.ItemSubAnimationBinding
 import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SubAnimationAdapter(
    val ctxt: Context,
    val animationsList: ArrayList<Int>,
    val intentFrom: String?
) : RecyclerView.Adapter<SubAnimationAdapter.viewHolder>() {
    lateinit var binding: ItemSubAnimationBinding


    inner class viewHolder(var binding: ItemSubAnimationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding =
            ItemSubAnimationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)

    }

    override fun getItemCount(): Int {
        return animationsList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val holderItem = animationsList.get(position)
        GlobalScope.launch(Dispatchers.Main) {
            kotlinx.coroutines.delay(100)

            holder.binding.imageItemId.setAnimation(holderItem)

            holder.binding.imageItemId.repeatCount = LottieDrawable.INFINITE
            holder.binding.imageItemId.playAnimation()
        }
        holder.itemView.setOnClickListener {
            ctxt.startActivity(
                Intent(ctxt, SetAnimationAcivity::class.java).putExtra(
                    "animationId",
                    holderItem
                ).putExtra("fromAdapter", "From_Adapter").putExtra("intentFrom",intentFrom)
            )
          /*  if (ctxt is Activity) {
                ctxt.finish()
            }*/
        }


    }
}