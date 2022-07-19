package com.example.recognigtion

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.get

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

class MainActivity : AppCompatActivity() , View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onClick(v: View?) {
        try{
            val intent = Intent(this, FirstRecogActivity::class.java)
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
}