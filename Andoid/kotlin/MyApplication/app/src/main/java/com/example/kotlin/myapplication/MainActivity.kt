package com.example.kotlin.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private external fun StringForJNI(): String;
    private external fun SumForJNI(i : Int, j : Int): Int;


    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val str = "1 + 2 = " + SumForJNI(1, 2)
        val view = findViewById<TextView>(R.id.text)
        view.setText(str)
    }
}
