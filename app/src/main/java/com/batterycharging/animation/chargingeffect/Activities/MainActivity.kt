package com.batterycharging.animation.chargingeffect.Activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.batterycharging.animation.chargingeffect.Fragments.BatteryFragment
import com.batterycharging.animation.chargingeffect.Fragments.HomeFragment
import com.batterycharging.animation.chargingeffect.Fragments.SettingsFragment
import com.batterycharging.animation.chargingeffect.HelperClasses.Constants
import com.batterycharging.animation.chargingeffect.HelperClasses.Constants.OVERLAY_PERMISSION_REQUEST_CODE
import com.batterycharging.animation.chargingeffect.HelperClasses.isReadStorageAllowed
import com.batterycharging.animation.chargingeffect.HelperClasses.isWriteStorageAllowed
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.HelperClasses.requestStoragePermission
import com.batterycharging.animation.chargingeffect.ModelClasses.AnimationSwitchStates
import com.batterycharging.animation.chargingeffect.ModelClasses.SwitchStates
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.Services.BatteryService
import com.batterycharging.animation.chargingeffect.databinding.ActivityMainBinding
import com.batterycharging.animation.chargingeffect.databinding.CustomDialogExitAppBinding
import com.batterycharging.animation.chargingeffect.databinding.CustomDialogPermissionAnimationDisplayBinding
import com.google.gson.Gson

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var switchStatesAnmations: AnimationSwitchStates
    lateinit var switchStatesBattery: SwitchStates

    //    val batteryInfoViewModel: BatteryInfoViewModel by viewModel()
    private val STORAGE_PERMISSION_CODE = 123
    private val NOTIFICATION_PERMISSION_CODE = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchStatesBattery = getSwitchStates()
        switchStatesAnmations = getSwitchStatesAnimations()


        if (!hasOverlayPermission()) {
            showOverlayPermissionDialog()
        } else {
//            if (switchStatesBattery.isChargerConnectSwitchOn || switchStatesBattery.isChargerDisconnectSwitchOn
//                || switchStatesBattery.isFullBatterySwitchOn || switchStatesBattery.isLowBatterySwitchOn
//                || switchStatesAnmations.isactiveAnimationSwitchOn
//            /*|| switchStatesAnmations.isbatteryPercentageSwitchOn
//            || switchStatesAnmations.isdouble_tap_closeSwitchOn*/
//            ) {
                startService()
//            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }

//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                STORAGE_PERMISSION_CODE
//            )
//        }

            if (!isReadStorageAllowed(this@MainActivity) && !isWriteStorageAllowed(this@MainActivity)) {
                requestStoragePermission(this)
            }
        }



        val selectedColor = ContextCompat.getColor(this, R.color.bottom_nav_icon_selected)

        val unselectedColor = ContextCompat.getColor(this, R.color.bottom_nav_icon_unselected)

        val iconTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(selectedColor, unselectedColor)
        )
        val textColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(selectedColor, unselectedColor)
        )


//        binding.bottomNav.itemBackground = ContextCompat.getDrawable(this, R.drawable.bottom_nav_item_background)

        binding.bottomNav.itemIconTintList = iconTintList

        binding.bottomNav.itemTextColor = textColors

        val menu = binding.bottomNav.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val iconResId = when (i) {
                0 -> R.drawable.battery_icon_nav
                1 -> R.drawable.home_icon_nav
                2 -> R.drawable.settings_icon_nav
                else -> throw IllegalArgumentException("Invalid item index")
            }
            menuItem.icon = AppCompatResources.getDrawable(this, iconResId)
        }

        binding.bottomNav.selectedItemId = R.id.home
        loadfragment(HomeFragment())



        binding.bottomNav.setOnItemSelectedListener {
            val currentFragment = getCurrentFragment()

            if (currentFragment is HomeFragment && it.itemId == R.id.home) {
                return@setOnItemSelectedListener false
            }

            when (it.itemId) {
                R.id.home -> {
                    loadfragment(HomeFragment())

                }

                R.id.battery -> {
                    loadfragment(BatteryFragment())
                }

                R.id.settings -> {
                    loadfragment(SettingsFragment())
                }

                else -> {
                    loadfragment(HomeFragment())
                }

            }

            true

        }

        // Create a callback for back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = getCurrentFragment()
                if (currentFragment is HomeFragment) {
                    showExitDialog()
//                    finish()
                } else {
                    binding.bottomNav.selectedItemId = R.id.home
                    loadfragment(HomeFragment())
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)


    }

    private fun showExitDialog() {
        val dialog_binding = CustomDialogExitAppBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()



        dialog_binding.cardNo.setOnClickListener {
            prEvents("cardNo","No btn switch from Exit Dialog is pressed!")

            dialog.dismiss()
        }

        dialog_binding.closeDialogId.setOnClickListener {
            prEvents("closeDialogId","Close btn switch from Exit Dialog is pressed!")

            dialog.dismiss()
        }

        dialog_binding.cardYes.setOnClickListener {
            prEvents("cardYes","Yes btn switch from Exit Dialog is pressed!")

            dialog.dismiss()
            finishAffinity()
            System.exit(0);
        }
    }


    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.continerView)
    }


    private fun startService() {
        val serviceIntent = Intent(this, BatteryService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        }
        else{
            startService(serviceIntent)
        }
    }

//    private fun startService() {
//        val serviceIntent = Intent(this, BatteryService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Check for Android 12 (API level 31) or higher
//            startForegroundService(serviceIntent)
//        } else {
//            startService(serviceIntent)
//        }
//    }


    private fun loadfragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.continerView, fragment)
        fragmentTransaction.commit()
    }


    private fun requestOverlayPermission() {
        if (!hasOverlayPermission()) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showOverlayPermissionDialog() {
        val dialog_binding = CustomDialogPermissionAnimationDisplayBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)


        dialog_binding.cardOk.setOnClickListener {
            requestOverlayPermission()
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission()) {
                startService()

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_CODE
                    )
                }

//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                STORAGE_PERMISSION_CODE
//            )
//        }

                if (!isReadStorageAllowed(this@MainActivity) && !isWriteStorageAllowed(this@MainActivity)) {
                    requestStoragePermission(this)
                }
            } else {
                showOverlayPermissionDialog()
            }
        }
    }

    fun getSwitchStates(): SwitchStates {
        val sharedPreferences =
            getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val savedSwitchStateString = sharedPreferences.getString(Constants.SWITCH_STATE_KEY, null)
        val defaultSwitchState = SwitchStates(
            isLowBatterySwitchOn = false,
            isFullBatterySwitchOn = false,
            isChargerConnectSwitchOn = false,
            isChargerDisconnectSwitchOn = false
        )
        return savedSwitchStateString?.let { deserializeSwitchState(it) } ?: defaultSwitchState
    }

    fun getSwitchStatesAnimations(): AnimationSwitchStates {
        val sharedPreferences =
            getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)
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

    private fun deserializeSwitchState(switchStateString: String): SwitchStates {
        return Gson().fromJson(switchStateString, SwitchStates::class.java)
    }

    private fun deserializeSwitchStateAnimation(switchStateString: String): AnimationSwitchStates {
        return Gson().fromJson(switchStateString, AnimationSwitchStates::class.java)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@MainActivity, "Read permission granted!!", Toast.LENGTH_SHORT)
//                    .show()
            } else {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Read permission not granted!!",
//                    Toast.LENGTH_SHORT
//                ).show()

            }
        }
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isReadStorageAllowed(this@MainActivity) && !isWriteStorageAllowed(this@MainActivity)) {
                    requestStoragePermission(this)
                }
            } else {
                if (!isReadStorageAllowed(this@MainActivity) && !isWriteStorageAllowed(this@MainActivity)) {
                    requestStoragePermission(this)
                }
            }
        }
    }


}