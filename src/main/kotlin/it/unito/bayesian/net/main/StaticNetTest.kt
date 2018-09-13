package it.unito.bayesian.net.main

import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.example.BayesNetExampleFactory.constructBurglaryAlarmNetwork
import aima.core.probability.example.ExampleRV
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.Inferences.getCustomEliminationAsk
import it.unito.bayesian.net.KCustomEliminationAsk
import it.unito.bayesian.net.KCustomEliminationAsk.InferenceMethod.STANDARD
import it.unito.bayesian.net.utils.ask

fun main(args: Array<String>){
    val net = constructBurglaryAlarmNetwork()
    val inference = getCustomEliminationAsk(
            inferenceMethod = KCustomEliminationAsk.InferenceMethod.MPE,
            removeIrrelevantRVs = true
    )
    val aimaInference = EliminationAsk()
    val query = ExampleRV.MARY_CALLS_RV
    val ap = AssignmentProposition(ExampleRV.EARTHQUAKE_RV, true)
    val res = inference.ask(query, ap, net)
    val aimaRes = aimaInference.ask(query, ap, net)
    println(res)
    println(aimaRes)
}