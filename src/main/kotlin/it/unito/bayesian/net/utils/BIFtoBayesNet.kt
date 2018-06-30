package it.unito.bayesian.net.utils

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
fun parseBifXML(): BayesNet {

    val bifReader = BIFReader()
    val path = "C:\\Users\\Cesare Iurlaro\\IdeaProjects\\bayesian-net-project\\src\\main\\resources\\bifXML\\dog.bif"
    val network = bifReader.processFile(path)

    val wekaBayesNet = EditableBayesNet(network)
    //printWekaBayesNet(wekaBayesNet)
    return buildAimaBayesNet(wekaBayesNet)

}

/**
 * Print the [EditableBayesNet] to operate with.
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
 * Allow to create a [BayesNet] from an [EditableBayesNet]
 * @param wekaBayesNet The [EditableBayesNet]
 * @return A [BayesNet]
 */
fun buildAimaBayesNet(wekaBayesNet: EditableBayesNet): BayesNet {
    val nodes = HashMap<String, Node>()

    for (i in 0 until wekaBayesNet.nrOfNodes) {
        if (wekaBayesNet.getChildren(i).size == 0) {
            val nodeName = wekaBayesNet.getNodeName(i)
            val distribution = flatten2dArray(wekaBayesNet.getDistribution(nodeName))!!
            val v = creation(wekaBayesNet, i, nodes, nodeName, distribution)
        }
    }
    return BayesNet(*nodes.values.toTypedArray())
}

/**
 * Allow to create ???
 * @param wekaBayesNet The [EditableBayesNet]
 * @param i
 * @param nodes
 * @param nodeName
 * @param distribution
 * @return [Node]
 */
fun creation(wekaBayesNet: EditableBayesNet, i: Int, nodes: HashMap<String, Node>, nodeName: String, distribution: DoubleArray): Node? {
    val rv = RandVar(nodeName, BooleanDomain())

    val parentsCardinality = wekaBayesNet.getParentCardinality(i)
    if (parentsCardinality != 0) {
        nodes[nodeName] = bottomUpCreation(wekaBayesNet, nodeName, nodes, distribution)
    } else {
        nodes[nodeName] = FullCPTNode(rv, distribution)
    }
    return nodes[nodeName]
}

/**
 * ???
 * @param wekaBayesNet
 * @param nodeName
 * @param nodes
 * @param distribution
 * @return [FullCPTNode]
 */
fun bottomUpCreation(wekaBayesNet: EditableBayesNet, nodeName: String, nodes: HashMap<String, Node>, distribution: DoubleArray): FullCPTNode {
    val rv = RandVar(nodeName, BooleanDomain())
    val i = wekaBayesNet.getNode(nodeName)

    val parentsNames = wekaBayesNet.getParentSet(i).parents.toList()
            .filter { parent -> parent != 0 }
            .map(wekaBayesNet::getNodeName)

    val parentsNodes = ArrayList<Node>()
    parentsNames.forEach {
        if (nodes.containsKey(it)) {
            parentsNodes.add(nodes[it]!!)
        } else {
            val parentIndex = wekaBayesNet.getNode(it)
            val parentDistribution = flatten2dArray(wekaBayesNet.getDistribution(it))!!
            val node = creation(wekaBayesNet, parentIndex, nodes, it, parentDistribution)
            parentsNodes.add(node!!)
        }
    }
    return FullCPTNode(rv, distribution, *parentsNodes.toTypedArray())
}

/**
 * ???
 * @param toBeFlattened
 * @return [DoubleArray]
 */
fun flatten2dArray(toBeFlattened: Array<DoubleArray>): DoubleArray? {
    return Arrays.stream(toBeFlattened)
            .flatMapToDouble(Arrays::stream).toArray()
}