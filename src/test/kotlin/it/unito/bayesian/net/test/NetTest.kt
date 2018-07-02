package it.unito.bayesian.net.test

import it.unito.bayesian.net.CustomDynamicBayesianNet
import it.unito.bayesian.net.utils.parseBooleanBayesInterchangeFormat

fun main(args: Array<String>){

    val path = CustomDynamicBayesianNet::class.java.classLoader.getResource("bifXML/dog-problem.xml").path //non funge
    val path2 = "C:\\Users\\Cesare Iurlaro\\IdeaProjects\\bayesian-net-project\\src\\main\\resources\\bifXML\\aima-wet-grass.xml"
    val net = parseBooleanBayesInterchangeFormat(path2)
    /*
    val ask = Inferences.getCustomEliminationAsk(Inferences.minWeightHeuristicFunction())
    val o = net.variablesInTopologicalOrder[0]
    val x = net.variablesInTopologicalOrder[net.variablesInTopologicalOrder.size - 1]

    val ok = ask.eliminationAsk(arrayOf(o), arrayOf(AssignmentProposition(x, true)), net)
    print(ok.toString())
    */
}