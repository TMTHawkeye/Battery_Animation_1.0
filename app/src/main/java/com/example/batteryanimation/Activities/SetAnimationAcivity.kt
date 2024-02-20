package com.example.batteryanimation.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.AnimationViewModel
import com.example.batteryanimation.databinding.ActivitySetAnimationAcivityBinding
import com.example.batteryanimation.databinding.ItemSubAnimationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetAnimationAcivity : AppCompatActivity() {
    lateinit var binding: ActivitySetAnimationAcivityBinding
    val animationViewModel:AnimationViewModel by viewModel()

    var animationId:Int=R.raw.battery_anim_1
    val intentFrom=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAnimationAcivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFrom = intent.getStringExtra("fromAdapter")

        if (intentFrom == "From_Adapter") {
            binding.animationsOptionsConstrain.visibility = View.VISIBLE
            binding.backBtnId.visibility = View.VISIBLE
            binding.setAnimationTitleId.visibility = View.VISIBLE
            animationId = intent.getIntExtra("animationId", R.raw.battery_anim_1)
        } else {
            binding.animationsOptionsConstrain.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setAnimationTitleId.visibility = View.GONE
            animationId = getAnimationState()
        }

        binding.animationId.setAnimation(animationId)
        binding.animationId.repeatCount = LottieDrawable.INFINITE
        binding.animationId.playAnimation()

        binding.setAnimationIdCard.setOnClickListener {
            saveAnimationState(animationId)
            Toast.makeText(this@SetAnimationAcivity, "Animation set!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPause() {
        super.onPause()
        if(!intentFrom.equals("From_Adapter")){
            finishAffinity()
        }
    }

    fun saveAnimationState(animationId: Int) {
        val sharedPreferences = getSharedPreferences("AnimationState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("animationId", animationId)
        editor.apply()
    }

    fun getAnimationState(): Int {
        val sharedPreferences = getSharedPreferences("AnimationState", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("animationId", R.raw.battery_anim_2)
    }



}