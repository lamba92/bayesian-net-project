package it.unito.probability.main

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.example.BayesNetExampleFactory.constructBurglaryAlarmNetwork
import aima.core.probability.example.ExampleRV
import aima.core.probability.example.ExampleRV.*
import aima.core.probability.proposition.AssignmentProposition
import it.unito.probability.CustomFactor
import it.unito.probability.CustomProbabilityTable
import it.unito.probability.bayes.BayesNetsFactory
import it.unito.probability.bayes.BayesNetsFactory.getDigitalCircuitNetExample
import it.unito.probability.bayes.BayesNetsFactory.i
import it.unito.probability.bayes.BayesNetsFactory.j
import it.unito.probability.bayes.BayesNetsFactory.o
import it.unito.probability.bayes.BayesNetsFactory.x
import it.unito.probability.bayes.BayesNetsFactory.y
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.utils.ask


fun main(args: Array<String>){
    val net = getDigitalCircuitNetExample()
    val inference = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.STANDARD,
            removeIrrelevantRVs = true
    )
    val aimaInference = EliminationAsk()
    val query = arrayOf(i as RandomVariable)
    val ap = arrayOf(AssignmentProposition(j, true), AssignmentProposition(o, false))
    val res = inference.ask(query, ap, net) as CustomProbabilityTable
    val aimaRes = aimaInference.ask(query, ap, net)
    println(res.printTable())
    println(aimaRes)
}