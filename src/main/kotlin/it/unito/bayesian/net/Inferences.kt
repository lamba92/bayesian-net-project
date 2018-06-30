package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import it.unito.bayesian.net.utils.MoralGraph
import it.unito.bayesian.net.utils.MoralGraph.MoralNode
import org.graphstream.graph.implementations.AbstractEdge

/**
 * Exact inference algorithm (Variable Elimination) for static [BayesianNetwork]s.
 */
object Inferences {

    /**
     * Used to get a Variable Elimination algorithm with a custom heuristic.
     * @param hMetrics The metric eventually used to compute an improved sorting of [RandomVariable]s
     * @return
     */
    fun getCustomEliminationAsk(hMetrics: (MoralGraph.MoralNode, MoralGraph) -> Int)
        = object : aima.core.probability.bayes.exact.EliminationAsk() {
        override fun order(bn: BayesianNetwork, vars: Collection<RandomVariable>): MutableList<RandomVariable> {
            return getOrderingFunction(bn, vars, hMetrics).reversed().toMutableList()
        }
    }

    /**
     * ???
     * @param bn The static [BayesianNetwork] to operate with
     * @param vars The [RandomVariable]s cointained in the [BayesianNetwork]
     * @param hMetrics The metric eventually used to compute an improved sorting of [RandomVariable]s
     */
    fun getOrderingFunction(
            bn: BayesianNetwork, vars: Collection<RandomVariable>,
            hMetrics: (MoralGraph.MoralNode, MoralGraph) -> Int)
            = MoralGraph(bn, vars, hMetrics).getRandomVariables()

    /**
     * Function used to calculate a [MoralNode]'s weight (domain cardinality).
     * @return [MoralNode]'s weight (domain cardinality)
     */
    fun minWeightHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return {node, graph ->
            var i = 1
            for(n in node.getNeighborNodeIterator<MoralGraph.MoralNode>())
                i *= n.randomVariable!!.domain.size()
            i
        }
    }

    /**
     * Function used to calculate the number of neighbours of a [MoralNode].
     * @return [MoralNode]'s size edge set
     */
    fun minNeighboursHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return { node, graph ->
            node.getEdgeSet<AbstractEdge>().size
        }
    }

}