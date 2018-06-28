package it.unito.bayesian.net.test

import aima.core.probability.example.DynamicBayesNetExampleFactory
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.CustomDynamicBayesianNet

fun main(args: Array<String>){
    val customNet = CustomDynamicBayesianNet(DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork())
    for(i in 0..100){
        val assignments = ArrayList<AssignmentProposition>()
        for(evRv in customNet.e_1){
            assignments.add(AssignmentProposition(evRv, false))
        }
        customNet.forward(assignments.toTypedArray(), true)
    }
}