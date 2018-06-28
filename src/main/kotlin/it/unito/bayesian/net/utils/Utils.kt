package it.unito.bayesian.net.utils

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.impl.CPT
import aima.core.probability.util.RandVar
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun generateVectorFromCPT(cpt: CPT, verbose: Boolean = false): Array<Double> {
    val N = cpt.parents.size

    // number of combinations
    // using bitshift to power 2
    val NN = 1 shl N

    // array to store combinations
    val flips = Array(NN) { BooleanArray(N) }

    // generating an array
    // enumerating combinations
    for (nn in 0 until NN) {

        // enumerating flips
        for (n in 0 until N) {

            // using the fact that binary nn number representation
            // is what we need
            // using bitwise functions to get appropriate bit
            // and converting it to boolean with ==
            flips[nn][N - n - 1] = nn shr n and 1 == 1

            // this is simpler bu reversed
            //flips[nn][n] = (((nn>>n) & 1)==1);
        }
    }
    val reverseFlip = flips.reversedArray()
    if(verbose){
        println("Original truth table before reversing")
        printTruthTable(flips)

        println("Reversed truth table")
        printTruthTable(reverseFlip)
    }
    val output = ArrayList<ArrayList<Double>>()
    for(line in reverseFlip){
        val d = cpt.getConditioningCase(*line.toTypedArray()).values
        output.add(ArrayList(d.toList()))
        if(verbose){println(Arrays.toString(d))}
    }

    return ArrayList<Double>().apply {
        for(line in output) addAll(line)
    }.toTypedArray()
}

fun printTruthTable(array: Array<BooleanArray>){
    for (nn in 0 until array.size) {

        print("$nn: ")

        for (n in 0 until array[nn].size) {
            print(if (array[nn][n]) "T " else "F ")
        }
        println("")
    }
}

fun RandomVariable.getNext(): RandVar{
    val pattern = Pattern.compile("([0-9]+$)")
    val m = pattern.matcher(name)
    return if (m.find()){
        val current = (m.group(m.groupCount()-1).toInt() + 1).toString()
        RandVar(name.replace(Regex("([0-9]+$)"), current), domain)
    } else RandVar(name + "_1", domain)

}

fun log(str: String){
    println(str)
}
