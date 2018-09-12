package it.unito.bayesian.net

import aima.core.probability.CategoricalDistribution
import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesInference
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.FiniteNode
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.ProbabilityTable
import it.unito.bayesian.net.utils.convertToCustom
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class KCustomEliminationAsk(val inferenceMethod: InferenceMethod = InferenceMethod.STANDARD): BayesInference {

    enum class InferenceMethod {
        STANDARD, MPE, MAP
    }

    override fun ask(X: Array<RandomVariable>, observedEvidence: Array<AssignmentProposition>, bn: BayesianNetwork): CategoricalDistribution {
        val (hidden, vars) = calculateVariables(X, observedEvidence, bn)


        // factors <- []
        var factors = ArrayList<Factor>()
        for (rv in vars.subtract(observedEvidence.map { it.termVariable })) {
            // factors <- [MAKE-FACTOR(rv, e) | factors]
            factors.add(0, makeFactor(rv, observedEvidence, bn))
        }

        for (rv in order(bn, hidden)) {
            factors = when (inferenceMethod) {
                InferenceMethod.STANDARD -> sumOut(rv, factors)
                InferenceMethod.MPE -> maxOut(rv, factors)
                InferenceMethod.MAP -> if (X.contains(rv)) sumOut(rv, factors) else maxOut(rv, factors)
            }
        }
        val k = pointwiseProduct(factors)
        return when(k){
            is CustomProbabilityTable -> k.normalize()
            is ProbabilityTable -> k.normalize()
            else -> throw IllegalArgumentException("Illegal class idiot")
        }
    }

    open fun calculateVariables(X: Array<RandomVariable>, e: Array<AssignmentProposition>, bn: BayesianNetwork)
            : Pair<Set<RandomVariable>, Collection<RandomVariable>> {

        val hidden = HashSet<RandomVariable>()
        val rvs = ArrayList<RandomVariable>(bn.variablesInTopologicalOrder)

        hidden.addAll(rvs)

        hidden.removeAll(X)
        hidden.removeAll(e.map { it.termVariable })

        return Pair(hidden, rvs)
    }

    private fun makeFactor(rv: RandomVariable, e: Array<AssignmentProposition>,
                           bn: BayesianNetwork): Factor {
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
            f.iterateOver { possibleAssignment, value ->
                if(possibleAssignment == assignment) table[assignment] = value
            }
            return CustomProbabilityTable(table)
        }
    }

    open fun order(bn: BayesianNetwork,
                        vars: Collection<RandomVariable>) = vars.reversed()

    private fun sumOut(rv: RandomVariable, factors: List<Factor>): ArrayList<Factor> {
        val summedOutFactors = ArrayList<Factor>()
        val toMultiply = ArrayList<Factor>()
        for (f in factors) {
            if (f.contains(rv))
                toMultiply.add(f)
            else
                summedOutFactors.add(f)
        }
        summedOutFactors.add(pointwiseProduct(toMultiply).sumOut(rv))
        return summedOutFactors
    }

    private fun maxOut(rv: RandomVariable, factors: List<Factor>): ArrayList<Factor> {
        val summedOutFactors = ArrayList<Factor>()
        val toMultiply = ArrayList<Factor>()
        for (f in factors) {
            if (f.contains(rv))
                toMultiply.add(f)
            else
                summedOutFactors.add(f)
        }
        summedOutFactors.add((pointwiseProduct(toMultiply).convertToCustom() as CustomFactor).maxOut(rv))
        return summedOutFactors
    }

    private fun pointwiseProduct(factors: List<Factor>): Factor {
        var product = factors[0]
        for (i in 1 until factors.size) {
            product = product.pointwiseProduct(factors[i])
        }
        return product
    }
}