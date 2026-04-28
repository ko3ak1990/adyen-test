package com.adyen.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adyen.demo.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cycler = GreenShadeCycler(
            shades = intArrayOf(
                getColor(R.color.adyen_green_500),
                getColor(R.color.adyen_green_600),
                getColor(R.color.adyen_green_700),
                getColor(R.color.adyen_green_800)
            )
        )

        binding.adyenTitle.setTextColor(cycler.current())
        binding.changeColorButton.setOnClickListener {
            binding.adyenTitle.setTextColor(cycler.next())
        }
    }
}

