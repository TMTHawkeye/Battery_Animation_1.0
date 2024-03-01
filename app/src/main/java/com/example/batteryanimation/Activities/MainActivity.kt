package com.example.batteryanimation.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.batteryanimation.Fragments.BatteryFragment
import com.example.batteryanimation.Fragments.HomeFragment
import com.example.batteryanimation.Fragments.SettingsFragment
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.HelperClasses.Constants.OVERLAY_PERMISSION_REQUEST_CODE
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.example.batteryanimation.R
import com.example.batteryanimation.Services.BatteryService
import com.example.batteryanimation.databinding.ActivityMainBinding
import com.example.batteryanimation.databinding.CustomDialogExitAppBinding
import com.example.batteryanimation.databinding.CustomDialogPermissionAnimationDisplayBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var switchStatesAnmations: AnimationSwitchStates
    lateinit var switchStatesBattery: SwitchStates

    //    val batteryInfoViewModel: BatteryInfoViewModel by viewModel()
    private val STORAGE_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchStatesBattery = getSwitchStates()
        switchStatesAnmations = getSwitchStatesAnimations()

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
        if (!hasOverlayPermission()) {
            showOverlayPermissionDialog()
        } else {
            if (switchStatesBattery.isChargerConnectSwitchOn || switchStatesBattery.isChargerDisconnectSwitchOn
                || switchStatesBattery.isFullBatterySwitchOn || switchStatesBattery.isLowBatterySwitchOn
                || switchStatesAnmations.isactiveAnimationSwitchOn
            /*|| switchStatesAnmations.isbatteryPercentageSwitchOn
            || switchStatesAnmations.isdouble_tap_closeSwitchOn*/
            ) {
                startService()
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
//            prEvents("cancel_btn","cancel Button from exit dialog is pressed!")

            dialog.dismiss()
        }

        dialog_binding.closeDialogId.setOnClickListener {
            dialog.dismiss()
        }

        dialog_binding.cardYes.setOnClickListener {
//            prEvents("exit_btn","exit Button from exit dialog is pressed!")

            dialog.dismiss()
//            finishAffinity()
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
    }


    private fun loadfragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.continerView, fragment)
        fragmentTransaction.commit()
    }


    private var batteryFullDialog: Dialog? = null
    private var batteryLowDialog: Dialog? = null
    private var chargerConnectedDialog: Dialog? = null
    private var disconnectedDialog: Dialog? = null

    /*    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.packageName)
                )
                activity.startActivityForResult(intent, requestCode)
            }
        }*/

    private fun requestOverlayPermission() {
        if (!hasOverlayPermission()) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showOverlayPermissionDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_permission_animation_display, null)
//        val dialog = AlertDialog.Builder(this)
//            .setView(dialogView)
//            .setCancelable(false)
//            .create()

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

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
                if (hasOverlayPermission(this@MainActivity)) {
                    if (switchStatesBattery.isChargerConnectSwitchOn || switchStatesBattery.isChargerDisconnectSwitchOn
                        || switchStatesBattery.isFullBatterySwitchOn || switchStatesBattery.isLowBatterySwitchOn
                        || switchStatesAnmations.isactiveAnimationSwitchOn
                    *//* || switchStatesAnmations.isbatteryPercentageSwitchOn
                 || switchStatesAnmations.isdouble_tap_closeSwitchOn*//*
                ) {
                    startService()
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "Permission not granted!!", Toast.LENGTH_SHORT).show()
        }
    }


    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
        return true
    }*/


    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission()) {
                // Permission granted, start your service or do any necessary action
                startService()
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
            isactiveAnimationSwitchOn = true,
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
                Toast.makeText(this@MainActivity, "Read permission granted!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Read permission not granted!!",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


}