package it.unito.probability

import aima.core.probability.example.BayesNetExampleFactory
import aima.core.probability.proposition.AssignmentProposition
import it.unito.probability.bayes.BayesNetsFactory.getAdderNetExample
import it.unito.probability.bayes.BayesNetsFactory.getDigitalCircuitNetExample
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction

fun main(args: Array<String>) {
    var net = getDigitalCircuitNetExample()
    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.MPE,
            hMetrics = minNeighboursHeuristicFunction(),
            removeIrrelevantRVs = true,
            showMoralGraph = false,
            delay = 1000
    )

    val queryVar = net.variablesInTopologicalOrder.first()
    val evidenceVar = net.variablesInTopologicalOrder.last()
    val res = inference.ask(emptyArray(), arrayOf(AssignmentProposition(evidenceVar, true)), net) as CustomFactor

    println(res.printTable())
}