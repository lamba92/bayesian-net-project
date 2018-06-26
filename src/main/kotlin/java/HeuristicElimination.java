package java;

import aima.core.probability.CategoricalDistribution;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.exact.EliminationAsk;
import aima.core.probability.example.BayesNetExampleFactory;
import aima.core.probability.example.ExampleRV;
import aima.core.probability.proposition.AssignmentProposition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class HeuristicElimination {

    public static void main(String[] args) {

        BayesianNetwork bn = BayesNetExampleFactory
                .constructBurglaryAlarmNetwork();

        EliminationAsk bayesInference = new EliminationAsk() {

            @Override
            protected List<RandomVariable> order(BayesianNetwork bn, Collection<RandomVariable> vars) {

                List<RandomVariable> order = new ArrayList<>(vars);
                order.sort(Comparator.comparingInt(o -> o.getDomain().size()));

                return super.order(bn, vars);
            }
        };

        CategoricalDistribution d_1 = bayesInference.ask(
                new RandomVariable[]{ExampleRV.MARY_CALLS_RV},
                new AssignmentProposition[]{}, bn);

        CategoricalDistribution d_2 = bayesInference.ask(
                new RandomVariable[]{ExampleRV.MARY_CALLS_RV},
                new AssignmentProposition[]{
                        new AssignmentProposition(ExampleRV.MARY_CALLS_RV, true)
                }, bn);

        System.out.println(d_1.getValues());
        System.out.println(d_2.getValues());
    }

}
