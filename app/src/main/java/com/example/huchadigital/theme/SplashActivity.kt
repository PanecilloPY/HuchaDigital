package com.example.huchadigital.theme

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.huchadigital.R

@Suppress("DEPRECATION") 
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2500 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        
        setContentView(R.layout.activity_splash)

        val logoImageView: ImageView = findViewById(R.id.splash_logo)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoImageView.startAnimation(fadeInAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            
            val intent = Intent(this@SplashActivity, _root_ide_package_.com.example.huchadigital.MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
