package it.unito.probability.bayes

import aima.core.probability.CategoricalDistribution
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesInference
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.FiniteNode
import aima.core.probability.bayes.impl.CPT
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import it.unito.probability.CustomFactor
import it.unito.probability.CustomProbabilityTable
import it.unito.probability.utils.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

open class CustomEliminationAsk(val inferenceMethod: InferenceMethod = InferenceMethod.STANDARD): BayesInference {

    enum class InferenceMethod {
        STANDARD, MPE, MAP
    }

    override fun ask(X: Array<RandomVariable>, observedEvidences: Array<AssignmentProposition>, bn: BayesianNetwork): CategoricalDistribution? {

        checkQuery(X, bn, observedEvidences)

        val (hidden, vars) = when(inferenceMethod){
            InferenceMethod.MPE -> Pair(ArrayList<RandomVariable>(), ArrayList<RandomVariable>(bn.variablesInTopologicalOrder))
            else -> calculateVariables(X, observedEvidences, bn)
        }

        val factors = ArrayList<CustomFactor>()
        for (rv in vars) {
            factors.add(0, makeFactor(rv, observedEvidences, bn))
        }

        println(vars.toString())
        return when(inferenceMethod){
            InferenceMethod.STANDARD -> exactInference((order(bn, hidden)).apply { println(this.toString()) }.apply { println("\n") }, factors)
            InferenceMethod.MPE -> mpeInference((order(bn, vars)).apply { println(this.toString()) }.apply { println("\n") }, factors);
            InferenceMethod.MAP -> mapInference((order(bn, hidden)).apply { println(this.toString()) }.apply { println("\n") }, X, factors);
        }
    }

    private fun mapInference(hidden: ArrayList<RandomVariable>, queries: Array<RandomVariable>, factors: ArrayList<CustomFactor>): CategoricalDistribution {
        var newFactors = ArrayList(factors)
        hidden.forEach {
            newFactors = sumOut(it, newFactors)
        }
        queries.forEach {
            var (newFactors, block) = maxOut(it, newFactors)
        }
        return newFactors.map { it as CustomProbabilityTable }.multiplyAll()
//FORSE ->        return newFactors.map.apply { println(this.toString()) } { it as CustomProbabilityTable }.multiplyAll()
        //return CustomProbabilityTable(HashMap())
        }

    private fun exactInference(orderedHiddenRVs: ArrayList<RandomVariable>,
                               factors: ArrayList<CustomFactor>): CategoricalDistribution {
        var newFactors = ArrayList(factors)
        for(rv in orderedHiddenRVs){
            newFactors = sumOut(rv, newFactors).apply { println(this.toString()) }.apply { println("\n") }
        }
        return (pointwiseProduct(newFactors) as CustomProbabilityTable).normalize()
    }

    private fun mpeInference(hiddenOrdered : ArrayList<RandomVariable>,
                             factors: ArrayList<CustomFactor>): CategoricalDistribution {

        var newFactors = ArrayList(factors)
        val finalAssignments = ArrayList<ArrayList<ArrayList<HashMap<RandomVariable, ArrayList<HashMap<RandomVariable, Any>>>>>>()
        for(rv in hiddenOrdered){
            val a = maxOut(rv, newFactors)
            newFactors = a.first.apply { println(this.toString()) }
            finalAssignments.add(a.second)
        }

        val tables = ArrayList<ArrayList<HashMap<RandomVariable, Any>>>()
        finalAssignments.forEach { block ->
            block.forEach { it.forEach { it.forEach { table -> tables.add(table.value) } } }
        }

        val mpeAssignments = getMpeAssignments(tables)

        return newFactors.map { it as CustomProbabilityTable}.multiplyAll()
    }

    private fun getMpeAssignments(tables: ArrayList<ArrayList<HashMap<RandomVariable, Any>>>): HashSet<Map.Entry<RandomVariable, Any>> {

        do {
            val size = tables.size - 1
            for (i in 0..size) {
                for (j in 0..size) {
                    if (i != j) {
                        val (firstTable, secondTable)
                                = intersectAssignments(tables[i], tables[j])
                        tables[i] = firstTable
                        tables[j] = secondTable
                    }
                }
            }
        } while (tables.count { it.size == 1 } != tables.size)


        val resultSet =  HashSet<Map.Entry<RandomVariable, Any>>()
        tables.forEach {
            it.forEach {
                resultSet.addAll(it.entries)
            }
        }



        resultSet.forEach { println(it) }

        return resultSet
    }

    private fun intersectAssignments(table1: ArrayList<HashMap<RandomVariable, Any>>,
                                    table2: ArrayList<HashMap<RandomVariable, Any>>) :
    Pair<ArrayList<HashMap<RandomVariable, Any>>, ArrayList<HashMap<RandomVariable, Any>>> {

        val commonColumn = table1.first().keys.intersect(table2.first().keys)
        val diff1 = table1.first().keys.minus(commonColumn)
        val diff2 = table2.first().keys.minus(commonColumn)

        if (commonColumn.isNotEmpty()) {
            val deleteSet = HashSet<Map<RandomVariable, Any>>()
            println("\n 1:$commonColumn \n 2:$diff1 \n 3:$diff2")

            println("PRIMA:\n" + table1.toString())
            println(table2.toString())

            val newTable2 = ArrayList<HashMap<RandomVariable, Any>>()
            table1.forEach { row -> deleteSet.add(row.minus(diff1)) }
            table2.filterTo(newTable2) { row ->
                deleteSet.any {
                    row.entries.containsAll(it.entries)
                }
            }

            deleteSet.clear()

            val newTable1 = ArrayList<HashMap<RandomVariable, Any>>()
            table2.forEach { row -> deleteSet.add(row.minus(diff2)) }
            table1.filterTo(newTable1) { row ->
                deleteSet.any {
                    row.entries.containsAll(it.entries)
                }
            }
            println("DOPO:\n" + newTable1.toString())
            println(newTable2.toString())
            println("----------------------------------")
            return Pair(newTable1, newTable2)
        }
        return Pair(table1, table2)
    }

    private fun checkQuery(X: Array<RandomVariable>, bn: BayesianNetwork, observedEvidences: Array<AssignmentProposition>) {
        if (inferenceMethod == InferenceMethod.STANDARD) {
            if (X.isEmpty())
                throw java.lang.IllegalArgumentException("Cannot apply elimination without a query.")
            if (!bn.variablesInTopologicalOrder.containsAll(X.toList()) || !bn.variablesInTopologicalOrder.containsAll(observedEvidences.map { it.termVariable }))
                throw java.lang.IllegalArgumentException("Cannot apply elimination on variables not inside the net.")
        } else if (inferenceMethod == InferenceMethod.MPE) {
            if (!bn.variablesInTopologicalOrder.containsAll(observedEvidences.map { it.termVariable }))
                throw java.lang.IllegalArgumentException("Cannot apply MPE elimination on variables not inside the net.")
            if (X.isNotEmpty()) println("Query array is not empty. MPE does not need any query variable. Computation will continue...")
        }
    }

    /**
     * Override this method for a more efficient implementation as
     * outlined in AIMA3e pgs. 527-28. Calculate the hidden variables from the
     * Bayesian Network. The default implementation does not perform any of
     * these.
     *
     * Two calcuations to be performed here in order to optimize iteration over
     * the Bayesian Network:<br>
     * 1. Calculate the hidden variables to be enumerated over. An optimization
     * (AIMA3e pg. 528) is to remove 'every variable that is not an ancestor of
     * a query variable or evidence variable as it is irrelevant to the query'
     * (i.e. sums to 1). 2. The subset of variables from the Bayesian Network to
     * be retained after irrelevant hidden variables have been removed.
     *
     * @param X Query random variables.
     * @param e Evidences on the network.
     * @param bn The [BayesianNetwork] on which execute the ordering.
     * @return A [Pair]<[Set]<[RandomVariable]>, [ArrayList]<[RandomVariable]>> where the first element are the relevant hidden, the second all the relevant hiddens comprising the query variables
     */
    open fun calculateVariables(X: Array<RandomVariable>, e: Array<AssignmentProposition>, bn: BayesianNetwork)
            : Pair<Set<RandomVariable>, Collection<RandomVariable>> {

        val hidden = HashSet<RandomVariable>(bn.variablesInTopologicalOrder)

        hidden.removeAll(X)
        hidden.removeAll(e.map { it.termVariable })

        val relevantRVs = ArrayList(hidden).apply { addAll(X); addAll(e.map { it.termVariable }) }

        return Pair(hidden, relevantRVs)
    }

    private fun makeFactor(rv: RandomVariable,
                           e: Array<AssignmentProposition>,
                           bn: BayesianNetwork): CustomFactor {
        val n = bn.getNode(rv) as? FiniteNode ?: throw IllegalArgumentException("Elimination-Ask only works with finite Nodes.")
        val relevantEvidences = e.filter { n.cpt.contains(it.termVariable) }
        return (n.cpt as CPT).convertToCustom().getFactorFor(relevantEvidences)
    }

    /**
     * Override this method for a more efficient implementation as
     * outlined in AIMA3e pgs. 527-28. The default implementation does not
     * perform any of these.
     * @param vars The collection [RandomVariable]s to order.
     * @param bn The network from which generate the ordering of [vars]
     */
    open fun order(bn: BayesianNetwork,
                        vars: Collection<RandomVariable>): ArrayList<RandomVariable>{
        //println(vars.toString())
        return ArrayList(vars.reversed())
    }

    private fun sumOut(rv: RandomVariable, factors: List<CustomFactor>): ArrayList<CustomFactor> {
        val summedOutFactors = ArrayList<CustomFactor>()
        val (toMultiply, notTo) = factors.partition { it.contains(rv) }
        summedOutFactors.addAll(notTo)
        summedOutFactors.add(pointwiseProduct(toMultiply).sumOut(rv) as CustomFactor)
        return summedOutFactors
    }

    private fun maxOut(rv: RandomVariable, factors: List<CustomFactor>)
            : Pair<ArrayList<CustomFactor>, ArrayList<ArrayList<HashMap<RandomVariable, ArrayList<HashMap<RandomVariable, Any>>>>>>{
        val maxedOutFactors = ArrayList<CustomFactor>()
        val (toMultiply, notTo) = factors.partition { it.contains(rv) }
        maxedOutFactors.addAll(notTo)
        val pointWised = pointwiseProduct(toMultiply)
        val maxedOut = pointWised.maxOut(rv)
        maxedOutFactors.add(maxedOut)
        //maxedOut contiene finalAssignment. Devi ritornarlo!
        return Pair(maxedOutFactors, (maxedOut as CustomProbabilityTable).finalAssignment)
    }

    private fun pointwiseProduct(factors: List<CustomFactor>): CustomFactor {
        var product = factors[0]
        for (i in 1 until factors.size) {
            product = product.pointwiseProduct(factors[i]) as CustomFactor
        }
        println("Vars: ${product.argumentVariables } Size: ${product.argumentVariables.size}")
        return product
    }
}


