package com.example.batteryanimation.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.HelperClasses.getCurrentDateFormatted
import com.example.batteryanimation.HelperClasses.getCurrentTime
import com.example.batteryanimation.Interfaces.DoubleClickListener
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.ModelClasses.CreatedWallpaperModel
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySetCreatedAnimationBinding
import com.google.gson.Gson
import io.paperdb.Paper

class SetCreatedAnimationActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetCreatedAnimationBinding
    var animationId: Int = 0
    private var batteryBoardcastReciver: BootReceiver? = null

    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()

    lateinit var switchStatesAnmations: AnimationSwitchStates


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetCreatedAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        switchStatesAnmations = getSwitchStatesAnimations(this@SetCreatedAnimationActivity)

        currentTimeLiveData.observe(this, Observer { time ->
            binding.timeTV.text = time
        })

        currentDateLiveData.observe(this, Observer { date ->
            binding.dateTV.text = date
        })



        binding.setAnimationIdCard.setOnClickListener {
            saveCreationState(animationId)
            saveActivityIntent()
            Toast.makeText(
                this@SetCreatedAnimationActivity,
                getString(R.string.animation_applied), Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        binding.backBtnId.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        val subintentFrom = intent.getStringExtra("intentFrom")

        updateCurrentTime()
        updateCurrentDate()

        if (subintentFrom.equals("From_Adapter")) {
            binding.setAnimationIdCard.visibility = View.VISIBLE
            binding.backBtnId.visibility = View.VISIBLE
            binding.setMydesignTitleId.visibility = View.VISIBLE
            animationId = intent.getIntExtra("selected_wallpaper_position", 0)
            val imagePath = getPathFromPreference(animationId ?: 0)
            Glide.with(this).load(imagePath).into(binding.selectedWallpaperId)

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            }
            switchStatesAnmations = getSwitchStatesAnimations(this@SetCreatedAnimationActivity)
            startFinishHandler()
            binding.setAnimationIdCard.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setMydesignTitleId.visibility = View.GONE
            animationId = getCreationState()
            val imagePath = getPathFromPreference(animationId ?: 0)
            Glide.with(this).load(imagePath).into(binding.selectedWallpaperId)

            binding.timeTV.visibility = View.VISIBLE
            binding.dateTV.visibility = View.VISIBLE

            if(switchStatesAnmations.isbatteryPercentageSwitchOn){
                binding.batteryPercentageConstrain.visibility= View.VISIBLE
                binding.batteryPercentageId.text=getBatteryPercentageFromSharedPreference().toString()
            }
            else{
                binding.batteryPercentageConstrain.visibility= View.GONE
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
    }
    private fun getBatteryPercentageFromSharedPreference(): Int {
        val sharedPreferences: SharedPreferences = getSharedPreferences(Constants.BATTERY_PREFERENCE_FILE, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("battery_percentage", 0)
    }

    fun saveActivityIntent() {
        val sharedPreferences = getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("intent", "creation")
        editor.apply()
    }

    fun saveCreationState(animationId: Int) {
        val sharedPreferences = getSharedPreferences("CreationState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("creationId", animationId)
        editor.apply()
    }

    fun getCreationState(): Int {
        val sharedPreferences = getSharedPreferences("CreationState", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("creationId", R.raw.battery_anim_2)
    }

    private fun getPathFromPreference(position: Int):String? {
        val createdWallpaperList: ArrayList<CreatedWallpaperModel>? =
            Paper.book().read("wallpaperModels")

        if (createdWallpaperList != null && position < createdWallpaperList.size) {
            val createdWallpaperModel = createdWallpaperList[position]

            val fontPath = createdWallpaperModel.fontPath ?: ""

            // Load Typeface from asset using fontPath
            val typeface = if (fontPath.isNotEmpty()) {
                Typeface.createFromAsset(assets, fontPath)
            } else {
                Typeface.DEFAULT
            }

            binding.timeTV.typeface = typeface
            binding.dateTV.typeface = typeface

            binding.timeTV.setTextColor(createdWallpaperModel.color ?: 0)
            binding.dateTV.setTextColor(createdWallpaperModel.color ?: 0)

            Glide.with(this@SetCreatedAnimationActivity)
                .load(createdWallpaperModel.imagePath)
                .into(binding.selectedWallpaperId)
            return createdWallpaperModel.imagePath
        }
        return null
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


