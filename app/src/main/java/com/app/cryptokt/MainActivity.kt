package com.app.cryptokt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.app.cryptokt.databinding.ActivityMainBinding
import com.app.cryptokt.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    val vm by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtons()
    }

    fun setupButtons(){

        binding.btnSave.setOnClickListener{
            vm.storeData("lola")
        }

        binding.btnCollect.setOnClickListener {
            vm.readData()
        }

    }
}