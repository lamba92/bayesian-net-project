[![Build Status](https://travis-ci.org/lamba92/bayesian-net-project.svg?branch=master)](https://travis-ci.org/lamba92/bayesian-net-project) [![](https://jitpack.io/v/Lamba92/bayesian-net-project.svg)](https://jitpack.io/#Lamba92/bayesian-net-project)

# Bayesian Networks Project
The project consists in two exercises:

   **1.** **VE (Variable Elimination)** algorithm extension and its implementation on `Static Bayesian Networks`,
 
   **2.** **Rollup Filtering inference** algorithm and its implementation on `Dynamic Bayesian Networks (DBN)`.
   
##  VE (Variable Elimination) algorithm extension
Si suddivide in quattro steps :
 - Rimuovere le variabili irrilevanti,
 - Implementare euristiche di ordinamento delle variabili,
 - Permettere inferenze MPE (Most Probable Explanation) e MAP (Maximum a Posteriori Probability),
 - Analisi risultati algoritmi.

# Preliminaries

## Static Bayesian Networks
A static Bayesian or probabilistic network B is a graphical structure that models a set of stochastic variables, 
the conditional independencies among these variables, and a joint probability distribution over these variables.
B includes a directed acyclic graph GB = (V,A), modeling the variables and conditional independencies in the network, 
and a set of parameter probabilities in the form of conditional probability tables (CPTs), capturing the strengths of the
relationships between the variables. The network models a joint probability distribution Pr(V) =n i=1 Pr(Vi | π(Vi)) over
its variables, where π(Vi)denotes the parents of Vi in GB.

## Variable Elimination algorithm
Variable elimination (VE) is a simple and general exact inference algorithm in probabilistic graphical models, such as Bayesian networks and Markov random fields. It can be used for inference of maximum a posteriori (MAP) state or estimation of conditional or marginal distributions over a subset of variables. The algorithm has exponential time complexity, but could be efficient in practice for the low-treewidth graphs, if the proper elimination order is used (which is a NP-hard problem).  
Per trovare un appropriato ordine di eliminazione delle variabili della rete Bayesiana utilizziamo delle euristiche.

### Euristiche
La struttura dati utilizza per trovare un ordine di eliminazione prende il nome di Moral Graph.
In graph theory, a moral graph is used to find the equivalent undirected form of a directed acyclic graph. The moralized counterpart of a directed acyclic graph is formed by adding edges between all pairs of nodes that have a common child, and then making all edges in the graph undirected.

Definiamo una funzione di valutazione la quale utilizza una delle seguenti euristiche:

 - Min-neighbors: The cost of a vertex is the number of neighbors it has in the current graph.
 
 - Min-weight: The cost of a vertex is the product of weights — domain cardinality — of its neighbors.
 
 - Min-ﬁll: The cost of a vertex is the number of edges that need to be added to the graph due to its elimination.
 
 - Weighted-min-ﬁll: The cost of a vertex is the sum of weights of the edges that need to be added to the graph due to its elimination, where a weight of an edge is the product of weights of its constituent vertices.
 
Tuttavia è dimostrato che nessuna di queste funzioni sia migliore di un'altra in quanto la loro bontà è strettamente dipendente dalla topologia della rete su cui l'algoritmo stesso viene applicato.

Moral graph e funzione di valutazione vengono utilizzati da un algoritmo greedy l'ordinamento appropriato con cui effettuare l'algortimo VE.

L'algoritmo greedy viene riportato nella seguente immagine: IMG.
 

## Dynamic Bayesian Networks
Dynamic Bayesian Networks (DBN's) are static Bayesian networks that are modeled over an arrangement of time-series. 
In a Dynamic Bayesian Network, each time slice is conditionally dependent on the previous one. The probabilities among the original distribution determine the probabilities in the successive time series.

Per costruire una DBN occorre:
**1.** Distribuzione a priori sulle variabili di stato P(Xo)
**2.** il modello di transizione P(Xt+1 | Xt)
**3.** il modello sensore P(Et | Xt)

É possibile costruire una rete dinamica completa con un numero non limitato di intervalli a partire da questa specifica, copiando il primo intervallo.

## Rollup Filtering algorithm
É possibile utilizzare molteplici tecnichen per effettuare l'inferenza esatta su una DBN. Esse sono l'Unrolling, il Filtering e lo Smoothing. 

Tuttavia, noi ci siamo concentrati sulla tecnica del Rollup Filtering. Con questa tecnica è possibile concentrarsi solamente su due intervalli alla volta della DBN; l'intervallo successivo verrà creato utilizzando l'algortimo di VE sull'ultimo intervallo disponibile.

Per effettuare l'inferenza occorre possedere una sequenza di osservazioni.

# Project management
The project has been divided into three main parts:
- `utils` folder, which contains:

  - `MoralGraph.kt`, which implements a Moral Graph and all the necessary methods needed,

  - `BIFToBayesNet.kt`, which implements a BIF file parser used to instatiate a BayesNet object from a BIF file (Boolean Domain only),
  
  - `Utils.kt` and `WrongDistributionException` files.
  
- `CustomDynamicaBayesNet.kt` , which implents l'algoritmo di Rollup Filtering con due slice alla volta. Per far avanzare la rete allo stato temporale successivo t+1, dove t è stato temporale attuale, occorre eseguire il metodo `forward`

- `Inferences.kt` che istanzia un oggetto il quale contiene l'estensione dell'algoritmo di VE richiesta dal progetto e che espone i metodi

  - `order`, che a partire da una `BayesianNetwork` restituisce un ordine appropriato con cui applicare l'algortimo di VE,
  
  - `calculateVariables` che, date le variabili di query, le evidenze e la Rete Bayesiana, rimuove da quest'ultima le **variabili irrilevanti**, ossia every variable that is not an ancestor of a query variable or evidence variable.

There is also a `resource` folder, which contains all the BIF format hard-coded nets.


# VE extension implementation

## Utilities
They are contained within the utils folder. They are:





## Authors

* **Cesare Iurlaro** - [CesareIurlaro](https://github.com/CesareIurlaro)
* **Giuseppe Gabbia**  - [beppe95](https://github.com/beppe95)
* **Lamberto Basti**  - [lamba92](https://github.com/lamba92)
