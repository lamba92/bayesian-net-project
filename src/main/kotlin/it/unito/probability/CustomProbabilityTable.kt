package it.unito.probability

import aima.core.probability.CategoricalDistribution
import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.proposition.AssignmentProposition
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment


/**
 * Representation of a probability table using hash maps allowing direct access to possible assignments and their values.
 * @param table The table containing the distribution.
 * @param maxedOutAssignments A map containing previous maxed out random variables on the given [table].
 */
class CustomProbabilityTable(val table: HashMap<HashMap<RandomVariable, Any>, Double>,
                             maxedOutAssignments: HashMap<RandomVariable, Any> = HashMap(),
                             finalAssignment: ArrayList<ArrayList<HashMap<RandomVariable, ArrayList<HashMap<RandomVariable, Any>>>>> = ArrayList())
    : CustomFactor, CategoricalDistribution {

    private val maxedOutAssignments = HashMap(maxedOutAssignments)
    val finalAssignment = ArrayList(finalAssignment)


    override fun maxOut(vararg vars: RandomVariable): CustomFactor {
        if(vars.isEmpty()) return this
        if(!argumentVariables.containsAll(vars.toList()))
            throw IllegalArgumentException("Invalid argument. Input RVs must be contained inside the factor.")
        val iter = vars.iterator()
        var f = maxOutHelper(iter.next())
        iter.forEachRemaining {
            f = f.maxOutHelper(it)
        }
        return f
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
        val previousAssignments = HashMap(maxedOutAssignments).apply { putAll((multiplier as CustomProbabilityTable).maxedOutAssignments)}
        return CustomProbabilityTable(newTable, previousAssignments)
    }

    private fun buildCommonRows(multiplierTable: HashMap<Map<RandomVariable, Any>, Double>,
                                intersectionCurrentAssignment: HashMap<RandomVariable, Any>)
            : Map<Map<RandomVariable, Any>, Double> {
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

    override fun normalize(): CustomProbabilityTable {
        val sum = table.values.sum()
        if(sum != 0.0 && sum != 1.0){
            table.forEach { t, u ->
                table[t] = u / sum
            }
        }
        return this
    }

    private fun maxOutHelper(randVar: RandomVariable): CustomProbabilityTable {
        val table = HashMap<HashMap<RandomVariable, Any>, Double>()
        val el = ArrayList<HashMap<RandomVariable,ArrayList<HashMap<RandomVariable, Any>>>>()
        val enumAssignment =
                HashMap<RandomVariable, ArrayList<HashMap<RandomVariable, Any>>>().apply { this.put(randVar, ArrayList()) } //singola riga

        iterateOver { possibleAssignment, value ->
            val newPossibleAssignment = HashMap(possibleAssignment).apply { remove(randVar) }
            if(!table.containsKey(newPossibleAssignment) || value > table[newPossibleAssignment]!!) {
                table[newPossibleAssignment] = value
                enumAssignment[randVar]!!.removeIf { it.entries.containsAll(newPossibleAssignment.entries) }
                enumAssignment[randVar]!!.add(HashMap(possibleAssignment))
            }
        }
        el.add(enumAssignment)
        finalAssignment.add(el)
        return CustomProbabilityTable(table, HashMap(maxedOutAssignments), ArrayList(finalAssignment))
    }

    private fun sumOutHelper(rv: RandomVariable): CustomProbabilityTable {
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

   override fun printTable(): String {
        val asciiTable = AsciiTable()
        asciiTable.addRule()
        asciiTable.addRow(ArrayList<String>(argumentVariables.map { it.toString() }).apply { add("Probability") }).apply { setTextAlignment(TextAlignment.CENTER) }
        asciiTable.addRule()
        iterateOver { possibleAssignment, value ->
            asciiTable.addRow(ArrayList<String>(possibleAssignment.map { it.value.toString() }).apply { add("%.4f".format(value)) }).apply { setTextAlignment(TextAlignment.CENTER) }
            asciiTable.addRule()
        }
        asciiTable.addRow(ArrayList<String>().apply {
            argumentVariables.forEach { _ -> add(" ") }
            add("SUM: %.4f".format(table.values.sum()))
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

    operator fun times(op: CustomProbabilityTable): CustomProbabilityTable {
        val newTable = HashMap<HashMap<RandomVariable, Any>, Double>()
        val key = HashMap<RandomVariable, Any>().apply {
            putAll(maxedOutAssignments)
            putAll(op.maxedOutAssignments)
        }
        newTable[key] = table.entries.first().value * op.table.entries.first().value
        return CustomProbabilityTable(newTable)
    }

    fun getFactorFor(evidences: List<AssignmentProposition>) =
            CustomProbabilityTable(HashMap(table).apply {
                entries.removeIf {
                    for (e in evidences) {
                        if (it.key[e.termVariable] != e.value) return@removeIf true
                    }
                    false
                }
            })

    fun exposeMaxedOutAssignment() = CustomProbabilityTable(HashMap<HashMap<RandomVariable, Any>, Double>().apply {
            this[maxedOutAssignments] = table.entries.first().value
        })

    override fun toString(): String {
        var toReturn = ""
        table.forEach { possibleAssignment, probability ->
            possibleAssignment.forEach { _, rvAssignment ->
                toReturn += if(rvAssignment as Boolean) "T" else "F"
            }
            toReturn += "=%.03f".format(probability)
            toReturn += " | "
        }
        if(argumentVariables.isNotEmpty()){
            argumentVariables.forEach {
                toReturn += "$it, "
            }
            toReturn = toReturn.dropLast(2)
        }
        else toReturn = toReturn.dropLast(3)
        return toReturn
    }
}