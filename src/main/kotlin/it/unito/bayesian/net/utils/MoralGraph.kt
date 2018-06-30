package it.unito.bayesian.net.utils

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.*
import java.util.*

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

    fun getRandomVariables(): ArrayList<RandomVariable> {
        val toReturn = ArrayList<RandomVariable>()
        display()
        while(heuristicQueue.isNotEmpty()){
            Thread.sleep(1000)
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

    class MoralNode(graph: AbstractGraph, name: String): SingleNode(graph, name){
        private var heuristic: Int? = null
        var randomVariable: RandomVariable? = null

        fun calculateHeuristic(hMetric: (MoralNode, MoralGraph) -> Int): Int {
            heuristic = hMetric(this, graph as MoralGraph)
            return heuristic!!
        }

        fun hasNotEdgeBetween(node: MoralNode) = !hasEdgeBetween(node)
        fun hasEdgeBetween(node: MoralNode) = hasEdgeBetween(node.randomVariable!!.name)
    }
}