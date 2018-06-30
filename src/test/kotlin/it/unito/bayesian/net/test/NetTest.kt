package it.unito.bayesian.net.test

import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.Inferences
import it.unito.bayesian.net.utils.parseBifXML

fun main(args: Array<String>){
    val net = parseBifXML()
    val ask = Inferences.getCustomEliminationAsk(Inferences.minWeightHeuristicFunction())
    val o = net.variablesInTopologicalOrder[0]
    val x = net.variablesInTopologicalOrder[net.variablesInTopologicalOrder.size - 1]

    val ok = ask.eliminationAsk(arrayOf(o), arrayOf(AssignmentProposition(x, true)), net)
    print(ok.toString())
}