package it.unito.probability.utils

import aima.core.probability.Factor
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesInference
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.CPT
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import it.unito.probability.CustomProbabilityTable
import java.util.regex.Pattern

fun CPT.generateVector(verbose: Boolean = false) = generateVectorFromCPT(this, verbose)

fun Node.isAncestorOf(eventuallyChildNode: Node): Boolean {
    return if(eventuallyChildNode.parents.contains(this)) true
    else {
        for(parent in eventuallyChildNode.parents)
            if(isAncestorOf(parent)) return true
        false
    }
}

fun RandomVariable.isAncestorOf(rv: RandomVariable, bn: BayesianNetwork) =
        bn.getNode(this).isAncestorOf(bn.getNode(rv))

fun RandomVariable.isAncestorOf(rvs: Collection<RandomVariable>, bn: BayesianNetwork): Boolean {
    rvs.forEach {
        if(isAncestorOf(it, bn)) return true
    }
    return false
}

fun RandomVariable.isNotAncestorOf(rvs: Collection<RandomVariable>, bn: BayesianNetwork) =
        !this.isAncestorOf(rvs, bn)

fun Factor.convertToCustom(verbose: Boolean = false): CustomProbabilityTable {
    val table = HashMap<HashMap<RandomVariable, Any>, Double>()
    iterateOver { possibleAssignment, probability ->
        table[HashMap(possibleAssignment)] = probability
    }
    return CustomProbabilityTable(table, verbose = verbose)
}

fun BayesInference.ask(X: Collection<RandomVariable>, e: Collection<AssignmentProposition>, bn: BayesianNetwork) =
        ask(X.toTypedArray(), e.toTypedArray(), bn)

fun Collection<CustomProbabilityTable>.multiplyAll(): CustomProbabilityTable {
    if(size == 1) return this.first()

    val it = iterator()
    var res = it.next() * it.next()
    it.forEachRemaining {
        res *= it
    }
    return res
}

fun BayesInference.ask(X: RandVar, proposition: AssignmentProposition, bn: BayesianNetwork) = ask(arrayOf(X), arrayOf(proposition), bn)
fun BayesInference.ask(X: Array<RandVar>, observedEvidences: AssignmentProposition, bn: BayesianNetwork?) = ask(X, arrayOf(observedEvidences), bn)

fun <K, V> MutableMap<K, V>.put(entry: Map.Entry<K, V>) = put(entry.key, entry.value)

fun RandomVariable.getNext(): RandVar{
    val pattern = Pattern.compile("([0-9]+$)")
    val m = pattern.matcher(name)
    return if (m.find()){
        val current = (m.group(m.groupCount()-1).toInt() + 1).toString()
        RandVar(name.replace(Regex("([0-9]+$)"), current), domain)
    } else RandVar(name + "_1", domain)
}