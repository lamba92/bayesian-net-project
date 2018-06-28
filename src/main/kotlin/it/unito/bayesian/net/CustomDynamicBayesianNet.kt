//@file:Suppress("HasPlatformType", "FunctionName")

package it.unito.bayesian.net

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.DynamicBayesianNetwork
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.CPT
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.proposition.AssignmentProposition
import it.unito.bayesian.net.utils.generateVectorFromCPT
import java.util.*
import it.unito.bayesian.net.utils.getNext
import it.unito.bayesian.net.utils.log

/**
 * Representation of a Dynamic Bayesian network unrolling using
 * rollup filtering.
 */
class CustomDynamicBayesianNet: DynamicBayesianNetwork {

    /**
     * Constructor for a newly generated network.
     * @param priorNetwork The original beliefs eventually used to start from beginning.
     * @param X_0_to_X_1 A [Map] between [RandomVariable]s connecting the beliefs to the status variables.
     * @param E_1 A [Set] of [RandomVariable]s contained inside their nodes.
     * @param rootNodes The root [Node]s to be used to initialize the network.
     *
     */
    constructor(priorNetwork: BayesianNetwork,
                X_0_to_X_1: Map<RandomVariable, RandomVariable>,
                E_1: Set<RandomVariable>,
                vararg rootNodes: Node){
        currentSlice = DynamicBayesNet(priorNetwork, X_0_to_X_1, E_1, *rootNodes)
    }

    /**
     * Constructor for a newly generated network.
     * @param net The [DynamicBayesianNetwork] which will be evolved over time.
     */
    constructor(net: DynamicBayesianNetwork){
        currentSlice = net
    }

    private var currentSlice: DynamicBayesianNetwork


    /**
     * Allows to transition the network one slice ahead.
     * @param propositions The [Array]<[AssignmentProposition]> used to represent the observation of the evidence [RandomVariable]s.
     * @param verbose Allows to print logs while calculating. Default is `false`.
     */
    fun forward(propositions: Array<AssignmentProposition> = emptyArray(), verbose: Boolean = false){
        val nextBeliefs = HashMap<RandomVariable, Node>()
        val nextRootNodes = ArrayList<Node>()
        val newStateVariables = HashMap<RandomVariable, Node>()
        val X_1_to_X_2 = HashMap<RandomVariable, RandomVariable>()
        val newEvidences = HashMap<RandomVariable, Node>()

        // This cycle create the new beliefs nodes calculating
        // through transition model and an ask on the evidences
        if(verbose) log("\nGenerating new beliefs nodes...")
        currentSlice.x_0_to_X_1.forEach { x0, x1 ->
            val newCPT = Inferences.eliminationAskWithMinWeightHeuristic.ask(
                    arrayOf(x1), propositions, currentSlice
            )
            val node = FullCPTNode(x1, newCPT.values)
            nextBeliefs[x1] = node
            if(currentSlice.getNode(x0).parents.isEmpty()) nextRootNodes.add(nextBeliefs[x1]!!)
            if(verbose){
                log("$x1 ${Arrays.toString(newCPT.values)}")
            }
        }
        if(verbose) log("\nGenerating new state variables nodes...")
        // This cycle create the new state variables nodes extracting
        // the transition model from previous nodes
        for(oldRV in currentSlice.x_1){
            val parents = currentSlice.getNode(oldRV).parents
            val newParents = ArrayList<Node>()
            val newRv = oldRV.getNext()
            for(parent in parents) {
                val parentRV = parent.randomVariable
                newParents.add(nextBeliefs[currentSlice.x_0_to_X_1[parentRV]]!!)
            }
            val distribution = generateVectorFromCPT(currentSlice.getNode(oldRV).cpd as CPT).toDoubleArray()
            newStateVariables[newRv] = FullCPTNode(newRv, distribution, *newParents.toTypedArray())
            X_1_to_X_2[oldRV] = newRv
            if (verbose){
                log("Old RV name: $oldRV\n" +
                        "New RV name: $newRv\n" +
                        "cpt: ${Arrays.toString(distribution)}")
            }
        }

        if(verbose) log("Generating new evidences nodes...")
        // This cycle create the new evidence variables nodes extracting
        // the sensor model from previous nodes
        for(oldEvRv in currentSlice.e_1){
            val parents = currentSlice.getNode(oldEvRv).parents
            val newParents = ArrayList<Node>()
            val newEvRv = oldEvRv.getNext()
            for(parent in parents){
                val parentRv = parent.randomVariable
                newParents.add(newStateVariables[X_1_to_X_2[parentRv]]!!)
            }
            val distribution = generateVectorFromCPT(currentSlice.getNode(oldEvRv).cpd as CPT).toDoubleArray()
            newEvidences[newEvRv] = FullCPTNode(newEvRv, distribution, *newParents.toTypedArray())
            if (verbose){
                log("Old RV name: $oldEvRv\n" +
                        "New RV name: $newEvRv\n" +
                        "cpt: ${Arrays.toString(distribution)}")
            }
        }

        currentSlice = DynamicBayesNet(currentSlice.priorNetwork, X_1_to_X_2, newEvidences.keys, *nextRootNodes.toTypedArray())
    }

    override fun getE_1() = currentSlice.e_1

    override fun getX_1_VariablesInTopologicalOrder() = currentSlice.x_1_VariablesInTopologicalOrder

    override fun getX_1_to_X_0() = currentSlice.x_0_to_X_1

    override fun getNode(rv: RandomVariable?) = currentSlice.getNode(rv)

    override fun getPriorNetwork() = currentSlice.priorNetwork

    override fun getX_0() = currentSlice.x_0

    override fun getX_1() = currentSlice.x_1

    override fun getVariablesInTopologicalOrder() = currentSlice.variablesInTopologicalOrder

    override fun getX_0_to_X_1() = currentSlice.x_0_to_X_1
}