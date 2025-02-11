package com.batterycharging.animation.chargingeffect.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.batterycharging.animation.chargingeffect.BroadCastReceivers.BootReceiver
import com.batterycharging.animation.chargingeffect.HelperClasses.Constants
import com.batterycharging.animation.chargingeffect.HelperClasses.getCurrentDateFormatted
import com.batterycharging.animation.chargingeffect.HelperClasses.getCurrentTime
import com.batterycharging.animation.chargingeffect.Interfaces.DoubleClickListener
import com.batterycharging.animation.chargingeffect.Interfaces.OnStateCharge
import com.batterycharging.animation.chargingeffect.ModelClasses.AnimationSwitchStates
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivitySetWallpaperBinding
 import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class SetWallpaperActivity : BaseActivity() {
    lateinit var binding: ActivitySetWallpaperBinding
    var animationId : String? = null
    private var batteryBoardcastReciver: BootReceiver? = null
    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()

    lateinit var  switchStatesAnmations : AnimationSwitchStates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.setAnimationIdCard.setOnClickListener {
            saveWallpaperState(animationId)
            saveActivityIntent()
            Toast.makeText(this@SetWallpaperActivity,
                getString(R.string.animation_applied), Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this@SetWallpaperActivity, WallpaperActivity::class.java).putExtra("intentFrom",subintentFrom))
            finish()
        }

        binding.cancelAnimationId.setOnClickListener {
//            startActivity(Intent(this@SetAnimationAcivity, SubAnimationActivity::class.java).putExtra("intentFrom",intentFrom))
            finish()
        }



        binding.backBtnId.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        updateCurrentTime()
        updateCurrentDate()
//        switchStatesAnmations = getSwitchStatesAnimations(this@SetWallpaperActivity)


        val subintentFrom = intent.getStringExtra("intentFrom")
        val folderName: String = "battery_wallpapers/"
        currentTimeLiveData.observe(this, Observer { time ->
            binding.timeTV.text = time
        })

        // Observe current date
        currentDateLiveData.observe(this, Observer { date ->
            binding.dateTV.text = date
        })

        if (subintentFrom.equals("From_Adapter")) {
            binding.animationsOptionsConstrain.visibility = View.VISIBLE
            binding.backBtnId.visibility = View.VISIBLE
            binding.setWallpaperTitleId.visibility = View.VISIBLE
            binding.timeTV.visibility = View.GONE
            binding.dateTV.visibility = View.GONE
            animationId = intent.getStringExtra("wallpaperPath")

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            }
            switchStatesAnmations = getSwitchStatesAnimations(this@SetWallpaperActivity)
            startFinishHandler()
            binding.animationsOptionsConstrain.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setWallpaperTitleId.visibility = View.GONE
            animationId = getWallpaperState()
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

            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_POWER_CONNECTED)
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            filter.addAction(Intent.ACTION_BATTERY_CHANGED)
//        filter.addAction(Intent.ACTION_TIME_TICK)
            registerReceiver(batteryBoardcastReciver, filter)

        }

        binding.previewAnimationId.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // When pressed
                    binding.backBtnId.visibility = View.GONE
                    binding.setWallpaperTitleId.visibility = View.GONE
                    binding.animationsOptionsConstrain.visibility = View.GONE
                    binding.timeTV.visibility = View.VISIBLE
                    binding.dateTV.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_UP -> {
                    // When released
                    binding.backBtnId.visibility = View.VISIBLE
                    binding.setWallpaperTitleId.visibility = View.VISIBLE
                    binding.animationsOptionsConstrain.visibility = View.VISIBLE
                    binding.timeTV.visibility = if (subintentFrom.equals("From_Adapter")) View.GONE else View.VISIBLE
                    binding.dateTV.visibility = if (subintentFrom.equals("From_Adapter")) View.GONE else View.VISIBLE
                }
            }
            true
        }



        Log.d("TAG", "onCreate: $animationId")
        CoroutineScope(Dispatchers.Main).launch {
            val drawable = loadImageAsync(folderName + animationId)
            binding.selectedWallpaperId.setImageDrawable(drawable)
        }

    }



    private fun getBatteryPercentageFromSharedPreference(): Int {
        // Use a specific shared preference file named "battery_preference_file"
        val sharedPreferences: SharedPreferences = getSharedPreferences(Constants.BATTERY_PREFERENCE_FILE, Context.MODE_PRIVATE)
        // Retrieve the battery percentage from shared preferences
        return sharedPreferences.getInt("battery_percentage", 0)
    }

    fun saveWallpaperState(animationId: String?) {
        val sharedPreferences = getSharedPreferences("WallpaperState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("wallpaperId", animationId)
        editor.apply()
    }
    fun getWallpaperState(): String? {
        val sharedPreferences = getSharedPreferences("WallpaperState", Context.MODE_PRIVATE)
        return sharedPreferences.getString("wallpaperId", "")
    }

    fun saveActivityIntent(){
        val sharedPreferences = getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("intent", "wallpaper")
        editor.apply()
    }

    private suspend fun loadImageAsync(filePath: String): Drawable? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream = assets.open(filePath)
                val drawable = Drawable.createFromStream(inputStream, null)
                inputStream.close()
                drawable
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
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
    }

    override fun onDestroy() {
        super.onDestroy()
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