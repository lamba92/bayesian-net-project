package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import it.unito.bayesian.net.utils.MoralGraph
import org.graphstream.graph.implementations.AbstractEdge

object Inferences {

    fun getCustomEliminationAsk(hMetrics: (MoralGraph.MoralNode, MoralGraph) -> Int)
        = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork, vars: Collection<RandomVariable>): MutableList<RandomVariable> {
            return getOrderingFunction(bn, vars, hMetrics).reversed().toMutableList()
        }
    }

    fun getOrderingFunction(
            bn: BayesianNetwork, vars: Collection<RandomVariable>,
            hMetrics: (MoralGraph.MoralNode, MoralGraph) -> Int)
            = MoralGraph(bn, vars, hMetrics).getRandomVariables()


    fun minWeightHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return {node, graph ->
            var i = 1
            for(n in node.getNeighborNodeIterator<MoralGraph.MoralNode>())
                i *= n.randomVariable!!.domain.size()
            i
        }
    }

    fun minNeighboursHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return { node, graph ->
            node.getEdgeSet<AbstractEdge>().size
        }
    }

}