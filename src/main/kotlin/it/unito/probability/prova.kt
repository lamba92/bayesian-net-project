package it.unito.probability

import aima.core.probability.domain.BooleanDomain
import aima.core.probability.example.BayesNetExampleFactory
import aima.core.probability.example.ExampleRV
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import it.unito.probability.bayes.BayesNetsFactory.getAdderNetExample
import it.unito.probability.bayes.BayesNetsFactory.getAlarmNet
import it.unito.probability.bayes.BayesNetsFactory.getChildNet
import it.unito.probability.bayes.BayesNetsFactory.getDigitalCircuitNetExample
import it.unito.probability.bayes.BayesNetsFactory.getHailfinderNet
import it.unito.probability.bayes.BayesNetsFactory.getPathfinderNet
import it.unito.probability.bayes.BayesNetsFactory.getSachsNet
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minFillHeuristicFunction
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction
import it.unito.probability.bayes.Inferences.minWeightHeuristicFunction
import it.unito.probability.bayes.Inferences.weightedMinFillHeuristicFunction
import it.unito.probability.utils.ask

fun main(args: Array<String>) {

    var net = getPathfinderNet()

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