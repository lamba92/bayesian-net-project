package it.unito.bayesian.net

import aima.core.probability.domain.BooleanDomain
import aima.core.probability.example.DynamicBayesNetExampleFactory
import aima.core.probability.example.ExampleRV
import aima.core.probability.proposition.AssignmentProposition
import aima.core.probability.util.RandVar
import java.util.stream.Collectors


fun main(args: Array<String>) {
    test()
}


fun test() {

    val rainEvidences = arrayOf(false, true, true, false, false, true)
    val dbn = DynamicBayesNetExampleFactory.getUmbrellaWorldNetwork()

    var count = 0
    for (rainEvidence in rainEvidences) {
        // calcolo la variabile aleatoria che dovrò usare per la sostituzione
        val queryVariable = arrayOf(ExampleRV.RAIN_t_RV)
        val observedValues = arrayOf(AssignmentProposition(ExampleRV.UMBREALLA_t_RV, rainEvidence))
        InferenceUsing.eliminationAsk.withMinWeightHeuristic.ask(queryVariable, observedValues, dbn)

        /* variabile aleatoria di prova per vedere che funziona la sostituzione */
        val tempRandVar = RandVar("Rain_t_$count", BooleanDomain())

        if (count % 2 == 0) { // se devo sostituire t-1 //ipoteticamente posso avere più di una variabile aleatoria per nodo, farò un inserimento per ogni nome
            val names = dbn.x_0.stream().map<String?> { x -> x.name }.collect(Collectors.toList()) //ipoteticamente posso avere più di una variabile aleatoria per nodo, farò un inserimento per ogni nome
            dbn.x_0.clear()
            dbn.x_0.add(tempRandVar) //qui dovrei fare un inserimento per ogni nome in names
            println("""x_0 è ${dbn.x_0}""")
        } else {
            val names = dbn.x_1.stream().map<String?> { x -> x.name }.collect(Collectors.toList())
            dbn.x_1.clear()
            dbn.x_1.add(tempRandVar)
            println("""x_1 è ${dbn.x_1}""")
        }
        count += 1
    }

}
