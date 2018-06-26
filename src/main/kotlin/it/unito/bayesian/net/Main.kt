import aima.core.probability.CategoricalDistribution
import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.exact.EliminationAsk
import aima.core.probability.example.BayesNetExampleFactory
import aima.core.probability.example.ExampleRV
import aima.core.probability.proposition.AssignmentProposition
import java.util.*
import java.util.Comparator.comparingInt
import kotlin.collections.ArrayList

fun main(args: Array<String>) {

    val bn = BayesNetExampleFactory
            .constructBurglaryAlarmNetwork()

    val variableElimination = object : EliminationAsk() {
        override fun order(bn: BayesianNetwork?, vars: Collection<RandomVariable>)
                = super.order(bn, ArrayList(vars).apply {
            sortWith(comparingInt { -it.domain.size() })
        })
    }

    val d2: CategoricalDistribution = variableElimination.ask(
            arrayOf<RandomVariable>(ExampleRV.BURGLARY_RV),
            arrayOf(AssignmentProposition(ExampleRV.MARY_CALLS_RV, true)), bn)

//    println(d1.values)
    println(d2.toString())
}
