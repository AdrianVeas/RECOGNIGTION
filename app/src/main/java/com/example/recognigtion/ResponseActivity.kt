package com.example.recognigtion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class ResponseActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var output:  String
    private lateinit var txtresponse: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        val bun = intent.extras
        txtresponse = findViewById(R.id.txtresponse)
        output = bun?.getString("output").toString()

        try{
            if (output.contains("Ruido"))
            {
                txtresponse.text = "THE USER IS: NIGMA."
            }else{
                if (output.contains("MOM"))
                {
                    txtresponse.text = "THE USER IS: MOM"
                }else{
                    if (output.contains("NO-NIGMA"))
                    {
                        txtresponse.text = "DON'T EXIST"
                    }else{
                        if (output.contains("NIGMA"))
                        {
                            txtresponse.text = "THE USER IS: NIGMA"
                        }
                    }
                }
            }
        }catch (e : Exception){
            Toast.makeText(applicationContext, "Error Response:"+e.message, Toast.LENGTH_LONG).show()
        }

    }

    override fun onClick(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}