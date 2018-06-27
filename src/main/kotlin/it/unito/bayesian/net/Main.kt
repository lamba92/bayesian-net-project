//import aima.core.probability.CategoricalDistribution
//import aima.core.probability.RandomVariable
//import aima.core.probability.bayes.ConditionalProbabilityDistribution
//import aima.core.probability.bayes.Node
//import aima.core.probability.bayes.impl.*
//import aima.core.probability.domain.BooleanDomain
//import aima.core.probability.example.DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork
//import aima.core.probability.example.ExampleRV
//import aima.core.probability.proposition.AssignmentProposition
//import aima.core.probability.util.ProbabilityTable
//import aima.core.probability.util.RandVar
//import it.unito.bayesian.net.InferenceUsing
//import it.unito.bayesian.net.MainClass
//import org.encog.ml.bayesian.bif.BIFUtil
//import java.util.*
//import kotlin.collections.HashMap
//
//fun main(args: Array<String>){
//
//    val evidences = arrayOf(true, false, true, false)
//
//    var initialDBN = getUmbrellaWorldNetwork()
//
//    for(e in evidences){
//
//        val net = BIFUtil.readBIF(MainClass::class.java.getResource("win95pts.bif").path)
//
//        val newNodesMap = HashMap<RandomVariable, Node>()
//        val RVtoCPT = HashMap<RandomVariable, CategoricalDistribution>()
//        val x_0_to_X_1 = initialDBN.x_0_to_X_1
//        val allNewRV = initialDBN.x_1_VariablesInTopologicalOrder
//        var count =0
//        //for (rv in allNewRV){
//            val cpt = initialDBN.getNode(rv).cpd
//            val t = InferenceUsing.eliminationAsk.withMinWeightHeuristic.eliminationAsk(
//                    arrayOf(rv),
//                    emptyArray(),
//                    initialDBN
//            )
//
//            val newName = rv.name.substring(0, rv.name.length-1)+count
//            val newRandVar = RandVar(newName, rv.domain)
//            val newNode = FullCPTNode(newRandVar, t.values)
//            initialDBN.x_0_to_X_1[rv] = newRandVar
//            println("yolo")
////            println("cpt.toString() = $cpt")
////            println("cpt.values = ${Arrays.toString(cpt.values)}")
////            RVtoCPT[rv] = cpt
////            val test = initialDBN.getNode(rv).parents.toTypedArray()
////            newNodesMap[rv] = FullCPTNode(rv, cpt.values, *emptyArray())
////            val newDBN = DynamicBayesNet(initialDBN.priorNetwork, )
////            println("olè")
//        //}
//        val newBDN = DynamicBayesNet(initialDBN.priorNetwork, x_0_to_X_1, newNode, )
//    }
//}
//
//
