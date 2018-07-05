package it.unito.bayesian.net

import aima.core.probability.CategoricalDistribution
import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.ProbabilityTable
import it.unito.bayesian.net.utils.MoralGraph
import it.unito.bayesian.net.utils.MoralGraph.MoralNode
import org.graphstream.graph.implementations.AbstractEdge
import java.util.ArrayList
import java.util.HashSet

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
        = object : CustomEliminationAsk() {
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
        return {node, _ ->
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
        return { node, _ ->
            node.getEdgeSet<AbstractEdge>().size
        }
    }

    

}