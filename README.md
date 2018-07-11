[![Build Status](https://travis-ci.org/lamba92/bayesian-net-project.svg?branch=master)](https://travis-ci.org/lamba92/bayesian-net-project) [![](https://jitpack.io/v/Lamba92/bayesian-net-project.svg)](https://jitpack.io/#Lamba92/bayesian-net-project)


# BAYESIAN NETWORKS PROJECTS

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
    maven(url = "http://jade.tilab.com/maven/")
}
...
dependencies {
    implementation("com.github.Lamba92", "bayesian-net-project", "{latest_version}")
    implementation("com.googlecode.aima-java", "aima-core", "3.0.0")
}
```
If you are using Maven, switch to Gradle, it's 2018.

## Usage

Create a `CustomDynamicBayesianNet` using a newly generated dynamic network or use an example from the factories of this library and aima's:
```
import it.unito.bayesian.net.CustomDynamicBayesianNet
import it.unito.bayesian.net.Inferences.eliminationAskWithMinWeightHeuristic

...

val customNet = CustomDynamicBayesianNet(getUmbrellaWorldNetwork(), eliminationAskWithMinWeightHeuristic)

customNet.forward() // Moves the network one step forward
```

`CustomDynamicBayesianNet` constructor needs a network and a `BayesianInference` to proceed forward in time.

Check out the [KDocs](https://jitpack.io/com/github/lamba92/bayesian-net-project/0.3/javadoc/bayesian-net-project/) for details. 

For a usage example have a look [at some tests here](https://github.com/lamba92/bayesian-net-project/blob/master/src/test/kotlin/it/unito/bayesian/net/test/Test.kt).

## Authors

* **Cesare Iurlaro** - [CesareIurlaro](https://github.com/CesareIurlaro)
* **Giuseppe Gabbia**  - [beppe95](https://github.com/beppe95)
* **Lamberto Basti**  - [lamba92](https://github.com/lamba92)
