package com.example.batteryanimation.Activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.batteryanimation.Adapters.FontsAdapter
import com.example.batteryanimation.HelperClasses.getCurrentDateFormatted
import com.example.batteryanimation.HelperClasses.getCurrentTime
import com.example.batteryanimation.Interfaces.FontCallback
import com.example.batteryanimation.ModelClasses.CreatedWallpaperModel
import com.example.batteryanimation.R
 import com.example.batteryanimation.databinding.ActivityCreateNewAnimationBinding
import com.example.batteryanimation.databinding.CustomDialogSaveCreationBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.github.dhaval2404.colorpicker.listener.DismissListener
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.ColorUtil.isDarkColor
import io.paperdb.Paper
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateNewAnimationActivity : AppCompatActivity(), FontCallback {
    lateinit var binding: ActivityCreateNewAnimationBinding
    private val PICK_IMAGE_REQUEST = 1
    private val currentTimeLiveData = MutableLiveData<String>()
    private val currentDateLiveData = MutableLiveData<String>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateCurrentTime()
        updateCurrentDate()
        sharedPreferences = getSharedPreferences("created_wallpaper_prefs", MODE_PRIVATE)

        binding.addPicImgId.setOnClickListener(View.OnClickListener { openGallery() })

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

        binding.constrainPreviewId.setOnClickListener {
            setPreviewVisibilities()
        }

        binding.undoPreviewId.setOnClickListener {
            undoPreviewVisibilities()
        }

        binding.doneId.setOnClickListener {
            val createdWallpaperModel = CreatedWallpaperModel(
                binding.timeTV.typeface,
                binding.timeTV.currentTextColor,
                binding.selectedImgId.drawable.toBitmap()
            )

            val wallpaperModels: ArrayList<CreatedWallpaperModel>? =
                Paper.book().read("wallpaperModels", ArrayList())

            wallpaperModels?.add(createdWallpaperModel)

            Paper.book().write("wallpaperModels", wallpaperModels?:ArrayList())

//            startActivity(Intent(this@CreateNewAnimationActivity, CreatedAnimationsActivity::class.java))
            showSaveCreationDialog().show()

        }

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

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            binding.doneId.visibility = View.VISIBLE
//            binding.selectedImgId.setImageURI(selectedImageUri)

            Glide.with(this)
                .load(selectedImageUri) // Pass your image URI here
                .into(binding.selectedImgId)

            currentTimeLiveData.observe(this, Observer { time ->
                binding.timeTV.text = time
            })

            // Observe current date
            currentDateLiveData.observe(this, Observer { date ->
                binding.dateTV.text = date
            })
        }
    }

    override fun addFont(typeface: Typeface?) {
        binding.timeTV.setTypeface(typeface)
        binding.dateTV.setTypeface(typeface)

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