package it.unito.bayesian.net.utils

import aima.core.probability.bayes.impl.CPT
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.Node
import aima.core.probability.util.RandVar
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    for (parent1 in i){
        for (parent2 in i){
            if(parent1 != parent2) {
                map[parent1] = parent2
                map[parent2] = parent1
            }
        }
    }

//    map.entries.removeIf { map[it.value]==it.key }

    val iter = map.iterator()
    while (iter.hasNext()){
        val j = iter.next()
        if(map.containsKey(j.value) && map[j.value] == j.key) iter.remove()
    }

    return map
}

/**
 * Checks if [rv] is parent of one of the [nextRvs] in a given [net].
 */
fun isParentOf(rv: RandomVariable, nextRvs: List<RandomVariable>, net: BayesianNetwork): Boolean {
    for(nextRv in nextRvs){
        for(parentNode in net.getNode(nextRv).parents){
            val parentRv = parentNode.randomVariable
            if(rv == parentRv) return true
        }
    }
    return false
}

fun isNotParentOf(rv: RandomVariable, nextRvs: List<RandomVariable>, net: BayesianNetwork) = !isParentOf(rv, nextRvs, net)

fun generateRvsToBeSummedOut(rvs: Collection<RandomVariable>, nextRvs: List<RandomVariable>, net: BayesianNetwork): List<RandomVariable> {
    val toReturn = ArrayList<RandomVariable>()
    for(rv in rvs)
        if(isNotParentOf(rv, nextRvs, net))
            toReturn.add(rv)
    return toReturn
}

fun Node.isAncestorOf(node: Node): Boolean {
    return if(node.parents.contains(this)) true
        else {
            node.parents.forEach { if(it.isAncestorOf(this)) return true }
            false
        }
}

fun RandomVariable.isAncestorOf(rv: RandomVariable, bn: BayesianNetwork) =
        bn.getNode(this).isAncestorOf(bn.getNode(rv))

fun RandomVariable.isAncestorOf(rvs: Collection<RandomVariable>, bn: BayesianNetwork): Boolean {
    rvs.forEach {
        if(this.isAncestorOf(it, bn))
            return true
    }
    return false
}

fun RandomVariable.isNotAncestorOf(rvs: Collection<RandomVariable>, bn: BayesianNetwork) =
        !this.isAncestorOf(rvs, bn)
