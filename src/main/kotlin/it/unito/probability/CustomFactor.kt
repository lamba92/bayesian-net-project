package it.unito.probability

import aima.core.probability.Factor
import aima.core.probability.RandomVariable

/**
 * Custom interface extending [Factor].
 */
interface CustomFactor: Factor {
    /**
     * Selecting the maximum probability of the provided variables from this Factor creating a new [CustomFactor] of
     * the remaining variables with their values updated.
     * @param vars Variables to be maxed out.
     * @return A new [CustomFactor] updated.
     */
    fun maxOut(vararg vars: RandomVariable): CustomFactor

    /**
     * Return a string containing a representation of this factor.
     */
    fun printTable(): String
}