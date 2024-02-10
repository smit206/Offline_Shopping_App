package com.example.entheos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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

        val image = intent.getIntExtra("image",0)
        val text = intent.getStringExtra("text")

        logo!!.setImageResource(image)
        infotitle!!.setText(text)

        back!!.setOnClickListener {
            val intent = Intent(this@info,home::class.java)
            intent.putExtra("frag","frag2")
            startActivity(intent)
        }

        val yourBitmap:Bitmap = BitmapFactory.decodeResource(resources,image)
        blurBackground(this,layout1!!,yourBitmap,25f)

        val scaledBackgroundImage = Bitmap.createBitmap(
            yourBitmap,
            0,
            0,
            yourBitmap.width,
            yourBitmap.height,
            Matrix(),
            true
        )

        val drawable = BitmapDrawable(resources, scaledBackgroundImage)
        layout1!!.background = drawable

//        layout1!!.setBackgroundResource(image)

    }

    fun blurBackground(context: Context,view:View,bitmap: Bitmap,radius:Float):Drawable{
        val blurredBitmap = blurBitmap(context,bitmap,radius)

        val blurredDrawable = BitmapDrawable(context.resources,blurredBitmap)

        ViewCompat.setBackground(view,blurredDrawable)

        return blurredDrawable
    }

    fun blurBitmap(context: Context,bitmap: Bitmap,radius: Float):Bitmap{
        val renderScript = RenderScript.create(context)

        val input = android.renderscript.Allocation.createFromBitmap(renderScript,bitmap,android.renderscript.Allocation.MipmapControl.MIPMAP_NONE,android.renderscript.Allocation.USAGE_SCRIPT)
        val output = android.renderscript.Allocation.createTyped(renderScript,input.type)

        val script = ScriptIntrinsicBlur.create(renderScript,android.renderscript.Element.U8_4(renderScript))
        script.setInput(input)

        script.setRadius(radius)

        script.forEach(output)

        output.copyTo(bitmap)

        renderScript.destroy()

        return bitmap

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@info,home::class.java)
        intent.putExtra("frag","frag2")
        startActivity(intent)
    }
}