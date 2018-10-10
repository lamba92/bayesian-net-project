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
import it.unito.probability.bayes.BayesNetsFactory.getSachsNet
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minFillHeuristicFunction
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction
import it.unito.probability.bayes.Inferences.minWeightHeuristicFunction
import it.unito.probability.bayes.Inferences.weightedMinFillHeuristicFunction
import it.unito.probability.bayes.getPathfinderNet

fun main(args: Array<String>) {
    var net = getPathfinderNet()
    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.STANDARD,
            hMetrics = minNeighboursHeuristicFunction(),
            removeIrrelevantRVs = false,
            showMoralGraph = false,
            delay = 1000
    )


    val f16 = RandVar("F16", BooleanDomain())
    val flt = RandVar("FAULT", BooleanDomain())
    val queryVar = RandVar("F96", BooleanDomain())


    //val queryVar = net.variablesInTopologicalOrder.first()
    //val evidenceVar = net.variablesInTopologicalOrder.last()
    //val evidenceVar = RandVar("PRESS", BooleanDomain())

    val res = inference.ask(arrayOf(queryVar), arrayOf(AssignmentProposition(f16, true), AssignmentProposition(flt, false)), net) as CustomFactor
    /*val res = inference.ask(arrayOf(queryVar),
                           arrayOf(AssignmentProposition(ExampleRV.JOHN_CALLS_RV, true),
                                   AssignmentProposition(ExampleRV.MARY_CALLS_RV,true)), net) as CustomFactor*/

    println(res.printTable())
}