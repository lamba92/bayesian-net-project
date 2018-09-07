package it.unito.bayesian.net.main

import aima.core.probability.example.DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.CustomDynamicBayesianNet
import it.unito.bayesian.net.Inferences.getCustomEliminationAsk
import it.unito.bayesian.net.Inferences.minFillHeuristicFunction
import it.unito.bayesian.net.Inferences.minNeighboursHeuristicFunction
import it.unito.bayesian.net.Inferences.minWeightHeuristicFunction
import it.unito.bayesian.net.example.BayesNetsFactory.getComplexDynamicNetworkExample

fun main(args: Array<String>){
    val inference = getCustomEliminationAsk(minFillHeuristicFunction())
    val nets = ArrayList<CustomDynamicBayesianNet>().apply {
//        add(CustomDynamicBayesianNet(getUmbrellaWorldNetwork(), inference))
        add(CustomDynamicBayesianNet(getComplexDynamicNetworkExample(), inference))
    }
    for(net in nets){
        for(i in 0 until 1000){
            val propositions = ArrayList<AssignmentProposition>()
            for(ev in net.e_1){
                propositions.add(AssignmentProposition(ev, true))
            }
            net.forward(propositions.toTypedArray(), true)
            propositions.clear()
        }
        println("_________________")
    }
}