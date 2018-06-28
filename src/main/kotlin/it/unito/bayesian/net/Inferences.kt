package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import java.util.Comparator

object Inferences {

    val eliminationAskWithMaxCardinalityHeuristic  = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = super.order(bn, ArrayList(vars).apply {
            // TODO
        })
    }

    val eliminationAskWithMinNeighboursHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = super.order(bn, ArrayList(vars).apply {
            sortWith(Comparator.comparingInt {
                - (bn?.getNode(it)?.children?.size ?: 0) - (bn?.getNode(it)?.parents?.size ?: 0)
            })
        })
    }

    val eliminationAskWithMinWeightHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = super.order(bn, ArrayList(vars).apply {
            sortWith(Comparator.comparingInt { -it.domain.size() })
        })

    }

    val eliminationAskWithMinFillHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = super.order(bn, ArrayList(vars).apply {
            // TODO
        })
    }

    val eliminationAskWithWeightedMinFillHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = super.order(bn, ArrayList(vars).apply {
            // TODO
        })
    }

}