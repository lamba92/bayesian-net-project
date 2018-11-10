package it.unito.probability

import aima.core.probability.proposition.AssignmentProposition
import it.unito.probability.bayes.BayesNetsFactory.getAlarmNet
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction

fun main(args: Array<String>) {

    var net = getAlarmNet()

    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.MPE,
            hMetrics = minNeighboursHeuristicFunction(),
            removeIrrelevantRVs = false,
            showMoralGraph = false,
            delay = 1000
    )


    val queryVars = net.variablesInTopologicalOrder.first()
    val evidenceVar1 = net.variablesInTopologicalOrder.last()
    val res = inference.ask(arrayOf(queryVars), arrayOf(AssignmentProposition(evidenceVar1, true)), net) as CustomFactor


    println(res.printTable())
}