package it.unito.bayesian.net.utils

import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.util.RandVar
import weka.classifiers.bayes.net.BIFReader
import weka.classifiers.bayes.net.EditableBayesNet
import java.util.*


fun parseBifXML() {

    val bifReader = BIFReader()
    val path = "C:\\Users\\Cesare Iurlaro\\IdeaProjects\\bayesian-net-project\\src\\main\\resources\\bifXML\\dog.bif"
    val network = bifReader.processFile(path)

    val wekaBayesNet = EditableBayesNet(network)
    //printWekaBayesNet(wekaBayesNet)
    buildAimaBayesNet(wekaBayesNet)

}

fun printWekaBayesNet(wekaBayesNet: EditableBayesNet) {

    for (i in 0 until wekaBayesNet.nrOfNodes) {
        val nodeName = wekaBayesNet.getNodeName(i)
        val parentsCardinality = wekaBayesNet.getParentCardinality(i) - 1

        println("\nVariabile:\n ~ $nodeName")
        if (parentsCardinality != 0) {
            println("Genitori: ")
            wekaBayesNet.getParentSet(i).parents.forEach { if (it != 0) println(" - ${wekaBayesNet.getNodeName(it)}") }
        }
        wekaBayesNet.getDistribution(nodeName).forEach { println("   ${Arrays.toString(it)}") }
    }
}


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


fun flatten2dArray(toBeFlattened: Array<DoubleArray>): DoubleArray? {
    return Arrays.stream(toBeFlattened)
            .flatMapToDouble(Arrays::stream).toArray()
}