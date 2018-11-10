package it.unito.probability

import aima.core.probability.proposition.AssignmentProposition
import it.unito.probability.bayes.BayesNetsFactory.getChildNet
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction

fun main(args: Array<String>) {

    val net = getChildNet()

    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.MAP,
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