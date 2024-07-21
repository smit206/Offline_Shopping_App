package com.example.entheos_shop

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.airbnb.lottie.LottieAnimationView

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val anim = findViewById<LottieAnimationView>(R.id.app_splesh)

        Handler(Looper.getMainLooper()).postDelayed({
            anim.visibility = View.VISIBLE
            anim.playAnimation()
        },0)

        anim.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                val intent = Intent(this@splash,login::class.java)
                startActivity(intent)

                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }
}