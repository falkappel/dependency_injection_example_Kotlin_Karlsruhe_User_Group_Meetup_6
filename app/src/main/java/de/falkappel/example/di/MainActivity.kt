package de.falkappel.example.di

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.falkappel.example.di.DependencyInjection.inject

class MainActivity : AppCompatActivity() {
    val barService: BarService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        barService.doFoo()
    }
}
