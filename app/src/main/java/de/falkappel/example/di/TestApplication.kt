package de.falkappel.example.di

import android.app.Application

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyInjection.addInstance(FooService(this))
    }
}