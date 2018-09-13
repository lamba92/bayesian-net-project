package it.unito.bayesian.net.main

import aima.core.probability.CategoricalDistribution
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesInference

import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.example.BayesNetExampleFactory
import aima.core.probability.example.BayesNetExampleFactory.constructBurglaryAlarmNetwork
import aima.core.probability.example.ExampleRV
import aima.core.probability.example.ExampleRV.JOHN_CALLS_RV
import aima.core.probability.example.ExampleRV.MARY_CALLS_RV
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.Inferences.getCustomEliminationAsk
import it.unito.bayesian.net.KCustomEliminationAsk.InferenceMethod.STANDARD
import it.unito.bayesian.net.utils.ask

fun main(args: Array<String>){
    val net = constructBurglaryAlarmNetwork()
    val inference = getCustomEliminationAsk(
            inferenceMethod = STANDARD,
            overrideCV = false
    )
    val aimaInference = EliminationAsk()
    val query = ExampleRV.MARY_CALLS_RV
    val queries = arrayOf(MARY_CALLS_RV, JOHN_CALLS_RV)
    val ap = arrayOf(
            AssignmentProposition(ExampleRV.EARTHQUAKE_RV, true),
            AssignmentProposition(ExampleRV.BURGLARY_RV, true)
    )
    val res = inference.ask(queries.map { it as RandomVariable }.toTypedArray(), ap, net)
    val aimaRes = aimaInference.ask(queries, ap, net)
    println(res)
    println(aimaRes)
}