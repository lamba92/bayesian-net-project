package it.unito.bayesian.net

import aima.core.probability.CategoricalDistribution
import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.proposition.AssignmentProposition
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

class CustomProbabilityTable(val table: HashMap<HashMap<RandomVariable, Any>, Double>) : CustomFactor, CategoricalDistribution {

    override fun maxOut(vararg vars: RandomVariable): CustomFactor {
        if(vars.isEmpty()) return this
        if(!argumentVariables.containsAll(vars.toList()))
            throw IllegalArgumentException("Invalid argument. Input RVs must be contained inside the factor.")
        var f = maxOutHelper(vars[0])
        for(i in 1 until vars.size - 1){
            f = f.maxOutHelper(vars[i])
        }
        return f.normalize()
    }

    override fun iterateOver(cdi: CategoricalDistribution.Iterator?) {
        (this as Factor).iterateOver(cdi as Factor.Iterator)
    }

    override fun iterateOver(cdi: CategoricalDistribution.Iterator?, vararg fixedValues: AssignmentProposition?) {
        (this as Factor).iterateOver(cdi as Factor.Iterator, *fixedValues)
    }

    override fun contains(rv: RandomVariable?) =
            table.entries.first().key.keys.contains(rv)

    override fun getValues() = table.values.toDoubleArray()

    override fun sumOut(vararg vars: RandomVariable): Factor {
        if(vars.isEmpty()) return this
        if(!argumentVariables.containsAll(vars.toList()))
            throw IllegalArgumentException("Invalid argument. Input RVs must be contained inside the factor.")
        var f = sumOutHelper(vars[0])
        for(i in 1 until vars.size - 1){
            f = f.sumOutHelper(vars[i])
        }
        return f.normalize()
    }

    override fun pointwiseProduct(multiplier: Factor) = pointwiseProductPOS(multiplier)

    override fun pointwiseProductPOS(multiplier: Factor, vararg prodVarOrder: RandomVariable): Factor {
        val intersection = argumentVariables.intersect(multiplier.argumentVariables)
//        if(intersection.isEmpty()) throw IllegalArgumentException(
//                "Intersection between factors is empty. Factors must have at least a common random variable.")
        val multiplierTable = HashMap<Map<RandomVariable, Any>, Double>()
        multiplier.iterateOver { multiplierPossibleAssignment, multiplierValue ->
            multiplierTable[multiplierPossibleAssignment] = multiplierValue
        }
        val union = argumentVariables.union(multiplier.argumentVariables)
        val newTable = java.util.HashMap<HashMap<RandomVariable, Any>, Double>()
        table.forEach { possibleAssignment, probability ->
            val intersectionCurrentAssignment = HashMap<RandomVariable, Any>()
            intersection.forEach {
                intersectionCurrentAssignment[it] = possibleAssignment[it]!!
            }
            buildCommonRows(multiplierTable, intersectionCurrentAssignment).forEach { multiplierPossibleAssignment, multiplierProbability ->
                val newProbability = probability * multiplierProbability
                val newPossibleAssignment = HashMap<RandomVariable, Any>()
                union.forEach {
                    newPossibleAssignment[it] = possibleAssignment[it] ?: multiplierPossibleAssignment[it]!!
                }
                newTable[newPossibleAssignment] = newProbability
            }
        }
        return CustomProbabilityTable(newTable)
    }

    private fun buildCommonRows(multiplierTable: HashMap<Map<RandomVariable, Any>, Double>, intersectionCurrentAssignment: HashMap<RandomVariable, Any>): Map<Map<RandomVariable, Any>, Double> {
        return multiplierTable.filter {
            for ((itcRV, itcV) in intersectionCurrentAssignment) {
                if (it.key[itcRV] != itcV) return@filter false
            }
            return@filter true
        }
    }

    override fun iterateOver(fi: Factor.Iterator) {
        table.forEach { t, u ->
            fi.iterate(t, u)
        }
    }

    override fun iterateOver(fi: Factor.Iterator, vararg fixedValues: AssignmentProposition) {
        TODO()
    }

    override fun getArgumentVariables() = table.entries.first().key.keys

    override fun normalize(): CustomProbabilityTable{
        val sum = table.values.sum()
        if(sum != 0.0 && sum != 1.0){
            table.forEach { t, u ->
                table[t] = u / sum
            }
        }
        return this
    }

    override fun maxOutHelper(randVar: RandomVariable): CustomProbabilityTable {
        val table = HashMap<HashMap<RandomVariable, Any>, Double>()
        iterateOver { possibleAssignment, value ->
            possibleAssignment.remove(randVar)
            if(!table.containsKey(possibleAssignment) || value >= table[possibleAssignment]!!)
                table[possibleAssignment as HashMap] = value
        }
        return CustomProbabilityTable(table)
    }

    override fun sumOutHelper(rv: RandomVariable): CustomProbabilityTable {
        val subSet = argumentVariables.subtract(ArrayList<RandomVariable>().apply { add(rv) })
        val resultTable = HashMap<HashMap<RandomVariable, Any>, Double>()
        table.forEach { possibleAssignment, probability ->
            val currentSubsetAssignment = HashMap<RandomVariable, Any>()
            subSet.forEach { currentSubsetAssignment[it] = possibleAssignment[it]!! }
            if(resultTable.containsKey(currentSubsetAssignment))
                resultTable[currentSubsetAssignment] = resultTable[currentSubsetAssignment]!! + probability
            else resultTable[currentSubsetAssignment] = probability
        }
        return CustomProbabilityTable(resultTable)
    }

    override fun toString(): String {
        val asciiTable = AsciiTable()
        asciiTable.addRule()
        asciiTable.addRow(ArrayList<String>(argumentVariables.map { it.toString() }).apply { add("Prob") }).apply { setTextAlignment(TextAlignment.CENTER) }
        asciiTable.addRule()
        iterateOver { possibleAssignment, value ->
            asciiTable.addRow(ArrayList<String>(possibleAssignment.map { it.value.toString() }).apply { add("%.4f".format(value)) }).apply { setTextAlignment(TextAlignment.CENTER) }
            asciiTable.addRule()
        }
        asciiTable.addRow(ArrayList<String>().apply {
            argumentVariables.forEach { add(" ") }
            add("SUM: ${table.values.sum()}")
        }).apply { setTextAlignment(TextAlignment.CENTER) }
        asciiTable.addRule()
        return asciiTable.render()
    }

    override fun setValue(idx: Int, value: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun divideBy(divisor: CategoricalDistribution?): CategoricalDistribution {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIndex(vararg values: Any?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun multiplyBy(multiplier: CategoricalDistribution?): CategoricalDistribution {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSum(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFor(): MutableSet<RandomVariable> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun multiplyByPOS(multiplier: CategoricalDistribution?, vararg prodVarOrder: RandomVariable?): CategoricalDistribution {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getValue(vararg eventValues: Any?): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getValue(vararg eventValues: AssignmentProposition?): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun marginal(vararg vars: RandomVariable?): CategoricalDistribution {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
