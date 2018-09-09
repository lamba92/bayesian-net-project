[![Build Status](https://travis-ci.org/lamba92/bayesian-net-project.svg?branch=master)](https://travis-ci.org/lamba92/bayesian-net-project) [![](https://jitpack.io/v/Lamba92/bayesian-net-project.svg)](https://jitpack.io/#Lamba92/bayesian-net-project)

# Bayesian Networks Project

The project consists in two implementations:

   **1.** **Extended Variable Elimination** inference algorithm on `Bayesian Networks`,
 
   **2.** **Rollup Filtering**  inference algorithm on `Dynamic Bayesian Networks` (DBNs).
      
# Preliminaries

## Bayesian Networks

A Bayesian Network is a **Directed Acyclic Graph** (**DAG**) that is an efficient and compact representation for a set of conditional independence assumptions about distributions. The directed graph tries to represent the Random Variables as nodes in a graph.
These **nodes** represent the Random Variables and the **edges** represent the direct influence of one variable of one another.

In general each Random Variable is associated with a **Conditional Probability Table**, also called as a **CPT**, that specifies the distribution over the values of the random variable associated with its parents.
The CPT encodes the distribution of the variables and help in precisely determining the output of the variable.

A common task in a Bayesian Network is to **"summing out" the probability of a random variable** A given the joint probability distribution of A with other variables, this task is called **marginalization** of the variable A. It is possible to compute it with the formula:

<p align="center">
  <img src="http://latex.codecogs.com/gif.latex?P%28A%29%20%3D%20%5Csum_eP%28A%7Ce%29P%28e%29"/>
</p>

You can compute the full joint distribution of a Bayesian Network with the following formula:

<p align="center">
  <img src="http://latex.codecogs.com/gif.latex?P%28V%29%20%3D%20%5Cprod_%7Bi%3D1%7D%5En%20P%28V_i%20%7C%20Parents%28V_i%29%29"/>
</p>

The following image represent a Bayesian Network:

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/net.png"  width="50%" height="50%"/>
</p>

### Variable Elimination algorithm

`Variable Elimination` (VE) is a simple and general exact inference algorithm in probabilistic graphical models, such as Bayesian Networks. It can be used for estimation of conditional or marginal distributions over a subset of variables and for the inference of the `Maximum A posteriori Probability` (MAP) state. 

The algorithm has an exponential time complexity, but could be efficient in practice for the low-width tree graphs, if the proper elimination order is used (which is a NP-hard problem).  
To find a variable elimination order we use heuristics.

### Heuristics

The data structure used to find a variable elimination order is called **Moral Graph**.
In graph theory, a Moral Graph is used to find the equivalent undirected form of a directed acyclic graph. 

The moralized counterpart of a Directed Acyclic Graph is formed by adding edges between all pairs of nodes that have a common child, and then making all edges in the graph undirected.

We define an elimination order through the evaluation function, which uses one of the following heuristics as evaluation metrics:

 - **Min-neighbors**: The cost of a vertex is the number of neighbors it has in the current graph.
 
 - **Min-weight**: The cost of a vertex is the product of weights — domain cardinality — of its neighbors.
 
 - **Min-fill**: The cost of a vertex is the number of edges that need to be added to the graph due to its elimination.
 
 - **Weighted-min-ﬁll**: The cost of a vertex is the sum of weights of the edges that need to be added to the graph due to its elimination, where a weight of an edge is the product of weights of its constituent vertices.
 
It is shown that *none of these heuristics is better than another* because their goodness is strictly dependent on the topology of the network on which the algorithm itself is applied.

The one in the following image is the search greedy algorithm for the heuristic sorting of the variables to be eliminated:

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/greedy.PNG"/>
</p>

## Dynamic Bayesian Networks

`Dynamic Bayesian Networks` (**DBN**'s) are static Bayesian networks that are modeled over an arrangement of **time series** or sequences. 
In a Dynamic Bayesian Network, each **time slice** is conditionally dependent on the previous one. The probabilities among the original distribution determine the probabilities in the successives.

Per costruire una DBN occorre:

**1.** <img align="center" src="http://latex.codecogs.com/gif.latex?P(X_0)&space;\rightarrow&space;Prior&space;\&space;distribution&space;\&space;on&space;\&space;state&space;\&space;variables" title="P(X_0) \rightarrow Prior \ distribution \ on \ state \ variables" />

**2.** <img align="center" src="http://latex.codecogs.com/gif.latex?P%28X_%7Bt&plus;1%7D%20%7C%20X_t%29%20%5C%3A%5C%3A%20%5Crightarrow%20%5C%3A%20Transition%20%5C%3A%20Model%20%5C%5C%5C%5C"/>

**3.** <img align="center" src="http://latex.codecogs.com/gif.latex?P%28E_t%20%7C%20X_t%29%20%5C%3A%5C%3A%20%5Crightarrow%20%5C%3A%20Sensor%20%5C%3A%20Model"/>

The following image represents a Dynamic Bayesian Network:

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/rain.PNG"  width="50%" height="50%"/>
</p>


### Rollup Filtering algorithm

It is possible to construct a Dynamic Network with an unlimited number of intervals starting from the previously specified parameters, by copying the first interval; this operation is called **Unrolling**.

However it suffers from an **excessive memory occupation**, since all time slices (which tend to infinity) are kept in memory.

We use the **Rollup Filtering** technique to solve this problem, which makes possible to focus on two slices at a time; the following interval will be created using the VE algorithm on the last product interval.

To carry out the inference you can also provide a sequence of observations.


# Project Description
   
## 1. VE (Variable Elimination) algorithm extension

The exercise is divided into four steps:
- Remove the irrelevant variables,
- Implement variable order heuristics,
- Allow inferences `MPE` (Most Probable Explanation) and `MAP` (Maximum a Posteriori Probability)
- Analysis of empirical results.

## 2. Rollup Filtering algorithm implementation

The exercise is divided into three steps:
- Arrange the `Variable Elimination` algorithm for Bayesian Networks and modify it so it can be performed after the execution of a `Rollup Filtering`, with the focus on two slices at a time,
- Provide a sequence of evidences, which can be more than one for each time slice,
- Analysis of empirical results.

# Project management

## Development

The project has been divided into three main parts:
- `utils` folder, which contains:

  - `MoralGraph.kt`, which implements a Moral Graph and all the necessary methods needed,

  - `BIFToBayesNet.kt`, which implements a BIF file parser that instatiate a BayesNet object from a BIF file (Boolean Domain only),
  
  - `Utils.kt` and `WrongDistributionException` files.
  
- `CustomDynamicaBayesNet.kt`, which implements the `Rollup Filtering` algorithm focus on two slices at a time. To advance the network to the following state *t+1*, where *t* is the current time state, the `forward` method must be executed.

- `Inferences.kt`, which instantiates an object which contains the extension of the `Variable Elimination` algorithm required by the project and which exposes the methods

  - `order`, which starting from a `BayesianNetwork` returns an appropriate order with which to apply the `Variable Elimination` algorithm,
  
  - `calculateVariables` that given the query variables, the evidences and the Bayesian Network, removes from the latter the **irrelevant variables**: every variable that is not an ancestor of a query variable or evidence variable.

## Resources

There is also a `resource` folder, which contains all the BIF format hard-coded nets.

## Graph Representation

We also used [GraphStream](https://github.com/graphstream/gs-core) to visualize and debug the Variable Elimination algorithm.

Here is an example of a graph before and after undergoing the pruning of a node.

<p align="center"> 
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/Immagine2a.png" width="20%" height="20%"/>
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/Immagine1.png" width="40%" height="40%" />
</p>


# Getting Started

An easy to use library that allows to build and evolve a Dynamic Bayesian Network which random variables have a boolean domain. It has been built using the library [aima-java](https://github.com/aimacode/aima-java). 

## Installing

Add the [JitPack.io](http://jitpack.io) repository to the project `build.grade`:
```
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then import the latest version in the `build.gradle` of the modules you need:

```
dependencies {
    implementation 'com.github.Lamba92:bayesian-net-project:{latest_version}'
    implementation 'com.googlecode.aima-java:aima-core:3.0.0'
}
```

If using Gradle Kotlin DSL:
```
repositories {
    maven(url = "https://jitpack.io")
}
...
dependencies {
    implementation("com.github.Lamba92", "bayesian-net-project", "{latest_version}")
    implementation("com.googlecode.aima-java", "aima-core", "3.0.0")
}
```
If you are using Maven, switch to Gradle, it's 2018.

## Usage

### Static Bayes Network

Create a `???` using a newly generated dynamic network or use an example from the factories of this library and aima's:

### Dynamic Bayes Network

Create a `CustomDynamicBayesianNet` using a newly generated dynamic network or use an example from the factories of this library and aima's:
```
import it.unito.bayesian.net.CustomDynamicBayesianNet
import it.unito.bayesian.net.Inferences.*

...

val inference = getCustomEliminationAsk(minWeightHeuristicFunction())
val customNet = getComplexDynamicNetworkExample()

customNet.forward() // Moves the network one step forward
```

`CustomDynamicBayesianNet` constructor needs a network and a `BayesianInference` to proceed forward in time.

Check out the [KDocs](https://jitpack.io/com/github/lamba92/bayesian-net-project/0.6/javadoc/bayesian-net-project/) for details. 

For a usage example have a look [at some tests here](https://github.com/lamba92/bayesian-net-project/tree/master/src/main/kotlin/it/unito/bayesian/net/main).

## Authors

* **Cesare Pio Iurlaro** - [CesareIurlaro](https://github.com/CesareIurlaro)
* **Giuseppe Gabbia**  - [beppe95](https://github.com/beppe95)
* **Lamberto Basti**  - [lamba92](https://github.com/lamba92)
