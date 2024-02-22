package com.example.batteryanimation.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.HelperClasses.getCurrentDateFormatted
import com.example.batteryanimation.HelperClasses.getCurrentTime
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySetWallpaperBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class SetWallpaperActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetWallpaperBinding
    var animationId : String? = null
    private var batteryBoardcastReciver: BootReceiver? = null
    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateCurrentTime()
        updateCurrentDate()
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
            binding.animationsOptionsConstrain.visibility = View.GONE
            binding.backBtnId.visibility = View.GONE
            binding.setWallpaperTitleId.visibility = View.GONE
            animationId = getWallpaperState()
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

        binding.previewAnimationId.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // When pressed
                    binding.backBtnId.visibility = View.GONE
                    binding.setWallpaperTitleId.visibility = View.GONE
                    binding.animationsOptionsConstrain.visibility = View.GONE
                    binding.timeTV.visibility = View.VISIBLE
                    binding.dateTV.visibility = View.VISIBLE
//                    binding.timeTV.text = getCurrentTime()
//                    binding.dateTV.text = getCurrentDateFormatted()



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

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
////                prEvents("back_btn","back Button from main activity is pressed and exit dialog is showed!")
//                if (subintentFrom.equals("From_Adapter")) {
//                    startActivity(
//                        Intent(
//                            this@SetWallpaperActivity,
//                            WallpaperActivity::class.java
//                        )/*.putExtra("intentFrom", subintentFrom)*/
//                    )
//                    finish()
//                } else {
//                    finishAffinity()
//                }
//            }
//
//        })

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
}