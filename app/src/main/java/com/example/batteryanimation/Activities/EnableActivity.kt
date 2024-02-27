package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.AnimationViewModel
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.ActivityEnableBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener{
    lateinit var binding:ActivityEnableBinding
    val animationViewModel: AnimationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEnableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animationViewModel.switchState.observe(this, Observer {
            binding.activeAnimationSwitch.isChecked = it.isactiveAnimationSwitchOn
            binding.percentageSwitch.isChecked = it.isbatteryPercentageSwitchOn
            binding.doubleTapCloseSwitch.isChecked = it.isdouble_tap_closeSwitchOn

        })

        binding.activeAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
            animationViewModel.updateSwitchState("activeAnimation", isChecked)
        }

        binding.percentageSwitch.setOnCheckedChangeListener { _, isChecked ->
            animationViewModel.updateSwitchState("batteryPercentage", isChecked)
        }

        binding.doubleTapCloseSwitch.setOnCheckedChangeListener { _, isChecked ->
            animationViewModel.updateSwitchState("double_tap_close", isChecked)
        }

        // Set up the spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_items,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.animationDurationSpinner.adapter = spinnerAdapter

        binding.animationDurationSpinner.setSelection(0)

        // Set up item selected listener for spinner
        binding.animationDurationSpinner.onItemSelectedListener = this

        // Observe changes in animation duration
        animationViewModel.switchState.observe(this, Observer { switchStates ->
            // Update spinner selection based on the animation duration
            val selectedPosition = when (switchStates.animationDuration) {
                3000 -> 0
                5000 -> 1
                10000 -> 2
                15000 -> 3
                else -> 0
            }
            binding.animationDurationSpinner.setSelection(selectedPosition)
        })

        binding.backBtnId.setOnClickListener {
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
//        Toast.makeText(this, "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()

        val duration = when (selectedItem) {
            "3 sec" -> 3000
            "5 sec" -> 5000
            "10 sec" -> 10000
            "15 sec" -> 15000
            else -> 3000
        }

        // Update animation duration
        animationViewModel.updateAnimationDuration(duration)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}