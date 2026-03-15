package com.example.debtmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DebtManagerApplication

fun main(args: Array<String>) {
    runApplication<DebtManagerApplication>(*args)
}