package com.example

import io.ktor.server.config.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun `No errors when all fields exists`() {
        //Given
        val config = ApplicationConfig("application.conf")

        //When
        val validateConfig = validateConfig(config) {
            obj("1") {
                field("a")
            }
        }

        //Then
        assertTrue(validateConfig.isEmpty())
    }

    @Test
    fun `Missing object and field in errors`() {
        //Given
        val config = ApplicationConfig("application.conf")
        val expectedErrors: MutableList<String> = mutableListOf("2", "2.b")

        //When
        val validateConfig = validateConfig(config) {
            obj("2") {
                field("b")
            }
        }

        //Then
        assertEquals(expectedErrors, validateConfig)
    }

    @Test
    fun `All missing objects and fields in errors`() {
        //Given
        val config = ApplicationConfig("application.conf")
        val expectedErrors: MutableList<String> = mutableListOf("2", "2.b", "d", "d.3")

        //When
        val validateConfig = validateConfig(config) {
            obj("2") {
                field("b")
            }
            obj("d") {
                field("3")
            }
        }

        //Then
        assertEquals(expectedErrors, validateConfig)
    }

    @Test
    fun `Missing field in errors`() {
        //Given
        val config = ApplicationConfig("application.conf")
        val expectedErrors: MutableList<String> = mutableListOf("b")

        //When
        val validateConfig = validateConfig(config) {
            field("b")
        }

        //Then
        assertEquals(expectedErrors, validateConfig)
    }

    @Test
    fun `Nested fields in errors`() {
        //Given
        val config = ApplicationConfig("application.conf")
        val expectedErrors: MutableList<String> = mutableListOf("2", "2.d", "2.d.b", "2.d.b.3")

        //When
        val validateConfig = validateConfig(config) {
            obj("2") {
                obj("d"){
                    obj("b"){
                        field("3")
                    }
                }
            }
        }

        //Then
        assertEquals(expectedErrors, validateConfig)
    }
}
