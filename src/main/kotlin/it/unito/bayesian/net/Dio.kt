package it.unito.bayesian.net

import aima.core.probability.CategoricalDistribution
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.ConditionalProbabilityDistribution
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.AbstractNode
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import it.unito.bayesian.net.example.BayesNetsFactory.e_1
import it.unito.bayesian.net.example.BayesNetsFactory.getDecentDynamicNetworkExample
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun main(array: Array<String>){
    val dbn = getDecentDynamicNetworkExample()
    val newRVs = HashMap<RandomVariable, CategoricalDistribution>()
    val nextStepNodes = ArrayList<Node>()

    for(rv in dbn.x_1){
        val currentNode = dbn.getNode(rv)

        val newCPT = InferenceUsing.eliminationAsk.withMinWeightHeuristic.eliminationAsk(
                arrayOf(rv), arrayOf(AssignmentProposition(e_1, true)), dbn
        )
        newRVs[rv] = newCPT
        println("$rv ${Arrays.toString(newCPT.values)}")
        val x1_node = FullCPTNode(rv, newCPT.values)
        val parents = currentNode.parents
        //creare mappa di assegnazione fra padri e figli nuovi
        val x2_node = CustomNode(rv.getNext(), currentNode.cpd
            //aggiungere i nodi padri qui
        )
    }

//    val dbnStep2 = DynamicBayesNet(dbn.priorNetwork, )
}

class CustomNode(rv: RandomVariable, val distribution: ConditionalProbabilityDistribution, vararg node: Node):
        AbstractNode(rv, *node){

    override fun getCPD(): ConditionalProbabilityDistribution = distribution
}

fun RandomVariable.getNext(): RandomVariable = RandVar(name.substring(name.length, name.length - 1), domain)