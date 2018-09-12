package it.unito.bayesian.net.example

import aima.core.probability.RandomVariable
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.util.RandVar
import it.unito.bayesian.net.CustomProbabilityTable
import it.unito.bayesian.net.utils.arrayOfBooleanArrays
import it.unito.bayesian.net.utils.generateRandomListOfNumbers
import java.util.Random

object CustomFactorFactory {

    val r = Random()

    fun getTestFactor(vararg rvs: String): CustomProbabilityTable{
        val table = HashMap<HashMap<RandomVariable, Any>, Double>()
        val rvList = ArrayList<RandomVariable>().apply {
            rvs.forEach { add(RandVar(it, BooleanDomain())) }
        }
        val assignmentCombinations = arrayOfBooleanArrays(rvList.size)
        val arrayOfProbabilities = generateRandomListOfNumbers(1000, assignmentCombinations.size).map {
            it.toDouble()/1000
        }
        for(k in 0 until arrayOfProbabilities.size){
            val possibleAssignment = HashMap<RandomVariable, Any>()
            for(i in 0 until rvList.size){
                possibleAssignment[rvList[i]] = assignmentCombinations[k][i]
            }
            table[possibleAssignment] = arrayOfProbabilities[k]
        }
        return CustomProbabilityTable(table)
    }
}

