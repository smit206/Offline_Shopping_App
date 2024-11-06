package com.example.entheos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class info : AppCompatActivity() {

    var logo:ImageView? = null
    var infotitle:TextView? = null
    var infotext:TextView? = null
    var btn:Button? = null
    var back:ImageView? = null
    var layout1:RelativeLayout? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        logo = findViewById(R.id.info_logo)
        infotitle = findViewById(R.id.info_title)
        infotext = findViewById(R.id.info_text)
        btn = findViewById(R.id.btnvisit)
        layout1 = findViewById(R.id.rl3)
        back = findViewById(R.id.info_back)

        val image = intent.getStringExtra("image")
        val text = intent.getStringExtra("text")
        val background = intent.getStringExtra("image")
        val description = intent.getStringExtra("desc")
        val website = intent.getStringExtra("site")

        infotitle!!.setText(text)
        infotext!!.setText(description)

        Glide.with(this)
            .load(image)
            .into(logo!!)

        if(background != null){
            Glide.with(this)
                .asBitmap()
                .load(background)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        applyBlurredBackground(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })
        }

        back!!.setOnClickListener {
            val intent = Intent(this@info,home::class.java)
            intent.putExtra("frag","frag2")
            startActivity(intent)
        }

        btn!!.setOnClickListener {
            val webpage:Uri = Uri.parse(website)
            val intent = Intent(Intent.ACTION_VIEW,webpage)
            startActivity(intent)
        }
    }

    private fun applyBlurredBackground(bitmap: Bitmap){
        val blurredBitmap = blurBitmap(bitmap,25f)
        val scaledBackgroundImage = Bitmap.createBitmap(
            blurredBitmap,
            0,
            0,
            blurredBitmap.width,
            blurredBitmap.height,
            Matrix(),
            true
        )
        val drawable = BitmapDrawable(resources,scaledBackgroundImage)
        layout1!!.background = drawable
    }

    fun blurBitmap(bitmap: Bitmap,radius: Float):Bitmap{
        val renderScript = RenderScript.create(this)

        val input = android.renderscript.Allocation.createFromBitmap(renderScript,bitmap,android.renderscript.Allocation.MipmapControl.MIPMAP_NONE,android.renderscript.Allocation.USAGE_SCRIPT)
        val output = android.renderscript.Allocation.createTyped(renderScript,input.type)

        val script = ScriptIntrinsicBlur.create(renderScript,android.renderscript.Element.U8_4(renderScript))
        script.setInput(input)

        script.setRadius(radius)

        script.forEach(output)

        output.copyTo(bitmap)

        return bitmap

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@info,home::class.java)
        intent.putExtra("frag","frag2")
        startActivity(intent)
    }
}