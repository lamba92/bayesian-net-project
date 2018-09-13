package it.unito.bayesian.net

import aima.core.probability.CategoricalDistribution
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesInference
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.FiniteNode
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.utils.convertToCustom
import it.unito.bayesian.net.utils.multiplyAll
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class KCustomEliminationAsk(private val inferenceMethod: InferenceMethod = InferenceMethod.STANDARD): BayesInference {

    enum class InferenceMethod {
        STANDARD, MPE, MAP
    }

    override fun ask(X: Array<RandomVariable>, observedEvidences: Array<AssignmentProposition>, bn: BayesianNetwork): CategoricalDistribution? {

        val (hidden, vars) = calculateVariables(X, observedEvidences, bn)
        val factors = ArrayList<CustomFactor>()

        for (rv in vars) {
            factors.add(0, makeFactor(rv, observedEvidences, bn))
        }
        return when(inferenceMethod){
            InferenceMethod.STANDARD -> exactInference(order(bn, hidden), factors)
            InferenceMethod.MPE -> mpeInference(order(bn, hidden), factors)
            InferenceMethod.MAP -> null
        }
    }

    private fun exactInference(orderedHiddenRVs: ArrayList<RandomVariable>,
                               factors: ArrayList<CustomFactor>): CategoricalDistribution {
        var newFactors = ArrayList(factors)
        for(rv in orderedHiddenRVs){
            newFactors = sumOut(rv, newFactors)
        }
        return (pointwiseProduct(newFactors) as CustomProbabilityTable).normalize()
    }

    private fun mpeInference(hiddenOrdered : ArrayList<RandomVariable>,
                             factors: ArrayList<CustomFactor>): CategoricalDistribution {
        var newFactors = ArrayList(factors)
        for(rv in hiddenOrdered){
            newFactors = maxOut(rv, newFactors)
        }
        return newFactors.map { it as CustomProbabilityTable }.multiplyAll()
    }

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
        val evidence = ArrayList<AssignmentProposition>()
        for (ap in e) {
            if (n.cpt.contains(ap.termVariable)) {
                evidence.add(ap)
            }
        }
        val f = n.cpt.getFactorFor(
                *evidence.toTypedArray())
        return try{
            f.convertToCustom()
        } catch (ex: Exception){
            val table = HashMap<HashMap<RandomVariable, Any>, Double>()
            val assignment = HashMap<RandomVariable, Any>()
            e.forEach {
                assignment[it.termVariable] = it.value
            }
            table[assignment] = f.values[0]
            return CustomProbabilityTable(table)
        }
    }

    open fun order(bn: BayesianNetwork,
                        vars: Collection<RandomVariable>) = ArrayList(vars.reversed())

    private fun sumOut(rv: RandomVariable, factors: List<CustomFactor>): ArrayList<CustomFactor> {
        val summedOutFactors = ArrayList<CustomFactor>()
        val toMultiply = ArrayList<CustomFactor>()
        for (f in factors) {
            if (f.contains(rv))
                toMultiply.add(f)
            else
                summedOutFactors.add(f)
        }
        summedOutFactors.add(pointwiseProduct(toMultiply).sumOut(rv) as CustomFactor)
        return summedOutFactors
    }

    private fun maxOut(rv: RandomVariable, factors: List<CustomFactor>): ArrayList<CustomFactor> {
        val summedOutFactors = ArrayList<CustomFactor>()
        val toMultiply = ArrayList<CustomFactor>()
        for (f in factors) {
            if (f.contains(rv))
                toMultiply.add(f)
            else
                summedOutFactors.add(f)
        }
        val pointWised = pointwiseProduct(toMultiply)
        val maxedOut = pointWised.maxOut(rv)
        summedOutFactors.add(maxedOut)
        return summedOutFactors
    }

    private fun pointwiseProduct(factors: List<CustomFactor>): CustomFactor {
        var product = factors[0]
        for (i in 1 until factors.size) {
            product = product.pointwiseProduct(factors[i]) as CustomFactor
        }
        return product
    }
}