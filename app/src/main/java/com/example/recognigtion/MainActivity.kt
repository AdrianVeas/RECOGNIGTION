package com.example.recognigtion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/*.nig*/

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),1000)
            }
        }catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_SHORT).show()
        }


    }
     fun Recognize(v: View?) {
        try{
            val intent = Intent(this, RecognizeActivity::class.java)
            startActivity(intent)
        }catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_SHORT).show()
        }

    }
    fun SingUp(v: View?) {
        try {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }
        catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_SHORT).show()
        }
    }
    fun Credits(v: View?){
        try {
            val intent = Intent(this, CreaditsActivity::class.java)
            startActivity(intent)
        }catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_SHORT).show()
        }
    }
}