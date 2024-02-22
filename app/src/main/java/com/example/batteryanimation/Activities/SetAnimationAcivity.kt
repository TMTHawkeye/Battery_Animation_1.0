package com.example.batteryanimation.Activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.HelperClasses.getCurrentDateFormatted
import com.example.batteryanimation.HelperClasses.getCurrentTime
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySetAnimationAcivityBinding
import com.example.batteryanimation.databinding.CustomDialogChargerConnectedBinding
import com.example.batteryanimation.databinding.CustomDialogChargerDisconnectedBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SetAnimationAcivity : AppCompatActivity() {
    lateinit var binding: ActivitySetAnimationAcivityBinding
    var subintentFrom = ""
    //    val animationViewModel:AnimationViewModel by viewModel()
    private var batteryBoardcastReciver: BootReceiver? = null
    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()
    var animationId: Int = R.raw.battery_anim_1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAnimationAcivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateCurrentTime()
        updateCurrentDate()


        currentTimeLiveData.observe(this, Observer { time ->
            binding.timeTV.text = time
        })

        // Observe current date
        currentDateLiveData.observe(this, Observer { date ->
            binding.dateTV.text = date
        })

        val intentFrom = intent.getStringExtra("intentFrom")
        subintentFrom = intent.getStringExtra("fromAdapter")?:"from_receiver"

        if (subintentFrom.equals("From_Adapter")) {
            binding.animationsOptionsConstrain.visibility = View.VISIBLE
            binding.backBtnId.visibility = View.VISIBLE
            binding.setAnimationTitleId.visibility = View.VISIBLE
            binding.timeTV.visibility = View.GONE
            binding.dateTV.visibility = View.GONE
            animationId = intent.getIntExtra("animationId", R.raw.battery_anim_1)
        } else {
            binding.animationsOptionsConstrain.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setAnimationTitleId.visibility = View.GONE
            animationId = getAnimationState()
            binding.timeTV.visibility = View.VISIBLE
            binding.dateTV.visibility = View.VISIBLE

//            binding.timeTV.text = getCurrentTime()
//            binding.dateTV.text = getCurrentDateFormatted()



            batteryBoardcastReciver = BootReceiver(object : OnStateCharge {
                override fun charge(isCharging: Boolean) {
                    if (isCharging) {
                    } else {
                        finish()
                    }
                }
            })

            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_POWER_CONNECTED)
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
//        filter.addAction(Intent.ACTION_TIME_TICK)
            registerReceiver(batteryBoardcastReciver, filter)

        }

        binding.animationId.setAnimation(animationId)
        binding.animationId.repeatCount = LottieDrawable.INFINITE
        binding.animationId.playAnimation()

        binding.setAnimationIdCard.setOnClickListener {
            saveAnimationState(animationId)
            saveActivityIntent()
            Toast.makeText(this@SetAnimationAcivity,
                getString(R.string.animation_applied), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@SetAnimationAcivity, SubAnimationActivity::class.java).putExtra("intentFrom",intentFrom))
            finish()
        }

        binding.cancelAnimationId.setOnClickListener {
            startActivity(Intent(this@SetAnimationAcivity, SubAnimationActivity::class.java).putExtra("intentFrom",intentFrom))
            finish()
        }

        binding.previewAnimationId.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // When pressed
                    binding.backBtnId.visibility = View.GONE
                    binding.setAnimationTitleId.visibility = View.GONE
                    binding.animationsOptionsConstrain.visibility = View.GONE
                    binding.timeTV.visibility = View.VISIBLE
                    binding.dateTV.visibility = View.VISIBLE
//                    binding.timeTV.text = getCurrentTime()
//                    binding.dateTV.text = getCurrentDateFormatted()



                }
                MotionEvent.ACTION_UP -> {
                    // When released
                    binding.backBtnId.visibility = View.VISIBLE
                    binding.setAnimationTitleId.visibility = View.VISIBLE
                    binding.animationsOptionsConstrain.visibility = View.VISIBLE
                    binding.timeTV.visibility = if (subintentFrom.equals("From_Adapter")) View.GONE else View.VISIBLE
                    binding.dateTV.visibility = if (subintentFrom.equals("From_Adapter")) View.GONE else View.VISIBLE
                }
            }
            true
        }

        binding.backBtnId.setOnClickListener {
            startActivity(Intent(this@SetAnimationAcivity, SubAnimationActivity::class.java).putExtra("intentFrom",intentFrom))
            finish()
        }


//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
////                prEvents("back_btn","back Button from main activity is pressed and exit dialog is showed!")
//                if (subintentFrom.equals("From_Adapter")) {
//                    startActivity(Intent(this@SetAnimationAcivity, SubAnimationActivity::class.java).putExtra("intentFrom",intentFrom))
//                    finish()
//                } else {
////                    finishAffinity()
//                }
//            }
//
//        })
    }

//    override fun onPause() {
//        super.onPause()
//        if (!subintentFrom.equals("From_Adapter")) {
//            finishAffinity()
//        }
//    }

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

    fun saveActivityIntent(){
        val sharedPreferences = getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("intent", "animation")
        editor.apply()
    }






    private fun showChargerConnectedDialog(): Dialog {
        val dialog_binding = CustomDialogChargerConnectedBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 5000)

        dialog_binding.closeDialogId.setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }

    private fun showDisconnectedDialog(): Dialog {
        val dialog_binding = CustomDialogChargerDisconnectedBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 5000)

        dialog_binding.closeDialogId.setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }

    private fun updateCurrentTime() {
        currentTimeLiveData.value = getCurrentTime()
        // Schedule next update after 1 minute
        Handler(Looper.getMainLooper()).postDelayed({
            updateCurrentTime()
        }, 1000)
    }
    private fun updateCurrentDate() {
        currentDateLiveData.value = getCurrentDateFormatted()
    }

}