package com.example.recognigtion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nickname = findViewById<EditText>(R.id.txtnickname)
        nickname.setText("Adrian")
        //Permissions
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),1000)
        }

    }
     fun Recognize(v: View?) {
        try{
            val intent = Intent(this, SingUpActivity::class.java)
            val bundle = Bundle()
            val nickname = findViewById<EditText>(R.id.txtnickname)
            if (nickname.text.toString() != "" ){
                bundle.putString("nickname", nickname.text.toString())
                intent.putExtras(bundle)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext, "Write your name", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_SHORT).show()
        }

    }
    fun SingUp(v: View?) {
        try {
            val intent = Intent(this, SingUpActivity::class.java)
            val bundle = Bundle()
            val nickname = findViewById<EditText>(R.id.txtnickname)
            if (nickname.text.toString() != "" ){
                bundle.putString("nickname", nickname.text.toString())
                intent.putExtras(bundle)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext, "Write your name", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_SHORT).show()
        }
    }
}