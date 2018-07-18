package it.unito.bayesian.net

import aima.core.probability.CategoricalDistribution
import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.ProbabilityTable
import it.unito.bayesian.net.utils.MoralGraph
import it.unito.bayesian.net.utils.MoralGraph.MoralNode
import it.unito.bayesian.net.utils.combineParents
import it.unito.bayesian.net.utils.isAncestorOf
import it.unito.bayesian.net.utils.isNotAncestorOf
import org.graphstream.graph.implementations.AbstractEdge
import java.util.ArrayList
import java.util.HashSet

/**
 * Exact inference algorithm (Variable Elimination) for static [BayesianNetwork]s.
 */
object Inferences {

    /**
     * Used to get a Variable Elimination algorithm with a custom ordering using an heuristic based on a [MoralGraph].
     * @param hMetrics The lambda used to assign an heuristic to a node of the [MoralGraph].
     * @return
     */
    fun getCustomEliminationAsk(hMetrics: (MoralGraph.MoralNode, MoralGraph) -> Int)
        = object : CustomEliminationAsk() {

        override fun order(bn: BayesianNetwork, vars: Collection<RandomVariable>) =
                MoralGraph(bn, vars, hMetrics).getRandomVariables()

        override fun calculateVariables(
                X: Array<out RandomVariable>,
                e: Array<out AssignmentProposition>,
                bn: BayesianNetwork,
                hidden: MutableSet<RandomVariable>,
                bnVARS: MutableCollection<RandomVariable>) {
            hidden.addAll(bn.variablesInTopologicalOrder)
            bnVARS.addAll(bn.variablesInTopologicalOrder)
            val mainRvs = ArrayList<RandomVariable>().apply {
                addAll(X)
                e.forEach { addAll(it.scope) }
            }
            hidden.removeAll(mainRvs)
            hidden.removeIf { it.isNotAncestorOf(mainRvs, bn) }
            bnVARS.removeIf { it.isNotAncestorOf(mainRvs, bn) }
        }
    }

    /**
     * The cost of a vertex is the product of weights — domain cardinality — of its neighbors.
     * @return The lambda to be used to order the [MoralGraph].
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
     * The cost of a vertex is the number of neighbors it has in the current graph.
     * @return The lambda to be used to order the [MoralGraph].
     */
    fun minNeighboursHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return { node, _ ->
            node.getEdgeSet<AbstractEdge>().size
        }
    }

    /**
     * The cost of a vertex is the number of edges that need to be added to the graph due to its elimination.
     * @return The lambda to be used to order the [MoralGraph].
     */
    fun minFillHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return { node, graph ->
            var t = 0
            combineParents(node.getNeighborNodeIterator()).forEach { p1, p2 ->
                val n1 = graph.getNode(p1)
                val n2 = graph.getNode(p2)
                if(n1.hasNotEdgeBetween(n2)) t++
            }
            t
        }
    }

    /**
     * The cost of a vertex is the sum of weights of the edges that need to be added to the graph due
     * to its elimination, where a weight of an edge is the product of weights of its constituent vertices.
     * @return The lambda to be used to order the [MoralGraph].
     */
    fun weightedMinFillHeuristicFunction(): (MoralGraph.MoralNode, MoralGraph) -> Int {
        return { node, graph ->
            var t = 0
            combineParents(node.getNeighborNodeIterator()).forEach { p1, p2 ->
                val n1 = graph.getNode(p1)
                val n2 = graph.getNode(p2)
                if(n1.hasNotEdgeBetween(n2)) t += n1.heuristic!!*n2.heuristic!!
            }
            t
        }
    }

}