package de.falkappel.example.di

import de.falkappel.example.di.DependencyInjection.inject

    class BarService {

        var fooService:FooService by inject()

        fun doFoo(){
            fooService.foo()
        }
    }