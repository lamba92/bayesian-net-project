package it.unito.probability

import aima.core.probability.Factor
import aima.core.probability.RandomVariable

interface CustomFactor: Factor {
    fun maxOut(vararg vars: RandomVariable): CustomFactor
    fun printTable(): String
}