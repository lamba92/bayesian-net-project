package it.unito.probability

import aima.core.probability.proposition.AssignmentProposition
import it.unito.probability.bayes.BayesNetsFactory.getFullAdderCircuitNet
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction

fun main(args: Array<String>) {
    var net = getFullAdderCircuitNet()
    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.MPE,
            hMetrics = minNeighboursHeuristicFunction(),
            removeIrrelevantRVs = true,
            showMoralGraph = true,
            delay = 1000
    )
    val queryVar = net.variablesInTopologicalOrder.last()
    val evidenceVar = net.variablesInTopologicalOrder.last()
    val res = inference.ask(arrayOf(queryVar), arrayOf(AssignmentProposition(evidenceVar, true)), net) as CustomFactor

//    println(res.printTable())
    print(res)
}