package it.unito.probability.main

import aima.core.probability.example.DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork
import aima.core.probability.proposition.AssignmentProposition
import it.unito.probability.bayes.BayesNetsFactory.getComplexDynamicNetworkExample
import it.unito.probability.bayes.CustomDynamicBayesianNet
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minFillHeuristicFunction
import it.unito.probability.bayes.CustomEliminationAsk

fun main(args: Array<String>){
    val inference = getCustomEliminationAsk(minFillHeuristicFunction(), CustomEliminationAsk.InferenceMethod.MPE)
    val nets = ArrayList<CustomDynamicBayesianNet>().apply {
        add(CustomDynamicBayesianNet(getUmbrellaWorldNetwork(), inference))
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