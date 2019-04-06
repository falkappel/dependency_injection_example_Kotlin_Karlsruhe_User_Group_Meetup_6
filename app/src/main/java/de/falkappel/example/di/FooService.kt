package de.falkappel.example.di

import android.content.Context
import android.util.Log

class FooService(context:Context) {
    fun foo() {

        Log.i("Foo", "does foo")
    }
}
