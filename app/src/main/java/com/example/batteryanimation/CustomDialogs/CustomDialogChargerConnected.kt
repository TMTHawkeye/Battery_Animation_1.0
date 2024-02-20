package com.example.batteryanimation.CustomDialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.batteryanimation.databinding.CustomDialogChargerConnectedBinding

class CustomDialogChargerConnected(val context: Context){
    val dialog = Dialog(context)
    val dialog_binding = CustomDialogChargerConnectedBinding.inflate(LayoutInflater.from(context))
    fun test(s: String) {
        Log.i("TAG", "test: $s")
    }
    init{

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialog.window?.setGravity(Gravity.CENTER)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showChargerConnectedDialog() {

//        val dialog = Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth)
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)
        dialog.setCancelable(false)
//        val window: Window = dialog.window!!
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window.setGravity(Gravity.CENTER)
//        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 5000)

        dialog_binding.closeDialogId.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun closeDialog(){
        dialog.dismiss()
    }

}