package it.unito.bayesian.net.utils

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.*
import java.util.*

/**
 * Representation of a Moral Graph used to calculate heuristics
 * for Variable Elimination algorithm.
 * @param net The [BayesianNetwork] from which generate the [MoralGraph].
 * @param vars The [Collection] of [RandomVariable]s contained inside [net].
 * @param hMetric The lambda function used to evaluate the heuristic of a [MoralNode].
 */
class MoralGraph(
        net: BayesianNetwork,
        val vars: Collection<RandomVariable>,
        private val hMetric: (MoralNode, MoralGraph) -> Int
    ): SingleGraph("MG", true, false) {

    private val heuristicQueue
            = PriorityQueue<MoralGraph.MoralNode>(
                compareBy<MoralGraph.MoralNode>{
                    it.calculateHeuristic(hMetric)
                })

    init{
        for(rv in vars){
            addNode<MoralNode>(rv.name).randomVariable = rv
        }
        for(rv in vars){
            for(p in net.getNode(rv).parents){
                if(getNode(rv).hasNotEdgeBetween(getNode(p.randomVariable)))
                    addEdge<AbstractEdge>("${rv.name}--${p.randomVariable.name}", getNode(rv), getNode(p.randomVariable), false)
            }
            combineParents(net.getNode(rv).parents).forEach { p1, p2 ->
                if(getNode(p1).hasNotEdgeBetween(getNode(p2)))
                    addEdge<AbstractEdge>("${p1.name}--${p2.name}", getNode(p1), getNode(p2), false)
            }
        }
        updateHeuristics()
    }

    private fun updateHeuristics() {
        for(n in getNodeSet<MoralNode>()) heuristicQueue.add(n)
    }

    fun getNode(rv: RandomVariable) = getNode<MoralNode>(rv.name)

    /**
     * Return an [ArrayList]<[RandomVariable]> in which they are ordered by the Variable Elimination algorithm.
     * @return The [RandomVariable]s ordered.
     */
    fun getRandomVariables(): ArrayList<RandomVariable> {
        val toReturn = ArrayList<RandomVariable>()
//        display()
        while(heuristicQueue.isNotEmpty()){
//            Thread.sleep(10000)
            val head = heuristicQueue.poll()
            val iterator = head.getNeighborNodeIterator<MoralNode>()
            toReturn.add(head.randomVariable!!)
            combineParents(iterator).forEach { t, u ->
                if(getNode(t).hasNotEdgeBetween(getNode(u)))
                    addEdge<AbstractEdge>("${t.name}--${u.name}", getNode(t), getNode(u), false)
            }
            removeNode<MoralNode>(head)
            for(n in iterator){
                heuristicQueue.remove(n)
                heuristicQueue.add(n)
            }
        }
        return toReturn
    }

    override fun <T : Node?> addNode_(sourceId: String?, timeId: Long, nodeId: String): T {
        var node: AbstractNode? = getNode(nodeId)
        if (node != null) {
            return node as T
        }
        node = MoralNode(this, nodeId)
        addNodeCallback(node)
        // If the event comes from the graph itself, create timeId
        //listeners.sendNodeAdded(sourceId, if(timeId == -1L) newEvent() else timeId, nodeId)
        return node as T
    }

    /**
     * Representation of a node inside the [MoralGraph].
     * @param graph The [MoralGraph] in which this node wil be contained.
     * @param name The unique name of this node.
     */
    class MoralNode(graph: AbstractGraph, name: String): SingleNode(graph, name){
        var heuristic: Int? = null
        var randomVariable: RandomVariable? = null

        /**
         * Calculate the heuristic value of this node and stores it inside [heuristic].
         * @return The [heuristic] value.
         */
        fun calculateHeuristic(hMetric: (MoralNode, MoralGraph) -> Int): Int {
            heuristic = hMetric(this, graph as MoralGraph)
            return heuristic!!
        }

        /**
         *  Negation of the function [hasEdgeBetween].
         *  @return Negation of the function [hasEdgeBetween].
         */
        fun hasNotEdgeBetween(node: MoralNode) = !hasEdgeBetween(node)

        fun hasEdgeBetween(node: MoralNode) = hasEdgeBetween(node.randomVariable!!.name)
    }
}