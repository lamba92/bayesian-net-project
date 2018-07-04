package it.unito.bayesian.net.main

import aima.core.probability.example.BayesNetExampleFactory.constructBurglaryAlarmNetwork
import aima.core.probability.example.ExampleRV
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.CustomDynamicBayesianNet
import it.unito.bayesian.net.Inferences
import it.unito.bayesian.net.example.BayesNetsFactory
import it.unito.bayesian.net.utils.parseBooleanBayesInterchangeFormat

fun main(args: Array<String>){
//    val path = CustomDynamicBayesianNet::class.java.classLoader.getResource("bifXML/aima-alarm.xml").path //non funge
    //val path2 = "C:\\Users\\Cesare Iurlaro\\IdeaProjects\\bayesian-net-project\\src\\main\\resources\\bifXML\\dog.bif"
    val net = constructBurglaryAlarmNetwork()

    val ask = Inferences.getCustomEliminationAsk(Inferences.minWeightHeuristicFunction())

    val ok = ask.ask(arrayOf(ExampleRV.BURGLARY_RV), arrayOf(AssignmentProposition(ExampleRV.JOHN_CALLS_RV, true)), net)
    print(ok.toString())
}