package it.unito.bayesian.net.main

import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.example.DynamicBayesNetExampleFactory
import aima.core.probability.example.DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.CustomDynamicBayesianNet
import it.unito.bayesian.net.Inferences
import it.unito.bayesian.net.Inferences.getCustomEliminationAsk
import it.unito.bayesian.net.Inferences.minWeightHeuristicFunction
import it.unito.bayesian.net.example.BayesNetsFactory
import it.unito.bayesian.net.example.BayesNetsFactory.getDecentDynamicNetworkExample

fun main(args: Array<String>){
    val inference = //EliminationAsk()
            getCustomEliminationAsk(minWeightHeuristicFunction())
    val nets = ArrayList<CustomDynamicBayesianNet>().apply {
        add(CustomDynamicBayesianNet(getUmbrellaWorldNetwork(), inference))
        add(CustomDynamicBayesianNet(getDecentDynamicNetworkExample(), inference))
    }
    for(net in nets){
        for(i in 0 until 10){
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