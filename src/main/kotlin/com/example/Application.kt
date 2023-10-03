package com.example

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*

enum class ParentState {
    INITIAL,
    NESTED,
    EMPTY
}

data class Parent(
    val path: String = "",
    val state: ParentState = ParentState.INITIAL,
)

class ConfigValidator(
    private val config: ApplicationConfig,
    private val parent: Parent,
    private val errors: MutableList<String> = mutableListOf(),
) {
    fun obj(name: String, function: ConfigValidator.() -> Unit) {
        val path = if (parent.path == "") {
            name
        } else {
            "${parent.path}.$name"
        }
        config.propertyOrNull(name)
            ?.let {
                val objConfig = config.config(name)
                val configValidator = ConfigValidator(
                    objConfig,
                    Parent(
                        path,
                        ParentState.NESTED
                    ),
                    errors
                )
                function.invoke(configValidator)
            }
            ?: let {
                errors.add(path)
                val configValidator = ConfigValidator(
                    config,
                    Parent(
                        path,
                        ParentState.EMPTY
                    ),
                    errors
                )
                function.invoke(configValidator)
            }
    }

    fun field(parameter: String) {
        when (parent.state) {
            ParentState.INITIAL -> {
                config.tryGetString(parameter) ?: errors.add(parameter)
            }

            ParentState.NESTED -> {
                config.tryGetString(parameter) ?: errors.add("${parent.path}.$parameter")
            }

            ParentState.EMPTY -> {
                errors.add("${parent.path}.$parameter")
            }
        }
    }

    fun validate(): List<String> {
        return errors
    }
}

fun validateConfig(config: ApplicationConfig, function: ConfigValidator.() -> Unit): List<String> {
    val configValidator = ConfigValidator(config, Parent())
    configValidator.function()
    configValidator.validate()
    return configValidator.validate()
}


fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {

    val errors: List<String> = validateConfig(environment.config) {

        obj("parent1") {
            field("child1")
        }

        field("child3")

        obj("parent2") {
            obj("parent3") {
                field("child1")
            }
        }
    }
}
