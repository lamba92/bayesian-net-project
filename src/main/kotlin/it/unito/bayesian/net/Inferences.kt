package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import java.util.*

object Inferences {

    val eliminationAskWithMinWeightHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork, vars: Collection<RandomVariable>)
                = ArrayList(vars).apply {
            sortedWith(compareBy<RandomVariable>{
                var dim = 1
                for(node in bn.getNode(it).parents){
                    dim *= node.randomVariable.domain.size()
                }
                for(node in bn.getNode(it).children){
                    dim *= node.randomVariable.domain.size()
                }
                return@compareBy -dim
            })
            reverse()
        }
    }

    val eliminationAskWithMinNeighboursHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = ArrayList(vars).apply {
            sortWith(Comparator.comparingInt {
                - (bn?.getNode(it)?.children?.size ?: 0) - (bn?.getNode(it)?.parents?.size ?: 0)
            })
        }
    }

    val eliminationAskWithMaxCardinalityHeuristic  = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = ArrayList(vars).apply {
            // TODO
        }
    }

    val eliminationAskWithMinFillHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = ArrayList(vars).apply {
            // TODO
        }
    }

    val eliminationAskWithWeightedMinFillHeuristic = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = ArrayList(vars).apply {
            // TODO
        }
    }

}