package it.unito.probability

import aima.core.probability.RandomVariable
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import it.unito.probability.bayes.BayesNetsFactory.getAdderNetExample
import it.unito.probability.bayes.BayesNetsFactory.getFullAdderCircuitNet
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction

fun main(args: Array<String>) {
    var net = getAdderNetExample()
    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.MPE,
            hMetrics = minNeighboursHeuristicFunction(),
            removeIrrelevantRVs = true,
            showMoralGraph = false,
            delay = 1000
    )
    val queryVar = net.variablesInTopologicalOrder.last()
    //val evidenceVar = net.variablesInTopologicalOrder.last()
    val evidenceVar = RandVar("C_OUT", BooleanDomain())

    val res = inference.ask(arrayOf(queryVar), arrayOf(AssignmentProposition(evidenceVar, true)), net) as CustomFactor

    println(res.printTable())
    //print(res)
}