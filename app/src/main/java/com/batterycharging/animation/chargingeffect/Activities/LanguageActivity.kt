package com.batterycharging.animation.chargingeffect.Activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.batterycharging.animation.chargingeffect.Adapters.LanguageAdapter
import com.batterycharging.animation.chargingeffect.HelperClasses.isFirstTimeLaunch
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.Interfaces.SelectedLanguageCallback
import com.batterycharging.animation.chargingeffect.ModelClasses.Language
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityLanguageBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import io.paperdb.Paper
import java.util.Locale

class LanguageActivity : BaseActivity(), SelectedLanguageCallback {
    lateinit var binding: ActivityLanguageBinding
    var selectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languagesList = getLanguagesList()
        val savedPosition = Paper.book().read<Int?>("LANG_POS", selectedPosition)
        val adapter = LanguageAdapter(this@LanguageActivity, languagesList, savedPosition, this)
        binding.languagesRV.layoutManager = LinearLayoutManager(this)
        binding.languagesRV.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                prEvents("back_btn","back Button from language activity is pressed!")

                if (isFirstTimeLaunch()) {
//                    finishAffinity()
                    finishAffinity()
                    System.exit(0);
                } else {
                    finish()
                }
            }
        })

        binding.backBtnId.setOnClickListener {
            prEvents("backBtnId","Back btn switch from LanguageActivity is pressed!")

            if (isFirstTimeLaunch()) {
                finishAffinity()
            } else {
                finish()
            }
        }

        binding.doneBtn.setOnClickListener {
            prEvents("doneBtn","Done btn switch from LanguageActivity is pressed!")

            if (adapter.savedPosition != -1) {
                Paper.book().write<Int?>("LANG_POS", selectedPosition)

                val locale = Locale(adapter.languageCode)
                Locale.setDefault(locale)
                val configuration: Configuration = resources.configuration
                configuration.locale = locale
                configuration.setLayoutDirection(locale)
                localeDelegate.setLocale(this, locale)
                LocaleHelper.setLocale(this, locale)

                BaseActivity().updateLocale(this as LanguageActivity, locale)

                val intent = if (isFirstTimeLaunch()) {
                    Intent(this, GuideScreenActivity::class.java)
                } else {
                    Intent(this, WelcomeActivity::class.java)

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                Paper.book().write("LangPref", false)

                startActivity(intent)
                finish()


            }
        }
    }

    fun getLanguagesList(): ArrayList<Language> {
        var languageList = ArrayList<Language>()
        languageList.add(Language("English", getDrawable(R.drawable.english_img), "en"))
        languageList.add(Language("Spanish", getDrawable(R.drawable.spanish_img), "es"))
        languageList.add(Language("French", getDrawable(R.drawable.french_img), "fr"))
        languageList.add(Language("German", getDrawable(R.drawable.german_img), "de"))
        languageList.add(Language("Urdu", getDrawable(R.drawable.urdu_img), "ur"))
        languageList.add(Language("Italian", getDrawable(R.drawable.italian_img), "it"))
//        languageList.add(Language("Arabic",getDrawable(R.drawable.saudi_arabia_flag),"ar"))
        languageList.add(Language("Arabic", getDrawable(R.drawable.arabic_img), "ar"))
//        languageList.add(Language("Korean",getDrawable(R.drawable.korean_flag),"ar"))
//        languageList.add(Language("Urdu",getDrawable(R.drawable.urdu_flag),"ar"))
//        languageList.add(Language("Portugues",getDrawable(R.drawable.portugal_flag),"ar"))
        return languageList
    }

    override fun languageSelected(position: Int) {
        this.selectedPosition = position
    }
}