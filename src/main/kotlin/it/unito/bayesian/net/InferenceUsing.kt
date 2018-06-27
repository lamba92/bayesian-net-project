package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import java.util.Comparator

object InferenceUsing {
    @Suppress("ClassName")
    object eliminationAsk{
        val withMinWeightHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
            override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                    = super.order(bn, ArrayList(vars).apply {
                sortWith(Comparator.comparingInt { -it.domain.size() })
            })
        }
        val withMinNeighbourHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
            override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                    = super.order(bn, ArrayList(vars).apply {
                sortWith(Comparator.comparingInt {
                    - (bn?.getNode(it)?.children?.size ?: 0) - (bn?.getNode(it)?.parents?.size ?: 0)
                })
            })
        }
    }
}