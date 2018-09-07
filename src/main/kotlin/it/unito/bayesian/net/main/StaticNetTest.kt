package it.unito.bayesian.net.main

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.bayes.exact.EnumerationAsk
import aima.core.probability.example.BayesNetExampleFactory.*
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.CustomEliminationAsk
import it.unito.bayesian.net.Inferences.getCustomEliminationAsk
import it.unito.bayesian.net.Inferences.minNeighboursHeuristicFunction
import it.unito.bayesian.net.Inferences.minWeightHeuristicFunction
import it.unito.bayesian.net.example.BayesNetsFactory.getComplexDynamicNetworkExample

fun main(args: Array<String>){
    val nets = ArrayList<BayesianNetwork>().apply {
//        add(constructToothacheCavityCatchNetwork())
//        add(constructToothacheCavityCatchWeatherNetwork())
//        add(constructMeningitisStiffNeckNetwork())
//        add(constructBurglaryAlarmNetwork())
//        add(constructCloudySprinklerRainWetGrassNetwork())
        add(getComplexDynamicNetworkExample())
    }
    val inference = CustomEliminationAsk()
    for(net in nets){
        val queryVar = net.variablesInTopologicalOrder.first()
        val evidenceVar = net.variablesInTopologicalOrder.last()
        val res = inference.ask(arrayOf(queryVar), arrayOf(AssignmentProposition(evidenceVar, true)), net)
        println(
                "Query variable is: $queryVar | Evidence variable is: $evidenceVar.\n" +
                "Distribution: $res\n\n"
        )
    }
}