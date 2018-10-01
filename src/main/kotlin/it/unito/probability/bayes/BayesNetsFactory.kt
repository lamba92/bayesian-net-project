@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_VARIABLE")

package it.unito.probability.bayes

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.CPT
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.util.RandVar
import it.unito.probability.utils.generateVectorFromCPT

/**
 * Example of [DynamicBayesNet]
 */
@Suppress("LocalVariableName")
object BayesNetsFactory{

    val r_0 = RandVar("R_0", BooleanDomain())
    val s_0 = RandVar("S_0", BooleanDomain())

    val r_1 = RandVar("R_1", BooleanDomain())
    val s_1 = RandVar("S_1", BooleanDomain())

    val e_1 = RandVar("E_1",BooleanDomain())

    val j = RandVar("J", BooleanDomain())
    val i = RandVar("I", BooleanDomain())

    val y = RandVar("Y", BooleanDomain())
    val x = RandVar("X", BooleanDomain())
    val o = RandVar("O", BooleanDomain())


    fun getComplexDynamicNetworkExample(): DynamicBayesNet {
        val priorNetwork = BayesNet(
                FullCPTNode(r_0, doubleArrayOf(0.5, 0.5)),
                FullCPTNode(s_0, doubleArrayOf(0.5, 0.5))
        )

        val r_0_node = FullCPTNode(r_0, doubleArrayOf(0.5, 0.5))
        val s_0_node = FullCPTNode(s_0, doubleArrayOf(0.5, 0.5))

        val r_1_node = FullCPTNode(r_1, doubleArrayOf(
                    0.1, 0.9,
                    0.15, 0.85,
                    0.7, 0.3,
                    0.8, 0.2
            ), r_0_node, s_0_node)
        val s_1_node = FullCPTNode(s_1, doubleArrayOf(
                    0.23, 0.77,
                    0.4, 0.6,
                    0.57, 0.43,
                    0.29, 0.71
                ), r_0_node, s_0_node)
        val e_1_node = FullCPTNode(e_1, doubleArrayOf(
                    0.93, 0.07,
                    0.49, 0.51,
                    0.87, 0.13,
                    0.01, 0.99
        ), r_1_node, s_1_node)

        val rvMap = HashMap<RandomVariable, RandomVariable>().apply {
            this[r_0] = r_1
            this[s_0] = s_1
        }
        val evidences = HashSet<RandomVariable>().apply { add(e_1) }
        return DynamicBayesNet(priorNetwork, rvMap, evidences, r_0_node, s_0_node)
    }

    fun getDigitalCircuitNetExample(): BayesianNetwork {
        val j_node = FullCPTNode(j, doubleArrayOf(0.5, 0.5))
        val i_node = FullCPTNode(i, doubleArrayOf(0.5, 0.5))

        val y_node = FullCPTNode(y, doubleArrayOf(
                0.01, 0.99,
                0.99, 0.01),
                j_node
        )

        val x_node = FullCPTNode(x, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95,
                0.05, 0.95,
                0.05, 0.95),
                j_node, i_node
        )

        val o_node = FullCPTNode(o, doubleArrayOf(
                0.98, 0.02,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                y_node, x_node
        )
        return BayesNet(j_node, i_node)
    }

    fun getAdderNetExample(): BayesianNetwork {
        val a_node = FullCPTNode(a, doubleArrayOf(0.5, 0.5))
        val b_node = FullCPTNode(b, doubleArrayOf(0.5, 0.5))
        val cIN_node = FullCPTNode(c_in, doubleArrayOf(0.5, 0.5))

        val xor_node = FullCPTNode(xor, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                a_node, b_node
        )

        val and_node1 = FullCPTNode(and1, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95,
                0.05, 0.95,
                0.05, 0.95),
                xor_node, cIN_node
        )

        val and_node2 = FullCPTNode(and2, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95,
                0.05, 0.95,
                0.05, 0.95),
                a_node, b_node
        )

        val s_node = FullCPTNode(s, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                xor_node, cIN_node
        )

        val cOut_node = FullCPTNode(c_out, doubleArrayOf(
                0.98, 0.02,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                and_node1, and_node2
        )
        return BayesNet(a_node, b_node, cIN_node)
    }


    fun getFullAdderCircuitNet(): BayesianNetwork {

        // Random Variables
        val a = RandVar("A_0", BooleanDomain())
        val b = RandVar ("B_0", BooleanDomain())
        val carryIn = RandVar("CarryIn_0", BooleanDomain())
        // val sum = RandVar("Sum_0", BooleanDomain())
        // val carryOut = RandVar ("CarryOut_0", BooleanDomain())

        // Roots
        val aRootNode = FullCPTNode(a, doubleArrayOf(0.5, 0.5))
        val bRootNode = FullCPTNode(b, doubleArrayOf(0.5, 0.5))
        val carryInRootNode = FullCPTNode(carryIn, doubleArrayOf(0.5, 0.5))
        val rootList = ArrayList<Node>().apply { this.add(aRootNode); this.add(bRootNode); this.add(carryInRootNode)}

        //Nodes
        var aNode = aRootNode
        var bNode = bRootNode
        var carryInNode = carryInRootNode

        for (i in 1..2){ //8962
            val sumTmp = RandVar ("Sum_$i-1", BooleanDomain())
            val sumNode = FullCPTNode(sumTmp, doubleArrayOf(
                    0.99, 0.01,
                    0.01, 0.99,
                    0.01, 0.99,
                    0.99, 0.01,
                    0.01, 0.99,
                    0.99, 0.01,
                    0.99, 0.01,
                    0.01, 0.99),
                    aNode, bNode, carryInNode
            )

            val carryOutTmp = RandVar ("CarryOut_$i", BooleanDomain())
            val carryOutNode = FullCPTNode(carryOutTmp, doubleArrayOf(
                    0.99, 0.01,
                    0.99, 0.01,
                    0.99, 0.01,
                    0.01, 0.99,
                    0.99, 0.01,
                    0.01, 0.99,
                    0.01, 0.99,
                    0.01, 0.99),
                    aNode, bNode, carryInNode
            )

        val aTmp = RandVar("A_$i", BooleanDomain())
        val bTmp = RandVar ("B_$i", BooleanDomain())
        val carryInTmp = RandVar("CarryIn_$i", BooleanDomain())

        aNode = FullCPTNode(aTmp, doubleArrayOf(0.5, 0.5))
        bNode = FullCPTNode(bTmp, doubleArrayOf(0.5, 0.5))
        carryInNode = FullCPTNode(carryInTmp, doubleArrayOf(0.99, 0.01, 0.01, 0.99), carryOutNode)
        rootList.apply { this.add(aNode); this.add(bNode) }
        }
        return BayesNet(*rootList.toTypedArray())
    }

}