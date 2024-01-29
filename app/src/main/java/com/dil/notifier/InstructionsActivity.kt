package com.dil.notifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InstructionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        //Change the title of the action bar at the top
        supportActionBar?.title = "Instructions"
    }
}