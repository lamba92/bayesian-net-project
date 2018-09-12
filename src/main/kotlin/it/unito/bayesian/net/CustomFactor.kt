package it.unito.bayesian.net

import aima.core.probability.Factor
import aima.core.probability.RandomVariable

interface CustomFactor: Factor {
    fun maxOut(vararg vars: RandomVariable): CustomFactor
    fun maxOutHelper(randVar: RandomVariable): CustomFactor
    fun sumOutHelper(rv: RandomVariable): CustomProbabilityTable
}