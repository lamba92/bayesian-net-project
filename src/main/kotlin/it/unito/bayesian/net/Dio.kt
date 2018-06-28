package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.CPT
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.example.DynamicBayesNetExampleFactory
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import it.unito.bayesian.net.example.BayesNetsFactory.e_1
import it.unito.bayesian.net.example.BayesNetsFactory.getDecentDynamicNetworkExample
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import java.util.Arrays

fun main(array: Array<String>){
    val dbn = getDecentDynamicNetworkExample()
    val nextStepMapNodes = HashMap<RandomVariable, Node>()
    val nextRootNodes = ArrayList<Node>()

    dbn.x_0_to_X_1.forEach { x0, x1 ->
        val newCPT = InferenceUsing.eliminationAsk.withMinWeightHeuristic.eliminationAsk(
                arrayOf(x1), arrayOf(AssignmentProposition(e_1, false)), dbn
        )
        val node = FullCPTNode(x1, newCPT.values)
        nextStepMapNodes[x1] = node
        println("$x1 ${Arrays.toString(newCPT.values)}")
        if(dbn.getNode(x0).parents.isEmpty()) nextRootNodes.add(nextStepMapNodes[x1]!!)
    }

    val newNodes = HashMap<RandomVariable, Node>()
    val newRvMap = HashMap<RandomVariable, RandomVariable>()
    for(oldRV in dbn.x_1){
        val parents = dbn.getNode(oldRV).parents
        val newParents = ArrayList<Node>()
        val newRv = oldRV.getNext()
        for(parent in parents) {
            val parentRV = parent.randomVariable
            newParents.add(nextStepMapNodes[dbn.x_0_to_X_1[parentRV]]!!)
        }
        val distribution = generateVectorFromCPT(dbn.getNode(oldRV).cpd as CPT).toDoubleArray()
        newNodes[newRv] = FullCPTNode(newRv, distribution, *newParents.toTypedArray())
        newRvMap[oldRV] = newRv
    }

    val newEvidences = HashMap<RandomVariable, Node>()
    for(oldEvRv in dbn.e_1){
        val parents = dbn.getNode(oldEvRv).parents
        val newParents = ArrayList<Node>()
        val newEvRv = oldEvRv.getNext()
        for(parent in parents){
            val parentRv = parent.randomVariable
            newParents.add(newNodes[newRvMap[parentRv]]!!)
        }
        val distribution = generateVectorFromCPT(dbn.getNode(oldEvRv).cpd as CPT).toDoubleArray()
        newEvidences[newEvRv] = FullCPTNode(newEvRv, distribution, *newParents.toTypedArray())
    }

    val newDbn = DynamicBayesNet(dbn.priorNetwork, newRvMap, newEvidences.keys, *nextRootNodes.toTypedArray())

    for(rv in newDbn.x_1){
        for (evRv in newDbn.e_1){
            val ask = InferenceUsing.eliminationAsk.withMinWeightHeuristic.eliminationAsk(
                    arrayOf(rv),
                    arrayOf(AssignmentProposition(evRv, false)),
                    newDbn
            )
            println(ask.toString())
        }
    }

}

fun RandomVariable.getNext(): RandomVariable = RandVar(name.substring(0, name.length - 1) + "dio", domain)

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
