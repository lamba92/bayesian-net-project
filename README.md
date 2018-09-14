[![Build Status](https://travis-ci.org/lamba92/bayesian-net-project.svg?branch=master)](https://travis-ci.org/lamba92/bayesian-net-project) [![](https://jitpack.io/v/Lamba92/bayesian-net-project.svg)](https://jitpack.io/#Lamba92/bayesian-net-project)

# Bayesian Networks Project

The project consists in two implementations:

   **1.** **Extended Variable Elimination** inference algorithm on `Bayesian Networks`,

   **2.** **Rollup Filtering**  inference algorithm on `Dynamic Bayesian Networks` (DBNs).

# Preliminaries

## Bayesian Networks

### Introduction

A Bayesian Network - short **BN** - is a **Directed Acyclic Graph** (**DAG**), an efficient and compact representation for a set of conditional dependencies assumptions about distributions. Its **nodes** represent the Random Variables, while **edges** represent dependencies between RVs.

A BN allows to **inference** the distribution of a given RV after observing another RV of the network. More specifically, assigning a value to a specified RV we can deduce how the rest of the network changes.

This is a **BN** example:

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/net.png"  width="50%" height="50%"/>
</p>

For the sake of brevity, this project is able to manipulate only Bernoulli distributions but all the concepts can be extended to other distributions, discrete or not.

### Inference on a Bayesian Network

Let's say we need to compute how the distribution of **A** changes as we observe that **E** has been set to "**e**". With the conditional probability formulae we can compute:

<p align="center">
  <img src="http://latex.codecogs.com/gif.latex?P%28A%29%20%3D%20%5Csum_eP%28A%7Ce%29P%28e%29"/>
</p>


The key point of this process is to be able to calculate distributions of RVs representing phenomenons whose are not observable directly while their possible consequences are. This is possible thanks to the Bayes Formulae which states how parent RVs changes fixing the value of his dependent variables:

<p align="center">
  <img src="https://wikimedia.org/api/rest_v1/media/math/render/svg/e7073f53d5a809262243e612be97d172108b4cd4"/>
</p>

This process is called **inference** and allows to ask the distribution of an unobservable RVs given one or more observations.

## Inference techniques

Making inferences on a BN using recurring Bayesian Formulae can be a challenging task even for advanced server clusters. Being efficient while inferencing is consequently a key point. Furthermore, inferences can exact or approximated. Approximations allow to greatly reduce the computational power needed at the cost of precision.

### Variable Elimination

`Variable Elimination` (VE) is a simple and general exact inference algorithm. It consists on identifying groups of repeated calculations, which are called **factors**, store them and then use the stored value instead of computing them again.

Those factors can be compacted using point-wise multiplication following a custom order; this very order allows to increase the efficiency of the algorithm.

**VE** has an exponential time complexity, but can be efficient in practice for low tree-width graphs, if the proper elimination order is found (which is a NP-hard problem).  

Heuristics may be used to find a **Variable Elimination** order:

#### Heuristics

The data structure used to find a variable elimination order is called **Moral Graph**.
In graph theory, a Moral Graph is used to find the equivalent undirected form of a Directed Acyclic Graph.

The moralized counterpart of a Directed Acyclic Graph is formed by adding edges between all pairs of nodes that have a common child, and then making all edges in the graph undirected.

We define an elimination order through the evaluation function, which uses one of the following heuristics as evaluation metrics:

 - **minimum neighbors**: The cost of a vertex is the number of neighbors it has in the current graph.

 - **minimum weight**: The cost of a vertex is the product of weights — domain cardinality — of its neighbors.

 - **minimum fill**: The cost of a vertex is the number of edges that need to be added to the graph due to its elimination.

 - **weighted minimum fill**: The cost of a vertex is the sum of weights of the edges that need to be added to the graph due to its elimination, where a weight of an edge is the product of weights of its constituent vertices.

It is shown that *none of these heuristics is better than another* because their goodness is strictly dependent on the topology of the network on which the algorithm itself is applied.

The one in the following image is the search greedy algorithm for the heuristic sorting of the Variables to be eliminated:

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/greedy.PNG"/>
</p>

### MPE

Given an evidence *e*, the **MPE**, also known as *max propagation*, is the assignment to all the non-evidence variables that has the highest probability. It is done finding a q instantiation of Q such that `P(q|e)` is maximal, which is the same as maximizing `P(q,e)`, since
<p align="center">
  <img src="http://latex.codecogs.com/gif.latex?P%28q%7Ce%29%3D%20%5Cfrac%7BP%28q%2C%20e%29%7D%7BP%28e%29%7D"/>
</p>

The **MPE** inference is similar to the Variable Elimination one, the difference is that when variables are marginalized out from distributions in order to compute queries, instead of summing values like in the Variable Elimination, the **maximum is used in the MPE**.

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/MPE-algorithm.png"/>
</p>

Given Q set of variables in a **BN**, q their instantiations and E set of evidences, then the maximum is computed, formally: 

<p align="center">
  <img src="http://latex.codecogs.com/gif.latex?%5Cbold%7Barg%7D%20%5C%3A%20%5Cunderset%7BQ%7D%7Bmax%7D%20%5C%3A%20P%28q%7Ce_%7B1%7D%2C%20%5C%3A%20e_%7B2%7D%2C%20%5C%3A%20...%5C%3A%20%2C%20e_%7Bn%7D%29%20%5C%3A%2C%20%5C%3A%20%5C%3A%20%5C%3A%20%5C%3A%20e_%7Bi%7D%20%5Cin%20E"/>
</p>

#### Example
In our code we made a representation of the following **BN**, which models the behavior of a digital circuit:
<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/BN_Circuit.png" width="50%" height="50%"/>
</p>

Given the evidences `J=true` and `O=false` and the order `J, I, X, Y, O` according to *min–neighbors heuristic*, the following computation is made:

<p align="center">
  <img src="http://latex.codecogs.com/gif.latex?%5Cunderset%7BJ%2CI%2CX%2CY%2CO%7D%7Bmax%7D%20%5Cbold%7Bf%7D_%7BI%20%5C%3A%7D%20%5Cbold%7Bf%7D_%7BJ%20%5C%3A%7D%20%5Cbold%7Bf%7D_%7BY%20%5C%3A%7D%20%5Cbold%7Bf%7D_%7BX%7CI%20%5C%3A%7D%20%5Cbold%7Bf%7D_%7BO%7CXY%7D%20%3D%20%28%5Cunderset%7BJ%7D%7Bmax%7D%5Cbold%7B%5C%3Af%7D_%7BJ%7D%29%20%5Cunderset%7BO%7D%7B%5C%3Amax%7D%20%28%5Cunderset%7BY%7D%7Bmax%7D%20%28%5Cunderset%7BX%7D%7Bmax%7D%20%28%5Cunderset%7BI%7D%7Bmax%7D%5Cbold%7B%5C%3Af%7D_%7BI%7D%5Cbold%7B%5C%3Af%7D_%7BX%7CI%7D%29%5Cbold%7B%5C%3Af%7D_%7BO%7CXY%7D%29%5Cbold%7B%5C%3Af%7D_%7BY%7D%29"/>
</p>


### MAP

A Maximum a Posteriori Probability (**MAP**) is an estimate of an unobserved quantity on the basis of empirical data.

Computing **MAP** for a set of variables Q and a set of evidence E means to find an instantiation q of variables Q which maximizes the probability `P(q|e)`. It is done using a **Variable Elimination and MPE combination**.

The algorithm is the following:
<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/MAP-algorithm.png" width="80%" height="80%"/>
</p>

Which computes a factored representation of the joint marginal `P(Q,e)` and then find an MPE for Q using the resulting marginal.


## Dynamic Bayesian Networks

`Dynamic Bayesian Networks` (**DBN**s) are static Bayesian Networks that are modeled over an arrangement of **time series** or sequences.
In a Dynamic Bayesian Network, each **time slice** is conditionally dependent on the previous one. The probabilities among the original distribution determine the probabilities in the successive.

To build a DBN it is necessary:

**1.** <img align="center" src="http://latex.codecogs.com/gif.latex?P(X_0)&space;\rightarrow&space;Prior&space;\&space;distribution&space;\&space;on&space;\&space;state&space;\&space;variables" title="P(X_0) \rightarrow Prior \ distribution \ on \ state \ variables" />

**2.** <img align="center" src="http://latex.codecogs.com/gif.latex?P%28X_%7Bt&plus;1%7D%20%7C%20X_t%29%20%5C%3A%5C%3A%20%5Crightarrow%20%5C%3A%20Transition%20%5C%3A%20Model%20%5C%5C%5C%5C"/>

**3.** <img align="center" src="http://latex.codecogs.com/gif.latex?P%28E_t%20%7C%20X_t%29%20%5C%3A%5C%3A%20%5Crightarrow%20%5C%3A%20Sensor%20%5C%3A%20Model"/>

The following image represents a Dynamic Bayesian Network:

<p align="center">
  <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/rain.PNG"  width="50%" height="50%"/>
</p>


### Rollup Filtering algorithm

Starting from the previously specified parameters it is possible to construct an unlimited number of intervals of the Dynamic Network, by copying the first interval; this operation is called **unrolling**.

However it suffers from an **excessive memory occupation**, since all time slices (which tend to infinity) are kept in memory.

We use the **Rollup Filtering** technique to solve this problem; it makes possible to focus on two slices at a time.
The following interval is created using the Variables Elimination algorithm on the last product interval.

To carry out the inference it can also be possible to provide a sequence of observations.


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
- `utils` package, which contains:

  - `MoralGraph.kt`, which implements a Moral Graph and all the necessary methods needed.

  - `BIFToBayesNet.kt`, which implements a BIF file parser that instantiate a BayesNet object from a BIF file (Boolean Domain only).

  - `Utils.kt`, which contains many utility methods.

- `CustomDynamicaBayesNet` class, which implements the **Rollup Filtering** algorithm.

- `Inferences` object, which instantiates an object which contains the extension of the `Variable Elimination` algorithm required by the project and which exposes the methods.

We also used external libreries:

- [GraphStream](https://github.com/graphstream/gs-core) to visualize and debug the ordering algorithms.

  Here is an example of a graph before and after undergoing the pruning of a node.

  <p align="center">
    <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/Immagine2a.png" width="20%" height="20%"/>
    <img src="https://github.com/lamba92/bayesian-net-project/blob/master/stuff/Immagine1.png" width="40%" height="40%" />
  </p>

- [AsciiTable](https://github.com/vdmeer/asciitable) to visualize probability tables and debug them. 

  Here is an example of a table before and after of a marginalization:
  
- [Weka](https://github.com/Waikato/weka-3.8)'s BIFreader to parse BIF file and code their content into our Bayesian Network.

# Getting Started

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
    compile 'com.github.Lamba92:bayesian-net-project:{latest_version}'
    compile 'com.googlecode.aima-java:aima-core:3.0.0'
    compile 'org.graphstream:gs-core:1.1.1'
    compile 'nz.ac.waikato.cms.weka:weka-dev:3.9.2'
    compile 'de.vandermeer:asciitable:0.3.2'
}
```

If using Gradle Kotlin DSL:
```
repositories {
    maven(url = "https://jitpack.io")
}
...
dependencies {
    compile("com.github.Lamba92", "bayesian-net-project", "{latest_version}")
    compile("de.vandermeer", "asciitable", "0.3.2")
    compile("com.googlecode.aima-java", "aima-core", "3.0.0")
    compile("org.graphstream", "gs-core", "1.1.1")
    compile("nz.ac.waikato.cms.weka", "weka-dev", "3.9.2")
}
```

## Usage

### Static Bayes Network

Use `Inferences.getCustomEliminationAsk()` to get an `CustomEliminationAsk()` object and then `ask()` using it:
```
val net = constructCloudySprinklerRainWetGrassNetwork()
val inf = getCustomEliminationAsk(
            inferenceMethod = CustomEliminationAsk.InferenceMethod.STANDARD,
            hMetrics = minNeighboursHeuristicFunction(),
            removeIrrelevantRVs = true,
            showMoralGraph = true,
            delay = 1000
    )
val queryVar = net.variablesInTopologicalOrder.last()
val evidenceVar = net.variablesInTopologicalOrder.first()
val res = inference.ask(arrayOf(queryVar), arrayOf(AssignmentProposition(evidenceVar, true)), net) as CustomFactor

println(res.printTable())

```

`res` will be the result distribution computed keeping in consideration an `AssignementProposition`.

### Dynamic Bayes Network

Create a `CustomDynamicBayesianNet` using a newly generated dynamic network or use an example from the factories of this library and aima's:

```
import it.unito.probability.net.CustomDynamicBayesianNet
import it.unito.probability.net.Inferences.*

...

val inference = getCustomEliminationAsk()
val dynNet = CustomDynamicBayesianNet(getComplexDynamicNetworkExample(), inference)

customNet.forward() // Moves the network one step forward
```

`CustomDynamicBayesianNet` constructor needs a network and a `BayesianInference` to proceed forward in time.

Check out the [KDocs](https://jitpack.io/com/github/lamba92/bayesian-net-project/1.0/javadoc/bayesian-net-project/) for details.

## Authors

* **Cesare Pio Iurlaro** - [CesareIurlaro](https://github.com/CesareIurlaro)
* **Giuseppe Gabbia**  - [beppe95](https://github.com/beppe95)
* **Lamberto Basti**  - [lamba92](https://github.com/lamba92)
