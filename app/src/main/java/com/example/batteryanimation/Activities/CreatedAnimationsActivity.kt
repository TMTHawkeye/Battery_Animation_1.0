package com.example.batteryanimation.Activities

 import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
 import android.util.Log
 import android.view.View
 import androidx.recyclerview.widget.GridLayoutManager
 import com.example.batteryanimation.Adapters.CreatedAnimationsAdapter
 import com.example.batteryanimation.Interfaces.NoDataCallBack
 import com.example.batteryanimation.ModelClasses.CreatedWallpaperModel
 import com.example.batteryanimation.databinding.ActivityCreatedAnimationsBinding
 import io.paperdb.Paper


class CreatedAnimationsActivity : AppCompatActivity() , NoDataCallBack{
    lateinit var binding:ActivityCreatedAnimationsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreatedAnimationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrieveListFromPaperDb(){
            if(it?.size!=0) {
                Log.d("TAG_size", "onCreate: ${it?.size}")
                val layoutManager = GridLayoutManager(this, 2)
                binding.creationsRV.layoutManager = layoutManager
                binding.creationsRV.adapter =  CreatedAnimationsAdapter(this@CreatedAnimationsActivity, it, this)
            }
            else{
                binding.creationsRV.visibility= View.GONE
                binding.noCreationId.visibility=View.VISIBLE
            }
        }


        binding.backBtnId.setOnClickListener {
            finish()
        }

    }

    private fun retrieveListFromPaperDb(callBack:(ArrayList<CreatedWallpaperModel>?)->Unit){
        val wallpaperList: ArrayList<CreatedWallpaperModel>?=
            Paper.book().read("wallpaperModels", ArrayList())
        callBack(wallpaperList)
    }

    override fun noDataFound(noDataFound: Boolean) {
        if(noDataFound){
            binding.creationsRV.visibility= View.GONE
            binding.noCreationId.visibility=View.VISIBLE
        }
    }


}