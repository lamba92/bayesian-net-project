package it.unito.bayesian.net.utils

import aima.core.probability.CategoricalDistribution
import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.util.ProbabilityTable
import it.unito.bayesian.net.CustomFactor

fun kotlinExecuteMaxOut(queries: Array<RandomVariable>, hidden: Set<RandomVariable>,
                        vars: List<RandomVariable>, factors: MutableList<Factor>,
                        _identity: ProbabilityTable): CategoricalDistribution {
    var newFactor = ArrayList<Factor>().apply { factors.forEach { add(it.convertToCustom()) } }
    for (randVar in vars) {
        // if var is hidden variable then factors <- max-OUT(var, factors)
        if (hidden.contains(randVar)) {
            newFactor = kotlinMaxOut(randVar, newFactor)
        }
    }
    val f = pointwiseProduct(newFactor)
    return (f.pointwiseProductPOS(_identity, *queries) as ProbabilityTable).normalize()
}

fun kotlinMaxOut(randVar: RandomVariable, factors: MutableList<Factor>): ArrayList<Factor> {
    val maxedOutFactors = ArrayList<Factor>()
    val toMaxOut = ArrayList<Factor>()

    for (f in factors) {
        if (f.contains(randVar)) {
            toMaxOut.add(f)
        } else {
            // This factor does not contain the variable
            // so no need to sum out - see AIMA3e pg. 527.
            maxedOutFactors.add(f)
        }
    }
    maxedOutFactors.add(pointwiseProduct(toMaxOut).maxOver(randVar))

    return maxedOutFactors
}

fun Factor.maxOver(randVar: RandomVariable): Factor {
    val table = HashMap<Map<RandomVariable, Any>, Double>()
    iterateOver { possibleAssignment, value ->
        possibleAssignment.remove(randVar)
        if(!table.containsKey(possibleAssignment) || value >= table[possibleAssignment]!!)
            table[possibleAssignment] = value
    }
    return ProbabilityTable(table.values.toDoubleArray(), *table.entries.first().key.keys.toTypedArray())
}

fun pointwiseProduct(factors: List<Factor>): Factor {

    var product = factors[0]
    for (i in 1 until factors.size) {
        product = product.pointwiseProduct(factors[i])
    }

    return product
}

fun Factor.convertToCustom(): CustomFactor {
    val table = HashMap<Map<RandomVariable, Any>, Double>()
    iterateOver { possibleAssignment, probability ->
        table[possibleAssignment] = probability
    }
    return CustomFactor(table)
}