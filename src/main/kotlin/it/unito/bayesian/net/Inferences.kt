package it.unito.bayesian.net

import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.utils.MoralGraph
import it.unito.bayesian.net.utils.combineParents
import it.unito.bayesian.net.utils.isNotAncestorOf
import org.graphstream.graph.implementations.AbstractEdge
import java.util.ArrayList
import it.unito.bayesian.net.KCustomEliminationAsk.InferenceMethod
import it.unito.bayesian.net.KCustomEliminationAsk.InferenceMethod.STANDARD

/**
 * Exact inference algorithm (Variable Elimination) for static [BayesianNetwork]s.
 */
object Inferences {

    /**
     * Used to get a Variable Elimination algorithm with a custom ordering using an heuristic based on a [MoralGraph].
     * @param hMetrics The lambda used to assign an heuristic to a node of the [MoralGraph].
     * @return A custom [EliminationAsk] object.
     */
    fun getCustomEliminationAsk(
            hMetrics: (MoralGraph.MoralNode, MoralGraph) -> Int = minWeightHeuristicFunction(),
            inferenceMethod: InferenceMethod = STANDARD,
            showMoralGraph: Boolean = false,
            delay: Long = 3000,
            overrideCV: Boolean= true)
        = object : KCustomEliminationAsk(inferenceMethod) {

        override fun order(bn: BayesianNetwork, vars: Collection<RandomVariable>) =
                MoralGraph(bn, vars, hMetrics).getRandomVariables(showMoralGraph, delay)

        override fun calculateVariables(
                X: Array<RandomVariable>,
                e: Array<AssignmentProposition>,
                bn: BayesianNetwork)
                    : Pair<Set<RandomVariable>, Collection<RandomVariable>> {
            if(overrideCV){
            val hiddenRVs = HashSet(bn.variablesInTopologicalOrder)
            val mainRvs = ArrayList<RandomVariable>().apply {
                addAll(X)
                addAll(e.map { it.termVariable })
            }
            hiddenRVs.removeAll(mainRvs)
            hiddenRVs.removeIf { it.isNotAncestorOf(mainRvs, bn) }

            return Pair(hiddenRVs, ArrayList(hiddenRVs).apply { addAll(mainRvs) })
            } else return super.calculateVariables(X, e, bn)
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