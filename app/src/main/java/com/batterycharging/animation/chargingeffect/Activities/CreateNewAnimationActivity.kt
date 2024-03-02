package com.batterycharging.animation.chargingeffect.Activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.batterycharging.animation.chargingeffect.Adapters.FontsAdapter
import com.batterycharging.animation.chargingeffect.HelperClasses.getCurrentDateFormatted
import com.batterycharging.animation.chargingeffect.HelperClasses.getCurrentTime
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.Interfaces.FontCallback
import com.batterycharging.animation.chargingeffect.ModelClasses.CreatedWallpaperModel
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityCreateNewAnimationBinding
import com.batterycharging.animation.chargingeffect.databinding.CustomDialogSaveCreationBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.github.dhaval2404.colorpicker.listener.DismissListener
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.ColorUtil.isDarkColor
import io.paperdb.Paper


class CreateNewAnimationActivity : BaseActivity(), FontCallback {
    lateinit var binding: ActivityCreateNewAnimationBinding
    private val PICK_IMAGE_REQUEST = 1
    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()
    private lateinit var sharedPreferences: SharedPreferences
     var fontPath : String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateCurrentTime()
        updateCurrentDate()
        fontPath= getString(R.string.default_font_path)
        sharedPreferences = getSharedPreferences("created_wallpaper_prefs", MODE_PRIVATE)

        binding.constrainAddPicId.setOnClickListener(View.OnClickListener { openGallery() })

        val assetManager: AssetManager = assets

        val fonts_list = assetManager.list("font")
        Log.d("TAG_fontlist", "onCreate: ${fonts_list?.size}")
        binding.fontsRecyclerView.layoutManager =
            LinearLayoutManager(
                this@CreateNewAnimationActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        val fragmentAdaptor = FontsAdapter(this@CreateNewAnimationActivity, fonts_list, this)
        binding.fontsRecyclerView.adapter = fragmentAdaptor


        binding.chooseColor.setOnClickListener(View.OnClickListener {
            prEvents("chooseColor","Choose Color button from CreateNewAnimationActivity is pressed!")

            ColorPickerDialog.Builder(this@CreateNewAnimationActivity)
                .setColorShape(ColorShape.CIRCLE)
                .setDefaultColor(R.color.white)


                .setColorListener(object : ColorListener {
                    override fun onColorSelected(color: Int, colorHex: String) {
                        //                                mColor = color;
                        binding.colorPickerView.setColor(color)
                        setButtonBackground(binding.chooseColor as AppCompatButton, color)
                    }
                })
                .setDismissListener(object : DismissListener {
                    override fun onDismiss() {
                        binding.colorPickerView.setVisibility(View.GONE)
                        binding.chooseColor.setVisibility(View.VISIBLE)
                        Log.d("ColorPickerDialog", "Handle dismiss event")
                    }
                })
                .show()
        })



        binding.doneId.setOnClickListener {
            prEvents("doneId","Done button from CreateNewAnimationActivity is pressed!")

            showSaveCreationDialog().show()

        }

        binding.backBtnId.setOnClickListener {
            prEvents("doneId","Done button from CreateNewAnimationActivity is pressed!")

            finish()
        }

        binding.constrainPreviewId.setOnClickListener {
            prEvents("constrainPreviewId","Preview button from CreateNewAnimationActivity is pressed!")

            setPreviewVisibilities()
        }

        binding.undoPreviewId.setOnClickListener {
            prEvents("undoPreviewId","Undo Preview button from CreateNewAnimationActivity is pressed!")

            undoPreviewVisibilities()
        }

        binding.doneId.visibility = View.VISIBLE

        currentTimeLiveData.observe(this, Observer { time ->
            binding.timeTV.text = time
        })

        currentDateLiveData.observe(this, Observer { date ->
            binding.dateTV.text = date
        })

    }

     private fun showSaveCreationDialog(): Dialog {
         val dialog_binding = CustomDialogSaveCreationBinding.inflate(layoutInflater)
         val dialog = Dialog(this)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.setCancelable(false)
         dialog.setContentView(dialog_binding.root)

         val window: Window = dialog.window!!
         window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
         window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         window.setGravity(Gravity.CENTER)


         dialog_binding.cardOk.setOnClickListener {
             if(selectedImagePath==null){
                 val drawableResourceId = R.drawable.default_custom_img
                 val drawableResourceName = resources.getResourceEntryName(drawableResourceId)
                 selectedImagePath=drawableResourceName
                 Log.d("TAG_path", "showSaveCreationDialog: $selectedImagePath")
             }
             val createdWallpaperModel = CreatedWallpaperModel(
                 fontPath,
                 binding.timeTV.currentTextColor,
                 selectedImagePath ?: ""
             )

             val wallpaperModels: ArrayList<CreatedWallpaperModel>? =
                 Paper.book().read("wallpaperModels", ArrayList())

             wallpaperModels?.add(createdWallpaperModel)

             Paper.book().write("wallpaperModels", wallpaperModels ?: ArrayList())

             dialog.dismiss()
             finish()
         }

         return dialog


     }


    private fun setButtonBackground(btn: AppCompatButton, color: Int) {
        if (isDarkColor(color)) {
            btn.setTextColor(Color.WHITE)
        } else {
            btn.setTextColor(Color.WHITE)
        }
        //        btn.setBackgroundTintList(ColorStateList.valueOf(color));
        binding.timeTV.setTextColor(color)
        binding.dateTV.setTextColor(color)
    }

    private var selectedImagePath: String? = null

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            selectedImagePath = selectedImageUri?.toString()

            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.selectedImgId)

        }
    }

    override fun addFont(typeface: Typeface?,fontPath:String?) {
        binding.timeTV.setTypeface(typeface)
        binding.dateTV.setTypeface(typeface)
        this.fontPath=fontPath
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

    private fun setPreviewVisibilities() {
        binding.backBtnId.visibility = View.GONE
        binding.newcreateTitleId.visibility = View.GONE
        binding.doneId.visibility = View.GONE

        binding.linearCardsFont.visibility = View.GONE
        binding.chooseColor.visibility = View.GONE
        binding.constrainToolsId.visibility = View.GONE

        binding.undoPreviewId.visibility = View.VISIBLE
//        binding.batteryPercentageConstrain.visibility=View.VISIBLE
    }

    private fun undoPreviewVisibilities() {
        binding.backBtnId.visibility = View.VISIBLE
        binding.newcreateTitleId.visibility = View.VISIBLE
        binding.doneId.visibility = View.VISIBLE

        binding.linearCardsFont.visibility = View.VISIBLE
        binding.chooseColor.visibility = View.VISIBLE
        binding.constrainToolsId.visibility = View.VISIBLE

        binding.undoPreviewId.visibility = View.GONE
//        binding.batteryPercentageConstrain.visibility=View.GONE
    }
}