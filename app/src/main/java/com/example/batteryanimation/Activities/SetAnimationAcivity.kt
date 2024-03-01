package com.example.batteryanimation.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.HelperClasses.getCurrentDateFormatted
import com.example.batteryanimation.HelperClasses.getCurrentTime
import com.example.batteryanimation.Interfaces.DoubleClickListener
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySetAnimationAcivityBinding
import com.google.gson.Gson


class SetAnimationAcivity : AppCompatActivity() {
    lateinit var binding: ActivitySetAnimationAcivityBinding
    lateinit var  switchStatesAnmations : AnimationSwitchStates
    var subintentFrom = ""
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
//            startActivity(Intent(this@SetAnimationAcivity, SubAnimationActivity::class.java).putExtra("intentFrom",intentFrom))
            finish()
        }


/*//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
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
//        })*/
    }

    override fun onResume() {
        super.onResume()
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            }
            switchStatesAnmations = getSwitchStatesAnimations(this@SetAnimationAcivity)
            startFinishHandler()
            binding.animationsOptionsConstrain.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setAnimationTitleId.visibility = View.GONE
            animationId = getAnimationState()
            binding.timeTV.visibility = View.VISIBLE
            binding.dateTV.visibility = View.VISIBLE

            if(switchStatesAnmations.isbatteryPercentageSwitchOn){
                binding.batteryPercentageConstrain.visibility=View.VISIBLE
                binding.batteryPercentageId.text=getBatteryPercentageFromSharedPreference().toString()
            }
            else{
                binding.batteryPercentageConstrain.visibility=View.GONE
            }

            if(switchStatesAnmations.isdouble_tap_closeSwitchOn) {
                binding.mainViewId.setOnClickListener(object : DoubleClickListener() {
                    override fun onSingleClick(v: View?) {}
                    override fun onDoubleClick(v: View?) {
                        finish()
                    }
                })
            }

            batteryBoardcastReciver = BootReceiver(object : OnStateCharge {
                override fun charge(isCharging: Boolean) {
                    if (isCharging) {
                    } else {
                        finish()
                    }
                }
            })
//
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_POWER_CONNECTED)
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            filter.addAction(Intent.ACTION_BATTERY_CHANGED)
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
//        getColorOfImageview()

    }

  /*  fun getColorOfImageview(){
        val drawable: Drawable = binding.animationId.drawable

        var color = resources.getColor(android.R.color.white)

        if (drawable != null) {
            color = Utils.getColorFromDrawable(drawable)
        }

        binding.timeTV.setTextColor(color)
        binding.dateTV.setTextColor(color)

    }*/


    private fun getBatteryPercentageFromSharedPreference(): Int {
        // Use a specific shared preference file named "battery_preference_file"
        val sharedPreferences: SharedPreferences = getSharedPreferences(Constants.BATTERY_PREFERENCE_FILE, Context.MODE_PRIVATE)
        // Retrieve the battery percentage from shared preferences
        return sharedPreferences.getInt("battery_percentage", 0)
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

    fun saveActivityIntent(){
        val sharedPreferences = getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("intent", "animation")
        editor.apply()
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

    fun getSwitchStatesAnimations(context: Context): AnimationSwitchStates {
        val sharedPreferences =
            context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)
        val savedSwitchStateString =
            sharedPreferences.getString(Constants.SWITCH_STATE_ANIMATION_KEY, null)
        val defaultSwitchState = AnimationSwitchStates(
            isactiveAnimationSwitchOn = false,
            isbatteryPercentageSwitchOn = false,
            isdouble_tap_closeSwitchOn = false,
            animationDuration = 3000
        )
        return savedSwitchStateString?.let { deserializeSwitchStateAnimation(it) }
            ?: defaultSwitchState
    }

    private fun deserializeSwitchStateAnimation(switchStateString: String): AnimationSwitchStates {
        return Gson().fromJson(switchStateString, AnimationSwitchStates::class.java)
    }

    private var finishHandler: Handler? = null

    override fun onPause() {
        super.onPause()
        stopFinishHandler()
    }

  /*  private fun getAnimationDuration(): Long {
        val sharedPreferences = getSharedPreferences("AnimationState", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("animationDuration", 0)
    }*/

    private fun startFinishHandler() {
        finishHandler = Handler(Looper.getMainLooper())
        finishHandler?.postDelayed({
            finish()
        }, switchStatesAnmations.animationDuration.toLong())
    }

    private fun stopFinishHandler() {
        finishHandler?.removeCallbacksAndMessages(null)
        finishHandler = null
    }



}