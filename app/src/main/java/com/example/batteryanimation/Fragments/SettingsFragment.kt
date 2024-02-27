package com.example.batteryanimation.Fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.batteryanimation.Activities.ChargingAlarm
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.CustomDialogRateUsBinding
import com.example.batteryanimation.databinding.FragmentSettingsBinding
import io.paperdb.Paper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : Fragment() {
    lateinit var binding:FragmentSettingsBinding
    var nonZeroRating = true
    lateinit var ratingKey: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(layoutInflater,container,false)

        binding.cardChargingNotificationId.setOnClickListener {
            startActivity(Intent(requireContext(),ChargingAlarm::class.java))
        }

        binding.linearPrivacy.setOnClickListener {
//            prEvents("privacy_btn","Privacy Policy Button from Menu Activity is pressed!")

            privacyPolicy()

        }

        binding.linearShare.setOnClickListener {
//            prEvents("share_btn","Share Button from Menu activity is pressed!")

            shareApplication()
        }

        binding.cardSelectLanguage.setOnClickListener {
//            prEvents("language_btn","Language Button from Menu activity is pressed!")

//            startActivity(Intent(this@SettingsFragment, LangungeActivity::class.java))
        }

        binding.linearRateUs.setOnClickListener {
//            prEvents("rateus_btn","Rate us Button from Menu activity is pressed!")

            showRateUsDialog()
        }

        binding.linearFeedback.setOnClickListener {
//            prEvents("feedback_btn","Feedback Button from Menu activity is pressed!")

            feedBack(4f)
        }




        return binding.root
    }

    private fun privacyPolicy() {
        val privacyPolicyUrl = "https://sites.google.com/view/ozone-team-applications-privac/home"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
        startActivity(browserIntent)
    }

    private fun feedBack(rating: Float) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:o3solutions.apps@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Rating for your app: ${rating.toString()}")
        }
        startActivity(Intent.createChooser(emailIntent, "Send feedback"))
    }

    private fun shareApplication() {
        val appPackageName = requireContext().packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

 /*   private fun handleRatings(rateUsDialog: Dialog, rating: Float) {
        when {
            rating == 0.0f -> {
                nonZeroRating = false
                Toast.makeText(
                    requireContext(),
                    R.string.kindly_rate_our_application,
                    Toast.LENGTH_SHORT
                ).show()
            }

            rating <= 1.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }

            rating <= 2.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }

            rating <= 3.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }

            rating <= 4.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }

            rating == 5.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                val appPackageName = requireContext().packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }

            else -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }


        }

        if (rating != 5.0f && nonZeroRating) {
            feedBack(rating)
        }
    }*/

    private fun setRating(key: String, value: Float) {
        Paper.book().write("$key", value)
        nonZeroRating = true
    }

    private fun showRateUsDialog() {
        ratingKey = getString(R.string.mypref)
        val dialog_binding = CustomDialogRateUsBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        val ratingExisted = Paper.book().read(ratingKey, 0.0f)
//        dialog_binding.ratingBar.rating = ratingExisted?:0.0f
//        val stars = dialog_binding.ratingBar.progressDrawable as LayerDrawable
//        stars.getDrawable(0).colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
//        stars.getDrawable(1).colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
//        stars.getDrawable(2).colorFilter = PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP)

        dialog_binding.closeDialogId.setOnClickListener {
            dialog.dismiss()
        }
    }


}