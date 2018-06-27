import aima.core.probability.RandomVariable
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.example.DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork
import aima.core.probability.example.ExampleRV

fun main(args: Array<String>){
//    val bn = constructBurglaryAlarmNetwork()
//    getUmbrellaWorldNetwork().x_0_to_X_1.forEach { k, v ->
//        println(k.toString())
//        println(v.toString())
//    }
//    val d1 = InferenceUsing.eliminationAsk.withMinWeightHeuristic.ask(
//            arrayOf<RandomVariable>(ExampleRV.BURGLARY_RV),
//            arrayOf(AssignmentProposition(ExampleRV.MARY_CALLS_RV, true)), bn)
//    println(d1.toString())
    val dbn = getUmbrellaWorldNetwork() as DynamicBayesNet

    val rollup = ArrayList<DynamicBayesNet>().apply {
        add(getUmbrellaWorldNetwork() as DynamicBayesNet)
    }

    ArrayList<Boolean>().apply {
        for(i in 2 until 10 step 1){
            add(i%2 != 0)
        }
    }.forEach {
        val currentDBN = rollup[rollup.size - 1]
        lateinit var newRoot: Node
        currentDBN.x_0.forEach {
            if(currentDBN.getNode(it).parents?.isEmpty() == true) newRoot = currentDBN.getNode(it)
        }
        rollup.add(DynamicBayesNet(
                currentDBN.priorNetwork,
                currentDBN.x_0_to_X_1,
                HashSet<RandomVariable>().apply {add(ExampleRV.UMBREALLA_t_RV) },
                newRoot))
    }
}
