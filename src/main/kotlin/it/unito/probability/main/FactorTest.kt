package it.unito.probability.main

import it.unito.probability.CustomProbabilityTable
import it.unito.probability.CustomFactorFactory.getTestFactor

fun main(args: Array<String>){
    val f1 = getTestFactor("A", "B", "C")
    val f2 = getTestFactor("B", "C", "D")
    val f3 = (f1.pointwiseProduct(f2) as CustomProbabilityTable).normalize()
    println(f1.table.values.sum())
    println(f2.table.values.sum())
    println(f3.table.values.sum())
    val f4 = f1.sumOut(f1.argumentVariables.first(), f1.argumentVariables.last())
    println(f1)
    println()
    println(f2)
    println()
    println(f4)
}