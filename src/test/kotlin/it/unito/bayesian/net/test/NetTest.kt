package it.unito.bayesian.net.test

import it.unito.bayesian.net.utils.parseBifXML

fun main(args: Array<String>){
    val path = "C:\\Users\\Cesare Iurlaro\\IdeaProjects\\bayesian-net-project\\src\\main\\resources\\bifXML\\dog.bif"
    val net = parseBifXML(path)
    /*val ask = Inferences.getCustomEliminationAsk(Inferences.minWeightHeuristicFunction())
    val o = net.variablesInTopologicalOrder[0]
    val x = net.variablesInTopologicalOrder[net.variablesInTopologicalOrder.size - 1]

    val ok = ask.eliminationAsk(arrayOf(o), arrayOf(AssignmentProposition(x, true)), net)
    print(ok.toString())*/
}