package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import it.unito.bayesian.net.utils.MoralGraph
import java.util.*

object Inferences {

    fun getCustomEliminationAsk(hMetrics: (List<MoralGraph.MoralNode>) -> Int)
        = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork, vars: Collection<RandomVariable>): MutableList<RandomVariable> {
            return getOrderingFunction(bn, vars, hMetrics)
        }
    }

    fun getOrderingFunction(
            bn: BayesianNetwork, vars: Collection<RandomVariable>,
            hMetrics: (List<MoralGraph.MoralNode>) -> Int)
            = MoralGraph(bn, vars, hMetrics).getRandomVariables()


    fun minWeightHeuristicFunction(): (List<MoralGraph.MoralNode>) -> Int {
        return {
            var t = 1
            for(n in it) t *= n.rv.domain.size()
            t
        }
    }

    fun minNeighboursHeuristicFunction(): (List<MoralGraph.MoralNode>) -> Int {
        return { it.size }
    }

}