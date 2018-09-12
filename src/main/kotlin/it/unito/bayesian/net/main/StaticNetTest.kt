package it.unito.bayesian.net.main

import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.example.BayesNetExampleFactory.*
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.Inferences.getCustomEliminationAsk
import it.unito.bayesian.net.Inferences.minNeighboursHeuristicFunction
import it.unito.bayesian.net.KCustomEliminationAsk
import it.unito.bayesian.net.example.BayesNetsFactory.getComplexDynamicNetworkExample

fun main(args: Array<String>){
    val nets = ArrayList<BayesianNetwork>().apply {
        add(constructToothacheCavityCatchNetwork())
        add(constructToothacheCavityCatchWeatherNetwork())
        add(constructMeningitisStiffNeckNetwork())
        add(constructBurglaryAlarmNetwork())
        add(constructCloudySprinklerRainWetGrassNetwork())
        add(getComplexDynamicNetworkExample())
    }
    val inference = getCustomEliminationAsk(inferenceMethod = KCustomEliminationAsk.InferenceMethod.MAP)
    for(net in nets){
        val queryVar = net.variablesInTopologicalOrder.last()
        val evidenceVar = net.variablesInTopologicalOrder.first()
        val res = inference.ask(arrayOf(queryVar), arrayOf(AssignmentProposition(evidenceVar, true)), net)
        println(
                "Query variable is: $queryVar | Evidence variable is: $evidenceVar.\n" +
                "Distribution: \n$res\n\n"
        )
    }
}