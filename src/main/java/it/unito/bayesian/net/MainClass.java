package it.unito.bayesian.net;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.bayes.ConditionalProbabilityTable;
import aima.core.probability.bayes.FiniteNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.AbstractNode;
import aima.core.probability.bayes.impl.CPT;

import java.util.ArrayList;
import java.util.Arrays;

public class MainClass {
    public static void main(String[] args) {

    }
}


//public class CustomNode extends AbstractNode implements FiniteNode{
//
//    private final CPT distribution;
//
//    public CustomNode(RandomVariable rv, CPT distribution, Node...nodes){
//        super(rv, nodes);
//        this.distribution = distribution;
//    }
//
//    public CustomNode(RandomVariable rv, double[] distribution, Node...nodes){
//        super(rv, nodes);
//        ArrayList<RandomVariable> parents = new ArrayList<RandomVariable>();
//        for(Node node: nodes){
//            parents.add(node.getRandomVariable());
//        }
//        RandomVariable[] arrayRV = new RandomVariable[parents.size()];
//        parents.toArray(arrayRV);
//        this.distribution = new CPT(rv, distribution, arrayRV);
//    }
//
//    @Override
//    public ConditionalProbabilityTable getCPT() {
//        return distribution;
//    }
//
//    @Override
//    public ConditionalProbabilityDistribution getCPD() {
//        return getCPT();
//    }
//}
