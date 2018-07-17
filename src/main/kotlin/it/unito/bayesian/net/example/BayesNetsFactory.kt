package it.unito.bayesian.net.example

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.util.RandVar

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
}