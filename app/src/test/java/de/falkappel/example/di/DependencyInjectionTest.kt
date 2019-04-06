package de.falkappel.example.di

import de.falkappel.example.di.DependencyInjection.get
import de.falkappel.example.di.DependencyInjection.inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@DisplayName("Test the minimal dependency injection")
internal class DependencyInjectionTest {

    @BeforeEach
    fun beforeEachTest() {
        DependencyInjection.reset()
    }

    @Nested
    @DisplayName("with reflection creation")
    inner class WithReflection {

        @Test
        @DisplayName("that injection returns the same instance")
        fun injectReturnsSameInstance() {
            val fooService1: FooService by inject()
            val fooService2: FooService by inject()
            assertThat(fooService1).isEqualTo(fooService2)
        }

        @Test
        @DisplayName("that get returns the same instance")
        fun getReturnsSameInstance() {
            val fooService1: FooService = get()
            val fooService2: FooService = get()
            assertThat(fooService1).isEqualTo(fooService2)
        }

        @Test
        @DisplayName("that get and inject returns the same instance")
        fun getAndInjectReturnsSameInstance() {
            val fooService1: FooService = get()
            val fooService2: FooService by inject()
            assertThat(fooService1).isEqualTo(fooService2)
        }
    }

    @Nested
    @DisplayName("with external created objects")
    inner class WithPreCreatedObject {

        @Test
        @DisplayName("that by inject() returns the pre created instance")
        fun returnsInsertedInstanceByInject() {
            val fooServiceCreated = FooService()
            DependencyInjection.addInstance(fooServiceCreated)
            val fooServiceResolved: FooService by inject()
            assertThat(fooServiceCreated).isEqualTo(fooServiceResolved)
        }

        @Test
        @DisplayName("that get() returns the pre created instance")
        fun returnsInsertedInstanceByGet() {
            val fooServiceCreated = FooService()
            DependencyInjection.addInstance(fooServiceCreated)
            val fooServiceResolved: FooService = get()
            assertThat(fooServiceCreated).isEqualTo(fooServiceResolved)
        }

        @Test
        @DisplayName("that double insertion of instances throws exception")
        fun doubleInsertFails() {
            val fooServiceCreated1 = FooService()
            val fooServiceCreated2 = FooService()
            DependencyInjection.addInstance(fooServiceCreated1)
            assertThrows<IllegalStateException>("double insertion of instances should throw exception") {
                DependencyInjection.addInstance(fooServiceCreated2)
            }
        }

        @Test
        @DisplayName("that insertion of instance after retrieval of an instances throws exception")
        fun insertAfterCreationFails() {
            @Suppress("UNUSED_VARIABLE")
            val fooServiceResolved: FooService = get()
            val fooServiceCreated = FooService()
            assertThrows<IllegalStateException>("double insertion of instances should throw exception") {
                DependencyInjection.addInstance(fooServiceCreated)
            }
        }
    }

    @Test
    @DisplayName("that var can be set to another object")
    fun testVarCanBeSetToOtherObject() {
        var fooService1: FooService by inject()
        val fooService2: FooService = get()
        assertThat(fooService1).isEqualTo(fooService2)
        fooService1 = FooService()
        assertThat(fooService1).isNotEqualTo(fooService2)
    }

    @Nested
    @DisplayName("with active test mode")
    inner class WithTestMode {

        @BeforeEach
        fun beforeEachTest() {
            DependencyInjection.activateTestMode()
        }

        @Test
        @DisplayName("access to injected var instance should throw exception")
        fun testAccessToInjectedReferenceVarFails() {
            @Suppress("CanBeVal") var fooService: FooService by inject()
            assertThrows<IllegalStateException>("access to instance should throw exception") {
                fooService.foo()
            }
        }

        @Test
        @DisplayName("access to injected val instance should throw exception")
        fun testAccessToInjectedReferenceValFails() {
            val fooService: FooService by inject()
            assertThrows<IllegalStateException>("access to instance should throw exception") {
                fooService.foo()
            }
        }

        @Test
        @DisplayName("get instance should throw exception")
        fun testGetReferenceFails() {
            assertThrows<IllegalStateException>("access to instance should throw exception") {
                val fooService: FooService = get()
                fooService.foo()
            }
        }

        @Test
        @DisplayName("add instance should throw exception")
        fun testAddReferenceFails() {
            assertThrows<IllegalStateException>("access to instance should throw exception") {
                DependencyInjection.addInstance(FooService())
            }
        }

        @Test
        @DisplayName("add class and instance should throw exception")
        fun testAddClassAndReferenceFails() {
            assertThrows<IllegalStateException>("access to instance should throw exception") {
                DependencyInjection.addInstance(FooService::class, FooService())
            }
        }
    }

    class FooService {
        fun foo() {}
    }
}
