package de.falkappel.example.di

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor

/**
 * Simple single instance dependency injection
 * @author Falk Appel
 */
object DependencyInjection {

    /**
     * inject the dependency instance lazy
     */
    inline fun <reified T : Any> inject() = DIDelegate(T::class)

    /**
     * get the dependency instance
     */
    inline fun <reified T : Any> get(): T = getInstance(T::class)

    private val instances = HashMap<String, Any>()
    private var testMode: Boolean = false

    fun <T : Any> getInstance(kClass: KClass<T>): T {
        checkTestMode("Can not createInstance instance of type ${kClass.qualifiedName!!}")
        synchronized(this) {
            val className = kClass.qualifiedName!!
            val instance = instances[className]

            instance?.let {
                if (kClass.isInstance(instance)) return cast(instance, kClass)
                else throw IllegalStateException(
                    "Failed Mapping: Instance of type ${instance::class.qualifiedName} to Class $className"
                )
            }
            val newInstance = createInstance(kClass)
            instances[className] = newInstance
            return newInstance
        }
    }


    fun addInstance(newInstance: Any) {
        checkTestMode("Can not addInstanceInternal instance of type ${newInstance::class.qualifiedName}")
        val kClass = newInstance::class
        val className = kClass.qualifiedName!!
        addInstanceInternal(className, newInstance)
    }

    fun <T : Any> addInstance(kClass: KClass<out T>, newInstance: T) {
        checkTestMode("Can not addInstanceInternal instance of type ${kClass.qualifiedName!!}")
        val className = kClass.qualifiedName!!
        when {
            !kClass.isInstance(newInstance) -> throw IllegalStateException(
                "Failed putting instance of type ${newInstance::class.qualifiedName} for Class $className"
            )
            else -> addInstanceInternal(className, newInstance)
        }
    }

    fun activateTestMode() {
        testMode = true
    }

    internal fun reset() {
        testMode = false
        instances.clear()
    }

    private fun addInstanceInternal(className: String, newInstance: Any) {
        when {
            instances.containsKey(className) -> throw IllegalStateException(
                "Failed to addInstanceInternal instance: Instance of type $className is already existing"
            )
            else -> instances[className] = newInstance
        }
    }

    private fun <T : Any> createInstance(kClass: KClass<T>): T {
        val primaryConstructor = kClass.primaryConstructor
        return when {
            primaryConstructor != null && primaryConstructor.parameters.isEmpty() -> primaryConstructor.call()
            else -> throw IllegalStateException(
                "Failed Mapping: Can not createInstance instance of type ${kClass.qualifiedName}"
            )
        }
    }

    private fun <T : Any> cast(any: Any, kClass: KClass<out T>): T {
        return kClass.javaObjectType.cast(any)!!
    }

    private fun checkTestMode(message: String) {
        when {
            testMode -> throw  IllegalStateException("Test mode is active: $message")
        }
    }

    class DIDelegate<T : Any>(private val kClass: KClass<T>) {

        private lateinit var myValue: T

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            when {
                !::myValue.isInitialized -> myValue = getInstance(kClass)
            }
            return myValue
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            myValue = value
        }
    }
}