package it.unito.bayesian.net.utils

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import java.util.*
import kotlin.collections.ArrayList

class MoralGraph(net: BayesianNetwork, vars: Collection<RandomVariable>, private val hMetric: (List<MoralNode>) -> Int) {

    private val nodesMap: HashMap<RandomVariable, MoralNode>
    private val heuristicQueue = PriorityQueue<MoralNode>(compareBy<MoralNode>{it.calculateHeuristic(hMetric)})

    init{
        val nodes = HashMap<RandomVariable, MoralNode>()
        for(rv in vars){
            nodes[rv] = MoralNode(rv)
        }
        for(rv in net.variablesInTopologicalOrder){
            combineParents(net.getNode(rv).parents).forEach { p1, p2 ->
                nodes[p1]!!.neighbours.add(nodes[p2]!!)
            }
        }
        nodesMap = nodes
        updateHeuristics()
    }

    fun getRandomVariables(): ArrayList<RandomVariable> {
        val toReturn = ArrayList<RandomVariable>()
        while(heuristicQueue.isNotEmpty()){
            val head = heuristicQueue.poll()
            toReturn.add(head.rv)
            combineParents(head.neighbours).forEach { t, u ->
                val node1 = nodesMap[t]!!
                val node2 = nodesMap[u]!!
                if(!node1.neighbours.contains(node2)
                        && !node2.neighbours.contains(node1)){
                    node1.neighbours.add(node2)
                    node2.neighbours.add(node1)
                }
            }
            for(n in head.neighbours){
                heuristicQueue.remove(n)
                heuristicQueue.add(n)
            }
        }
        return toReturn
    }

    private fun updateHeuristics(){
        heuristicQueue.clear()
        for(node in nodesMap.values) heuristicQueue.add(node)
    }


    class MoralNode(val rv: RandomVariable){
        val neighbours = ArrayList<MoralNode>()
        private var heuristic: Int? = null

        fun calculateHeuristic(hMetric: (List<MoralNode>) -> Int): Int {
            heuristic = hMetric(neighbours)
            return heuristic!!
        }
    }
}