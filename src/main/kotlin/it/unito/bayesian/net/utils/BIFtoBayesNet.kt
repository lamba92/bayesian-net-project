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


fun buildAimaBayesNet(wekaBayesNet: EditableBayesNet) {
    lateinit var nodeName: String
    val nodes = HashMap<String, Node>()

    for (i in 0 until wekaBayesNet.nrOfNodes) {
        creation(wekaBayesNet, i, nodes)
    }

    var aimaBayesNet = BayesNet(*nodes.values.toTypedArray())
}


fun creation(wekaBayesNet: EditableBayesNet, i: Int, nodes: HashMap<String, Node>) {
    val nodeName = wekaBayesNet.getNodeName(i)
    val distribution = flatten2dArray(wekaBayesNet.getDistribution(nodeName))!!
    val rv = RandVar(nodeName, BooleanDomain())

    val parentsCardinality = wekaBayesNet.getParentCardinality(i) - 1
    if (parentsCardinality != 0) {
        nodes[nodeName] = bottomUpCreation(wekaBayesNet, nodeName, nodes)
    } else {
        nodes[nodeName] = FullCPTNode(rv, distribution)
    }

}

fun bottomUpCreation(wekaBayesNet: EditableBayesNet, nodeName: String, nodes: HashMap<String, Node>): FullCPTNode {
    val distribution = flatten2dArray(wekaBayesNet.getDistribution(nodeName))!!
    val rv = RandVar(nodeName, BooleanDomain())
    val i = wekaBayesNet.getNode(nodeName)

    val parentsNames = wekaBayesNet.getParentSet(i).parents.toList()
            .filter { parent -> parent != 0 }
            .map(wekaBayesNet::getNodeName)

    val parentsNodes = ArrayList<Node>()
    parentsNames.forEach {
        if (nodes[it] != null) {
            parentsNodes.add(nodes[it]!!)
        } else {
            parentsNodes.add(bottomUpCreation(wekaBayesNet, it, nodes))
        }
    }
    return FullCPTNode(rv, distribution, *parentsNodes.toTypedArray())
}


fun flatten2dArray(toBeFlattened: Array<DoubleArray>): DoubleArray? {
    return Arrays.stream(toBeFlattened)
            .flatMapToDouble(Arrays::stream).toArray()
}