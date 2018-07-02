package it.unito.bayesian.net.utils

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.util.RandVar
import weka.classifiers.bayes.net.BIFReader
import weka.classifiers.bayes.net.EditableBayesNet
import java.util.*

/**
 * Used for parsing a BIFXML file to build a [BayesNet].
 * @return A [BayesNet]
 */
fun parseBifXML(path: String): BayesNet {

    val bifReader = BIFReader()
    val network = bifReader.processFile(path)
    val wekaBayesNet = EditableBayesNet(network)

    return buildAimaBayesNet(wekaBayesNet)

}

/**
 * Print an [EditableBayesNet].
 * @param wekaBayesNet The [EditableBayesNet]
 */
fun printWekaBayesNet(wekaBayesNet: EditableBayesNet) {

    for (i in 0 until wekaBayesNet.nrOfNodes) {
        val nodeName = wekaBayesNet.getNodeName(i)
        val parentsCardinality = wekaBayesNet.getParentCardinality(i) - 1

        println("\nVariabile:\n ~ $nodeName")
        if (parentsCardinality != 0) {
            println("Parent nodes: ")
            wekaBayesNet.getParentSet(i).parents.forEach { if (it != 0) println(" - ${wekaBayesNet.getNodeName(it)}") }
        }
        wekaBayesNet.getDistribution(nodeName).forEach { println("   ${Arrays.toString(it)}") }
    }
}

/**
 * Convert an [EditableBayesNet] into a [BayesNet]
 * @param wekaBayesNet The [EditableBayesNet] to convert into a [BayesNet]
 * @return The [BayesNet] copy of the input [EditableBayesNet]
 */
fun buildAimaBayesNet(wekaBayesNet: EditableBayesNet): BayesNet {
    val nodes = HashMap<String, Node>()

    for (i in 0 until wekaBayesNet.nrOfNodes) {
        if (wekaBayesNet.getChildren(i).size == 0) {
            val rv = RandVar(wekaBayesNet.getNodeName(i), BooleanDomain())
            val distribution = flatten2dArray(wekaBayesNet.getDistribution(rv.name))!!
            creation(wekaBayesNet, rv, distribution, nodes)
        }
    }
    return BayesNet(*nodes.values.toTypedArray())
}

/**
 * Create a [FullCPTNode] copy and all of his [FullCPTNode] ancestors copies of an [EditableBayesNet] Node,
 * starting from theirs [RandomVariable]s and [distribution]s and saving them into [nodes]
 * @param wekaBayesNet [EditableBayesNet] to be copied
 * @param rv [RandomVariable] of the [FullCPTNode] to be copied
 * @param distribution Distribution of the [FullCPTNode] to be copied
 * @param nodes [HashMap] which contains all the already created [FullCPTNode]s
 * @return [FullCPTNode] already linked to his ancestors
 */
private fun creation(wekaBayesNet: EditableBayesNet, rv: RandomVariable, distribution: DoubleArray, nodes: HashMap<String, Node>): Node? {

    val parentsCardinality = wekaBayesNet.getParentCardinality(wekaBayesNet.getNode(rv.name))
    if (parentsCardinality != 0) {
        nodes[rv.name] = bottomUpCreation(wekaBayesNet, rv, distribution, nodes)
    } else {
        nodes[rv.name] = FullCPTNode(rv, distribution)
    }
    return nodes[rv.name]
}

/**
 * Helper of the [creation] method, which creates the ancestors before the actual [FullCPTNode]
 * @param wekaBayesNet [EditableBayesNet] to be copied
 * @param rv [RandomVariable] of the [FullCPTNode] to be copied
 * @param distribution Distribution of the [FullCPTNode] to be copied
 * @param nodes [HashMap] which contains all the already created [FullCPTNode]s
 * @return [FullCPTNode] already linked to his ancestors
 */
private fun bottomUpCreation(wekaBayesNet: EditableBayesNet, rv: RandomVariable, distribution: DoubleArray, nodes: HashMap<String, Node>): FullCPTNode {
    var parentsNames = wekaBayesNet.parentSets[wekaBayesNet.getNode(rv.name)].parents.toList()
            //.filter { lambda } //al posto della sublist successiva si può scrivere una funzione qui
            .map(wekaBayesNet::getNodeName)

    parentsNames = parentsNames.subList(0, wekaBayesNet.getNrOfParents(wekaBayesNet.getNode(rv.name)))

    val parentsNodes = ArrayList<Node>()
    parentsNames.forEach {
        if (nodes.containsKey(it)) {
            parentsNodes.add(nodes[it]!!)
        } else {
            val parentDistribution = flatten2dArray(wekaBayesNet.getDistribution(it))!!
            val parentRv = RandVar(it, BooleanDomain())
            val node = creation(wekaBayesNet, parentRv, parentDistribution, nodes)
            parentsNodes.add(node!!)
        }
    }
    return FullCPTNode(rv, distribution, *parentsNodes.toTypedArray())
}

/**
 * Convert an Array<DoubleArray> into a DoubleArray flattening it in a single row
 * @param toBeFlattened table of double values
 * @return [DoubleArray] transposition of the input table in a single row
 */
private fun flatten2dArray(toBeFlattened: Array<DoubleArray>): DoubleArray? {
    return Arrays.stream(toBeFlattened)
            .flatMapToDouble(Arrays::stream).toArray()
}