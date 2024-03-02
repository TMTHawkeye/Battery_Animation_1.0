package com.batterycharging.animation.chargingeffect.Fragments

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.batterycharging.animation.chargingeffect.Activities.ChargingAlarm
import com.batterycharging.animation.chargingeffect.Activities.LanguageActivity
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.CustomDialogRateUsBinding
import com.batterycharging.animation.chargingeffect.databinding.FragmentSettingsBinding
import io.paperdb.Paper

class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    var nonZeroRating = true
    lateinit var ratingKey: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(layoutInflater,container,false)

        binding.cardChargingNotificationId.setOnClickListener {
            prEvents("Charging_Notification","Charging Notification Button from settings fragment is pressed!")
            startActivity(Intent(requireContext(), ChargingAlarm::class.java))
        }

        binding.linearPrivacy.setOnClickListener {
            prEvents("privacy_btn","Privacy Policy Button from settings fragment is pressed!")
            privacyPolicy()

        }

        binding.linearShare.setOnClickListener {
            prEvents("share_btn","Share Button from settings fragment is pressed!")

            shareApplication()
        }

        binding.cardSelectLanguage.setOnClickListener {
            prEvents("language_btn","Language Button from settings fragment is pressed!")
            startActivity(Intent(requireContext(), LanguageActivity::class.java))
        }

        binding.linearRateUs.setOnClickListener {
            prEvents("rateus_btn","Rate us Button from settings fragment is pressed!")

            showRateUsDialog()
        }

        binding.linearFeedback.setOnClickListener {
            prEvents("feedback_btn","Feedback Button from settings fragment is pressed!")

            feedBack(4f)
        }

        binding.linearMoreapps.setOnClickListener {
            prEvents("moreapps_btn","More Apps Button from settings fragment is pressed!")
            moreApps()
        }

        return binding.root
    }

    private fun privacyPolicy() {
        val privacyPolicyUrl = "https://sites.google.com/view/alawraq-studio-privacy-policy/home"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
        startActivity(browserIntent)
    }

    private fun feedBack(rating: Float) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:developer@alawraqstudio.com")
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
            "https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun handleRatings(
        rateUsDialog: Dialog,
        rating: Float,
        dialog_binding: CustomDialogRateUsBinding
    ) {
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
    }

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
        dialog_binding.ratingBar.setIsIndicator(false)

        val ratingExisted = Paper.book().read(ratingKey, 0.0f)
        /*if (ratingExisted!!.equals(0.0f)) {
            dialog_binding.ratingBar.setIsIndicator(false)
//            dialog_binding.linearRated.visibility = View.GONE
//            dialog_binding.linearNotRated.visibility = View.VISIBLE
        }*/
        /* else {
            dialog_binding.lowBatteryImgId.setImageDrawable(R.drawable)
            dialog_binding.rateappTxtSubtitle.text = getString(R.string.performBetter)
        }*/
        dialog_binding.ratingBar.rating = ratingExisted?:0.0f

        if (ratingExisted == 1.0f) {
            dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.low_rating_img))
        }
        else if(ratingExisted!! > 1.0f && ratingExisted <=3.0f){
            dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.medium_rating_img))
        }
        else if(ratingExisted >=4){
            dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.high_rating_img))
        }
        else{
            dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.rate_us_dialog_img))

        }

        dialog_binding.closeDialogId.setOnClickListener {
            dialog.dismiss()
        }

        dialog_binding.cardSubmit.setOnClickListener {
            prEvents("rate_btn","Rateus Button from rate us dialog is pressed!")

            val rating = dialog_binding.ratingBar.rating
            handleRatings(dialog, rating,dialog_binding)
        }

        dialog_binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating == 1.0f) {
                dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.low_rating_img))
            }
            else if(rating >1.0f && rating <=3.0f){
                dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.medium_rating_img))
            }
            else if(rating >=4){
                dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.high_rating_img))
            }
            else{
                dialog_binding.rateUsImgId.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.rate_us_dialog_img))

            }
        }


    }

    private fun moreApps(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=%22alawraq+studio%22&c=apps&hl=en&gl=US&pli=1")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=%22alawraq+studio%22&c=apps&hl=en&gl=US&pli=1")))
        }
    }


}