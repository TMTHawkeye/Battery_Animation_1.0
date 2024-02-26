package com.example.batteryanimation

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.CustomDialogs.CustomDialogChargerConnected
import com.example.batteryanimation.Fragments.BatteryFragment
import com.example.batteryanimation.Fragments.HomeFragment
import com.example.batteryanimation.Fragments.SettingsFragment
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.example.batteryanimation.Services.BatteryService
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.ActivityMainBinding
import com.example.batteryanimation.databinding.CustomDialogBatteryFullBinding
import com.example.batteryanimation.databinding.CustomDialogChargerConnectedBinding
import com.example.batteryanimation.databinding.CustomDialogChargerDisconnectedBinding
import com.example.batteryanimation.databinding.CustomDialogLowBatteryBinding
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var switchStatesAnmations : AnimationSwitchStates
    lateinit var switchStatesBattery : SwitchStates
//    val batteryInfoViewModel: BatteryInfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchStatesBattery = getSwitchStates()
        switchStatesAnmations = getSwitchStatesAnimations()

        if (!hasOverlayPermission(this@MainActivity)) {
            requestOverlayPermission(this@MainActivity, OVERLAY_PERMISSION_REQUEST_CODE)
        } else {
            if (switchStatesBattery.isChargerConnectSwitchOn || switchStatesBattery.isChargerDisconnectSwitchOn
                || switchStatesBattery.isFullBatterySwitchOn || switchStatesBattery.isLowBatterySwitchOn
                || switchStatesAnmations.isactiveAnimationSwitchOn
                || switchStatesAnmations.isbatteryPercentageSwitchOn
                || switchStatesAnmations.isdouble_tap_closeSwitchOn
            ) {
                startService()
            }
        }
//        observeBatteryStatus()

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

   /*  fun observeBatteryStatus() {
 //        batteryInfoViewModel.isSwitchOn.observe(this@MainActivity, Observer { isSwitchOn ->
 //            if (isSwitchOn) {
                *//* batteryInfoViewModel.batteryPercentage.observe(
                     this@MainActivity,
                     Observer { batteryPercentage ->
                         if (batteryPercentage != null) {
                             if (batteryPercentage >= 51) {
                                 if (batteryFullDialog == null || !batteryFullDialog!!.isShowing) {
                                     batteryFullDialog = showBatteryFullDialog()
                                     batteryFullDialog?.show()
                                 }
                             } else if (batteryPercentage <= 20) {
                                 if (batteryLowDialog == null || !batteryLowDialog!!.isShowing) {
                                     batteryLowDialog = showBatteryLowDialog()
                                     batteryLowDialog?.show()
                                 }
                             } else {
                                 // Dismiss dialogs if not needed
                                 batteryFullDialog?.dismiss()
                                 batteryLowDialog?.dismiss()
                             }
                         }
                     }
                 )*//*
 //            }
 //        })

         var wasCharging = batteryInfoViewModel.isCharging.value ?: false

         batteryInfoViewModel.isCharging.observe(this@MainActivity, Observer { newChargingState ->
             if (newChargingState != null && newChargingState != wasCharging) {
                 wasCharging = newChargingState
                 if (newChargingState) {
                     disconnectedDialog?.dismiss()
                     if (chargerConnectedDialog == null || !chargerConnectedDialog!!.isShowing) {
                         *//*chargerConnectedDialog = showChargerConnectedDialog()
                         chargerConnectedDialog?.show()*//*
                     }
                 } else {
                     chargerConnectedDialog?.dismiss()
                     if (disconnectedDialog == null || !disconnectedDialog!!.isShowing) {
                         *//*disconnectedDialog = showDisconnectedDialog()
                         disconnectedDialog?.show()*//*
                     }
                 }
             }
         })

     }*/


    /*private fun showBatteryFullDialog() : Dialog{
        val dialog_binding = CustomDialogBatteryFullBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog_binding.closeDialogId.setOnClickListener {
//            prEvents("cancel_btn","cancel Button from exit dialog is pressed!")
            dialog.dismiss()
        }

        dialog_binding.cardOk.setOnClickListener {
            dialog.dismiss()
        }

        return dialog


    }*/

    /*    private fun showChargerConnectedDialog(): Dialog {
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
        }*/

    /*  private fun showDisconnectedDialog(): Dialog {
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
      }*/

    /* private fun showBatteryLowDialog():Dialog {
         val dialog_binding = CustomDialogLowBatteryBinding.inflate(layoutInflater)
         val dialog = Dialog(this)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.setCancelable(false)
         dialog.setContentView(dialog_binding.root)

         val window: Window = dialog.window!!
         window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
         window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         window.setGravity(Gravity.CENTER)


         dialog_binding.closeDialogId.setOnClickListener {
 //            prEvents("cancel_btn","cancel Button from exit dialog is pressed!")
             dialog.dismiss()
         }

         dialog_binding.cardOk.setOnClickListener {
             dialog.dismiss()
         }

         return dialog


     }*/
    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission(this@MainActivity)) {
                if (switchStatesBattery.isChargerConnectSwitchOn || switchStatesBattery.isChargerDisconnectSwitchOn
                    || switchStatesBattery.isFullBatterySwitchOn || switchStatesBattery.isLowBatterySwitchOn
                    || switchStatesAnmations.isactiveAnimationSwitchOn
                    || switchStatesAnmations.isbatteryPercentageSwitchOn
                    || switchStatesAnmations.isdouble_tap_closeSwitchOn
                ) {
                    startService()
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "Permission not granted!!", Toast.LENGTH_SHORT).show()
        }
    }

    private val OVERLAY_PERMISSION_REQUEST_CODE = 123

    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
        return true
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


}