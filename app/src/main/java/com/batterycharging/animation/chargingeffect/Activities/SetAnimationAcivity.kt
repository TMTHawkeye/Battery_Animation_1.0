package com.batterycharging.animation.chargingeffect.Activities

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
import com.batterycharging.animation.chargingeffect.BroadCastReceivers.BootReceiver
import com.batterycharging.animation.chargingeffect.HelperClasses.Constants
import com.batterycharging.animation.chargingeffect.HelperClasses.getCurrentDateFormatted
import com.batterycharging.animation.chargingeffect.HelperClasses.getCurrentTime
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.Interfaces.DoubleClickListener
import com.batterycharging.animation.chargingeffect.Interfaces.OnStateCharge
import com.batterycharging.animation.chargingeffect.ModelClasses.AnimationSwitchStates
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivitySetAnimationAcivityBinding
import com.google.gson.Gson


class SetAnimationAcivity : BaseActivity() {
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

        currentDateLiveData.observe(this, Observer { date ->
            binding.dateTV.text = date
        })

        binding.previewAnimationId.setOnTouchListener { view, motionEvent ->
            prEvents("previewAnimationId","Preview btn  from SetAnimation is pressed!")

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.backBtnId.visibility = View.GONE
                    binding.setAnimationTitleId.visibility = View.GONE
                    binding.animationsOptionsConstrain.visibility = View.GONE
                    binding.timeTV.visibility = View.VISIBLE
                    binding.dateTV.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_UP -> {
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
            prEvents("backBtnId","Back btn  from SetAnimation is pressed!")

            finish()
        }
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
    }
    private fun getBatteryPercentageFromSharedPreference(): Int {
        val sharedPreferences: SharedPreferences = getSharedPreferences(Constants.BATTERY_PREFERENCE_FILE, Context.MODE_PRIVATE)
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