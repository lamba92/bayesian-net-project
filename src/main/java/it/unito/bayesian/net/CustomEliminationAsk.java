package it.unito.bayesian.net;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.stream.Stream;

import aima.core.probability.CategoricalDistribution;
import aima.core.probability.Factor;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.*;
import aima.core.probability.bayes.impl.CPT;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.ProbabilityTable;
import javafx.util.Pair;

import static it.unito.bayesian.net.utils.UtilsKt.generateVectorFromCPT;
import static it.unito.bayesian.net.utils.UtilsKt.mapCaster;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): Figure 14.11, page
 * 528.<br>
 * <br>
 *
 * <pre>
 * function ELIMINATION-ASK(X, e, bn) returns a distribution over X
 *   inputs: X, the query variable
 *           e, observed values for variables E
 *           bn, a Bayesian network specifying joint distribution P(X<sub>1</sub>, ..., X<sub>n</sub>)
 *
 *   factors <- []
 *   for each var in ORDER(bn.VARS) do
 *       factors <- [MAKE-FACTOR(var, e) | factors]
 *       if var is hidden variable the factors <- SUM-OUT(var, factors)
 *   return NORMALIZE(POINTWISE-PRODUCT(factors))
 * </pre>
 *
 * Figure 14.11 The variable elimination algorithm for inference in Bayesian
 * networks. <br>
 * <br>
 * <b>Note:</b> The implementation has been extended to handle queries with
 * multiple variables. <br>
 *
 * @author Ciaran O'Reilly
 */
public class CustomEliminationAsk implements BayesInference {
    //
    private static final ProbabilityTable _identity = new ProbabilityTable(
            new double[] { 1.0 });

    public static final int MPE = 1;
    public static final int STANDARD = 0;

    private int inferenceMethod;

    public CustomEliminationAsk(int inferenceMethod) {
        if(inferenceMethod != MPE && inferenceMethod != STANDARD){
            this.inferenceMethod = STANDARD;
        } else {
            this.inferenceMethod = inferenceMethod;
        }
    }

    public CustomEliminationAsk(){
        this.inferenceMethod = STANDARD;
    }

    // function ELIMINATION-ASK(X, e, bn) returns a distribution over X
    /**
     * The ELIMINATION-ASK algorithm in Figure 14.11.
     *
     * @param X
     *            the query variables.
     * @param e
     *            observed values for variables E.
     * @param bn
     *            a Bayes net with variables {X} &cup; E &cup; Y /* Y = hidden
     *            variables //
     * @return a distribution over the query variables.
     */
    private CategoricalDistribution mpeEliminationAsk(final RandomVariable[] X,
                                                      final AssignmentProposition[] e, final BayesianNetwork bn) {

        Set<RandomVariable> hidden = new HashSet<>();
        List<RandomVariable> VARS = new ArrayList<>();
        calculateVariables(X, e, bn, hidden, VARS);

        // factors <- []
        List<Factor> factors = new ArrayList<>();
        for (RandomVariable rv : VARS) {
            // factors <- [MAKE-FACTOR(rv, e) | factors]
            factors.add(0, makeFactor(rv, e, bn));
        }

        executeMaxOut(factors, bn, e, VARS);

        return null;
        // return ((ProbabilityTable) .... ).normalize();
    }

    private CategoricalDistribution standardEliminationAsk(final RandomVariable[] X,
                                                  final AssignmentProposition[] e, final BayesianNetwork bn) {

        Set<RandomVariable> hidden = new HashSet<>();
        List<RandomVariable> VARS = new ArrayList<>();
        calculateVariables(X, e, bn, hidden, VARS);

        // factors <- []
        List<Factor> factors = new ArrayList<>();
        for (RandomVariable rv : VARS) {
            // factors <- [MAKE-FACTOR(rv, e) | factors]
            factors.add(0, makeFactor(rv, e, bn));
        }

        return executeSumOut(X, hidden, VARS, factors, bn, e);
    }

    private CategoricalDistribution executeMaxOut(List<Factor> factors, BayesianNetwork bn, AssignmentProposition[] e, List<RandomVariable> VARS) {
        //ArrayList<RandomVariable> assignments = new ArrayList<>();
        //Arrays.stream(e).forEach(assignmentProposition -> assignments.add(assignmentProposition.getTermVariable()));

        List<RandomVariable> assignments = new ArrayList<>();
        Arrays.stream(e).forEach(assignmentProposition -> assignments.add(assignmentProposition.getTermVariable()));

        System.out.println("I fattori del grafo sono: " + factors);
        for (RandomVariable rv : order(bn, VARS)) {
            if(assignments.contains(rv)){
                double[] values = ((CPT) bn.getNode(rv).getCPD()).getFactorFor().getValues();
                System.out.println("\n PROVA: \n" + ((CPT) bn.getNode(rv).getCPD()).getFactorFor());
                Pair<Map, Double> maxedOut = maxOut(rv, values, bn, e);
            } else {
                Factor rvFactor = (((CPT) bn.getNode(rv).getCPD())).getFactorFor();
                ArrayList<Factor> childrenFactors = new ArrayList<>();
                bn.getNode(rv).getChildren().forEach(node ->
                        childrenFactors.add(((CPT) node.getCPD()).getFactorFor()));
                childrenFactors.add(0, rvFactor);
                Factor product = pointwiseProduct(childrenFactors); // ho fatto il prodotto dei figli, ora devo fare max
                System.out.println("Product " + product);
                Pair<Map, Double> maxedOut = maxOut(rv, product.getValues(), bn, e);

                //System.out.println("\n " + maxedOut.getValue());
            }
        }

        /*List<Factor> evidencesFactors = new ArrayList<>();
        for(AssignmentProposition ap : e) {
            //mpeEvidencesResult *= Arrays.stream(((CPT) bn.getNode(ap.getTermVariable()).getCPD()).getFactorFor().getValues()).max().getAsDouble();
            Factor factor = ((CPT) bn.getNode(ap.getTermVariable()).getCPD()).getFactorFor();
            evidencesFactors.add(factor);
        }

        List<Factor> rvFactors = new ArrayList<>();
        for (RandomVariable rv : noEvidences) {
            System.out.println("\nNode " + rv.getName() + ":");
            if (!assignments.contains(rv)) {
                CPT maxedOut = maxOut(rv, factors, bn, e);
                Factor f = maxedOut.getFactorFor();
                rvFactors.add(f);
                System.out.println(f);
            }
        }

        Factor mpeEvidencesResults = pointwiseProduct(evidencesFactors);
        Factor mpeRvResults = pointwiseProduct(factors);*/


        return null;
    }

    private CategoricalDistribution executeSumOut(final RandomVariable[] X, Set<RandomVariable> hidden,  List<RandomVariable> VARS, List<Factor> factors, final BayesianNetwork bn, AssignmentProposition[] e) {
        System.out.println("I fattori del grafo sono: " + factors);
        // for each var in ORDER(bn.VARS) do
        for (RandomVariable var : order(bn, VARS)) {
            // if var is hidden variable then factors <- SUM-OUT(var, factors)
            if (hidden.contains(var)) {
                factors = sumOut(var, factors, bn);
            }
        }
        // return NORMALIZE(POINTWISE-PRODUCT(factors))
        Factor product = pointwiseProduct(factors);
        return ((ProbabilityTable) product.pointwiseProductPOS(_identity, X)).normalize();

    }

    //
    // START-BayesInference
    public CategoricalDistribution ask(final RandomVariable[] X,
                                       final AssignmentProposition[] observedEvidence,
                                       final BayesianNetwork bn) {
        switch (inferenceMethod){
            case STANDARD:
                return standardEliminationAsk(X, observedEvidence, bn);
            case MPE:
                return mpeEliminationAsk(X, observedEvidence, bn);
            default:
                return standardEliminationAsk(X, observedEvidence, bn);
        }
    }

    // END-BayesInference
    //

    //
    // PROTECTED METHODS
    //
    /**
     * <b>Note:</b>Override this method for a more efficient implementation as
     * outlined in AIMA3e pgs. 527-28. Calculate the hidden variables from the
     * Bayesian Network. The default implementation does not perform any of
     * these.<br>
     * <br>
     * Two calcuations to be performed here in order to optimize iteration over
     * the Bayesian Network:<br>
     * 1. Calculate the hidden variables to be enumerated over. An optimization
     * (AIMA3e pg. 528) is to remove 'every variable that is not an ancestor of
     * a query variable or evidence variable as it is irrelevant to the query'
     * (i.e. sums to 1). 2. The subset of variables from the Bayesian Network to
     * be retained after irrelevant hidden variables have been removed.
     *
     * @param X
     *            the query variables.
     * @param e
     *            observed values for variables E.
     * @param bn
     *            a Bayes net with variables {X} &cup; E &cup; Y /* Y = hidden
     *            variables //
     * @param hidden
     *            to be populated with the relevant hidden variables Y.
     * @param bnVARS
     *            to be populated with the subset of the random variables
     *            comprising the Bayesian Network with any irrelevant hidden
     *            variables removed.
     */
    protected void calculateVariables(final RandomVariable[] X,
                                      final AssignmentProposition[] e, final BayesianNetwork bn,
                                      Set<RandomVariable> hidden, Collection<RandomVariable> bnVARS) {

        bnVARS.addAll(bn.getVariablesInTopologicalOrder());
        hidden.addAll(bnVARS);

        for (RandomVariable x : X) {
            hidden.remove(x);
        }
        for (AssignmentProposition ap : e) {
            hidden.removeAll(ap.getScope());
        }

        return;
    }

    /**
     * <b>Note:</b>Override this method for a more efficient implementation as
     * outlined in AIMA3e pgs. 527-28. The default implementation does not
     * perform any of these.<br>
     *
     * @param bn
     *            the Bayesian Network over which the query is being made. Note,
     *            is necessary to provide this in order to be able to determine
     *            the dependencies between variables.
     * @param vars
     *            a subset of the RandomVariables making up the Bayesian
     *            Network, with any irrelevant hidden variables alreay removed.
     * @return a possibly opimal ordering for the random variables to be
     *         iterated over by the algorithm. For example, one fairly effective
     *         ordering is a greedy one: eliminate whichever variable minimizes
     *         the size of the next factor to be constructed.
     */
        protected List<RandomVariable> order(BayesianNetwork bn,
                                         Collection<RandomVariable> vars) {
        // Note: Trivial Approach:
        // For simplicity just return in the reverse order received,
        // i.e. received will be the default topological order for
        // the Bayesian Network and we want to ensure the network
        // is iterated from bottom up to ensure when hidden variables
        // are come across all the factors dependent on them have
        // been seen so far.
        List<RandomVariable> order = new ArrayList<RandomVariable>(vars);
        Collections.reverse(order);

        return order;
    }

    //
    // PRIVATE METHODS
    //
    private Factor makeFactor(RandomVariable var, AssignmentProposition[] e,
                              BayesianNetwork bn) {

        Node n = bn.getNode(var);
        if (!(n instanceof FiniteNode)) {
            throw new IllegalArgumentException(
                    "Elimination-Ask only works with finite Nodes.");
        }
        FiniteNode fn = (FiniteNode) n;
        List<AssignmentProposition> evidence = new ArrayList<>();
        for (AssignmentProposition ap : e) {
            if (fn.getCPT().contains(ap.getTermVariable())) {
                evidence.add(ap);
            }
        }

        return fn.getCPT().getFactorFor(
                evidence.toArray(new AssignmentProposition[0]));
    }

    private List<Factor> sumOut(RandomVariable var, List<Factor> factors, BayesianNetwork bn) {
        List<Factor> summedOutFactors = new ArrayList<>();
        List<Factor> toMultiply = new ArrayList<>();
        for (Factor f : factors) {
            if (f.contains(var)) {
                toMultiply.add(f);
            } else {
                // This factor does not contain the variable
                // so no need to sum out - see AIMA3e pg. 527.
                summedOutFactors.add(f);
            }
        }

        summedOutFactors.add(pointwiseProduct(toMultiply).sumOut(var));
        return summedOutFactors;
    }

    private Pair<Map, Double> maxOut(RandomVariable var, double[] values, BayesianNetwork bn, AssignmentProposition[] assignmentPropositions) {
        //Double[] arr = generateVectorFromCPT((CPT)bn.getNode(var).getCPD(),false);
        // double[] unboxedArr = Stream.of(arr).mapToDouble(Double::doubleValue).toArray();
        List<RandomVariable> list = new ArrayList<>();
        ArrayList<Pair<Map, Double>> termValues = new ArrayList<>();
        final Pair<Map, Double>[] max = new Pair[]{new Pair<>(new HashMap(), 0.0)};
        Set<Node> parents = bn.getNode(var).getParents();
        double[] unboxed = getDoubles(values);

        list.add(var);
        parents.forEach(p -> list.add(p.getRandomVariable()));

        if(!list.isEmpty()) {
            ArrayList<RandomVariable> rv_children = new ArrayList<>();
            bn.getNode(var).getChildren().forEach(node -> rv_children.add(node.getRandomVariable()));
            System.out.println("\n PADRE: " + var + "\n FIGLI: \n" + rv_children);
            calculateComplementary(unboxed);
            Arrays.stream(unboxed).forEach(value -> System.out.print(value + " "));

            Factor currentRandVarPT = new CPT(var, unboxed, rv_children.toArray(new RandomVariable[0])).getFactorFor();

            Set<AssignmentProposition> parentsPropositions = new HashSet<>();

            Factor.Iterator iterator = (possibleAssignment, probability) -> {

                // System.out.println(possibleAssignment.entrySet() + " " + probability);

                boolean flag = true;
                for (AssignmentProposition ass: assignmentPropositions) {
                    boolean check1 = possibleAssignment.get(ass.getTermVariable()) != null;
                    if (check1 && !possibleAssignment.get(ass.getTermVariable()).equals(ass.getValue())) {
                        flag = false;
                    }
                }
                if(flag) {
                    termValues.add(new Pair(mapCaster(possibleAssignment), probability));
                }
            };


            AssignmentProposition[] propositionsArray = parentsPropositions.toArray(new AssignmentProposition[0]);
            //currentRandVarPT.iterateOver(iterator, propositionsArray);
            currentRandVarPT.iterateOver(iterator, propositionsArray);
            System.out.println("\n termValues: \n" + termValues);
            termValues.forEach(
                mapDoublePair -> {
                    if (mapDoublePair.getValue() > max[0].getValue())
                        max[0] = mapDoublePair;
                }
            );
        }

        /* ArrayList<RandomVariable> rvList = new ArrayList<>();
        bn.getNode(var).getParents().forEach(node -> rvList.add(node.getRandomVariable()));
        RandomVariable[] rvArr = rvList.toArray(new RandomVariable[0]);

        CPT cpt = new CPT(var, unboxed, rvArr);
        Arrays.stream(cpt.getFactorFor().getValues()).forEach(System.out::println); */

        return max[0];
    }

    private void calculateComplementary(double[] unboxed) {
        for (int i=0; i<unboxed.length; i++) {
            if (i%2!=0) {
                unboxed[i] = 1 - unboxed[i-1];
            }
        }
    }

    private double[] getDoubles(double[] arr) {
        List<Double> pair = new ArrayList<>();
        List<Double> odd = new ArrayList<>();

        for (int i=0; i<arr.length; i++) {
            if (i%2==0) {
                pair.add(arr[i]);
            } else {
                odd.add(arr[i]);
            }
        }

        List<Double> tmp = new ArrayList<>();
        tmp.addAll(pair);
        tmp.addAll(odd);

        Double[] stockArr = tmp.toArray(new Double[0]);
        return Stream.of(stockArr).mapToDouble(Double::doubleValue).toArray();
    }

    private Factor pointwiseProduct(List<Factor> factors) {

        Factor product = factors.get(0);
        for (int i = 1; i < factors.size(); i++) {
            product = product.pointwiseProduct(factors.get(i));
        }

        return product;
    }
}