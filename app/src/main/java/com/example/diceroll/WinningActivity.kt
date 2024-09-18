package com.example.diceroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.diceroll.databinding.ActivityWinningBinding

class WinningActivity : AppCompatActivity() {
    private   val binding: ActivityWinningBinding by lazy {
        ActivityWinningBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}