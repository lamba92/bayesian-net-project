import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.example.BayesNetExampleFactory
import aima.core.probability.example.ExampleRV
import aima.core.probability.proposition.AssignmentProposition
import java.util.*

object HeuristicElimination {

    @JvmStatic
    fun main(args: Array<String>) {

        val bn = BayesNetExampleFactory
                .constructBurglaryAlarmNetwork()

        val bayesInference = object : EliminationAsk() {

            override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>): List<RandomVariable> {

                val order = ArrayList(vars)
                order.sortWith(Comparator.comparingInt { o -> o.domain.size() })

                return super.order(bn, vars)
            }
        }

        val d1 = bayesInference.ask(
                arrayOf<RandomVariable>(ExampleRV.MARY_CALLS_RV),
                arrayOf(), bn)

        val d2 = bayesInference.ask(
                arrayOf<RandomVariable>(ExampleRV.MARY_CALLS_RV),
                arrayOf(AssignmentProposition(ExampleRV.MARY_CALLS_RV, true)), bn)

        println(d1.values)
        println(d2.values)
    }

}
