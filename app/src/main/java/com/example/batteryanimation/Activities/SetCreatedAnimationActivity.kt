package com.example.batteryanimation.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.HelperClasses.getCurrentDateFormatted
import com.example.batteryanimation.HelperClasses.getCurrentTime
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.ModelClasses.CreatedWallpaperModel
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySetCreatedAnimationBinding
import com.google.gson.Gson
import io.paperdb.Paper

class SetCreatedAnimationActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetCreatedAnimationBinding
    var animationId: Int = 0

    //    private var batteryBoardcastReciver: BootReceiver? = null
    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()

    lateinit var switchStatesAnmations: AnimationSwitchStates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetCreatedAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val subintentFrom = intent.getStringExtra("intentFrom")

        updateCurrentTime()
        updateCurrentDate()
        switchStatesAnmations = getSwitchStatesAnimations(this@SetCreatedAnimationActivity)

        currentTimeLiveData.observe(this, Observer { time ->
            binding.timeTV.text = time
        })

        currentDateLiveData.observe(this, Observer { date ->
            binding.dateTV.text = date
        })


        if (subintentFrom.equals("From_Adapter")) {
            binding.setAnimationIdCard.visibility = View.VISIBLE
            binding.backBtnId.visibility = View.VISIBLE
            binding.setMydesignTitleId.visibility = View.VISIBLE
            binding.timeTV.visibility = View.GONE
            binding.dateTV.visibility = View.GONE
            animationId = intent.getIntExtra("selected_wallpaper_position", 0)
            getDataFromPreference(animationId ?: 0)

        } else {
            binding.setAnimationIdCard.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setMydesignTitleId.visibility = View.GONE
//            animationId = getWallpaperState()
            binding.timeTV.visibility = View.VISIBLE
            binding.dateTV.visibility = View.VISIBLE
//
////            binding.timeTV.text = getCurrentTime()
////            binding.dateTV.text = getCurrentDateFormatted()
//
//            if(switchStatesAnmations.isbatteryPercentageSwitchOn){
//                binding.batteryPercentageConstrain.visibility= View.VISIBLE
//                binding.batteryPercentageId.text=getBatteryPercentageFromSharedPreference().toString()
//            }
//            else{
//                binding.batteryPercentageConstrain.visibility= View.GONE
//            }

//            batteryBoardcastReciver = BootReceiver(object : OnStateCharge {
//                override fun charge(isCharging: Boolean) {
//                    if (isCharging) {
//                    } else {
//                        finish()
//                    }
//                }
//            })

//            val filter = IntentFilter()
//            filter.addAction(Intent.ACTION_POWER_CONNECTED)
//            filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
////        filter.addAction(Intent.ACTION_TIME_TICK)
//            registerReceiver(batteryBoardcastReciver, filter)


        }

        binding.setAnimationIdCard.setOnClickListener {
            saveAnimationState(animationId)
            saveActivityIntent()
            Toast.makeText(
                this@SetCreatedAnimationActivity,
                getString(R.string.animation_applied), Toast.LENGTH_SHORT
            ).show()
//            startActivity(Intent(this@SetWallpaperActivity, WallpaperActivity::class.java).putExtra("intentFrom",subintentFrom))
            finish()
        }

    }

    fun saveActivityIntent() {
        val sharedPreferences = getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("intent", "creation")
        editor.apply()
    }

    fun saveAnimationState(animationId: Int) {
        val sharedPreferences = getSharedPreferences("CreationState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("creationId", animationId)
        editor.apply()
    }

    fun getAnimationState(): Int {
        val sharedPreferences = getSharedPreferences("CreationState", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("creationId", R.raw.battery_anim_2)
    }

    private fun getDataFromPreference(position: Int) {
        val createdWallpaperList: ArrayList<CreatedWallpaperModel>? =
            Paper.book().read("wallpaperModels")

//        if (createdWallpaperList != null && position < createdWallpaperList.size) {
//            val createdWallpaperModel = createdWallpaperList[position]
//
//            binding.timeTV.setTypeface(createdWallpaperModel.typeface)
//            binding.dateTV.setTypeface(createdWallpaperModel.typeface)
//            binding.timeTV.setTextColor(createdWallpaperModel.color ?: 0)
//            binding.dateTV.setTextColor(createdWallpaperModel.color ?: 0)
//            binding.selectedWallpaperId.setImageBitmap(createdWallpaperModel.bitmap)
//        }
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
        )
        return savedSwitchStateString?.let { deserializeSwitchStateAnimation(it) }
            ?: defaultSwitchState
    }

    private fun deserializeSwitchStateAnimation(switchStateString: String): AnimationSwitchStates {
        return Gson().fromJson(switchStateString, AnimationSwitchStates::class.java)
    }
}
