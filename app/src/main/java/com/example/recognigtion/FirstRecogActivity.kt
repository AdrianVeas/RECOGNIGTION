package com.example.recognigtion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class FirstRecogActivity : AppCompatActivity(), View.OnClickListener {
    var Nickname: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_recog)
        val bun = intent.extras
        Nickname = bun?.getString("nickname").toString()
    }

    override fun onClick(v: View?) {

        val intent = Intent(this, RecognigtionActivity::class.java)
        val bundle = Bundle()

        bundle.putString("nickname", Nickname)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}