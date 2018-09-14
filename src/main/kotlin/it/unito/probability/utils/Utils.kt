package it.unito.probability.utils

import aima.core.probability.bayes.impl.CPT
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.Node
import it.unito.probability.bayes.CustomEliminationAsk
import it.unito.probability.bayes.Inferences.getCustomEliminationAsk
import it.unito.probability.bayes.Inferences.minNeighboursHeuristicFunction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun generateVectorFromCPT(cpt: CPT, verbose: Boolean = false): Array<Double> {
    val N = cpt.parents.size

    // array to store combinations
    val reverseFlip = arrayOfBooleanArrays(N).reversedArray()
    if(verbose){
        println("Original truth table before reversing")
        printTruthTable(reverseFlip.reversedArray())

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

fun arrayOfBooleanArrays(N: Int): Array<BooleanArray> {
    // number of combinations
    // using bitshift to power 2
    val NN = 1 shl N

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
    return flips
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

fun log(str: String){
    println(str)
}

fun combineParents(parents: Iterator<MoralGraph.MoralNode>): HashMap<RandomVariable, RandomVariable> {
    return combineParents(ArrayList<MoralGraph.MoralNode>().apply {
        for(p in parents) add(p)
    })
}

fun combineParents(parents: Collection<Any>): HashMap<RandomVariable, RandomVariable> {
    val i = ArrayList<RandomVariable>()
    for(o in parents){
        when (o) {
            is Node -> i.add(o.randomVariable)
            is MoralGraph.MoralNode -> i.add(o.randomVariable!!)
            else -> throw Exception("Wrong class")
        }
    }
    val map = HashMap<RandomVariable, RandomVariable>()
    for (parent1 in i) {
        for (parent2 in i) {
            if (parent1 != parent2) {
                map[parent1] = parent2
            }
        }
    }

    val iter = map.iterator()
    while (iter.hasNext()){
        val j = iter.next()
        if(map.containsKey(j.value) && map[j.value] == j.key) iter.remove()
    }

    return map
}

fun generateRandomListOfNumbers(targetSum: Int, numberOfDraws: Int): ArrayList<Int> {
    val r = Random()
    val load = ArrayList<Int>()

    //random numbers
    var sum = 0
    for (i in 0 until numberOfDraws) {
        val next = r.nextInt(targetSum) + 1
        load.add(next)
        sum += next
    }

    //scale to the desired target sum
    val scale = 1.0 * targetSum / sum
    sum = 0
    for (i in 0 until numberOfDraws) {
        load[i] = (load[i] * scale).toInt()
        sum += load[i]
    }

    //take rounding issues into account
    while (sum++ < targetSum) {
        val i = r.nextInt(numberOfDraws)
        load[i] = load[i] + 1
    }

    return load
}
