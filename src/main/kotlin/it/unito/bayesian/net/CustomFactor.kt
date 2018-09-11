package it.unito.bayesian.net

import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.proposition.AssignmentProposition

class CustomFactor(val table: HashMap<Map<RandomVariable, Any>, Double>) : Factor {
    override fun contains(rv: RandomVariable?) =
            table.entries.first().key.keys.contains(rv)

    override fun getValues() = table.values.toDoubleArray()

    override fun sumOut(vararg vars: RandomVariable?): Factor {
         TODO()
    }

    override fun pointwiseProductPOS(multiplier: Factor, vararg prodVarOrder: RandomVariable): Factor {
        TODO()
    }

    override fun pointwiseProduct(multiplier: Factor): Factor {
        TODO()
    }

    override fun iterateOver(fi: Factor.Iterator) {
        TODO()
    }

    override fun iterateOver(fi: Factor.Iterator, vararg fixedValues: AssignmentProposition) {
        TODO()
    }

    override fun getArgumentVariables() = table.entries.first().key.keys

}