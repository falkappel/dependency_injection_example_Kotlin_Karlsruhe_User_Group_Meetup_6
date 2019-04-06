package de.falkappel.example.di

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName("Test bar service")
@ExtendWith(MockKExtension::class)
internal class BarServiceTest {

    @RelaxedMockK
    private lateinit var fooService: FooService

    @InjectMockKs(overrideValues = true)
    private lateinit var barService: BarService

    @BeforeEach
    fun setUp() {
        DependencyInjection.activateTestMode()
    }

    @Test
    @DisplayName("does foo with foo service")
    fun testFoo() {
        barService.doFoo()
        verify(exactly = 1) { fooService.foo() }
    }
}