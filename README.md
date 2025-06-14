# asmd24-public-06-models

## Lab Activity 06 [Verifier]

This task can be showcased inside the test suite of
[Readers and Writers Petri Net](https://github.com/S-furi/asmd24-public-06-models/blob/master/src/test/scala/u06/modelling/PNReadersAndWriters.scala).

Simple, high level APIs have been created in order to support the definition of [**Liveness**](https://github.com/S-furi/asmd24-public-06-models/blob/master/src/main/scala/u06/modelling/properties/LivenessProperties.scala) and [**Safety**](https://github.com/S-furi/asmd24-public-06-models/blob/master/src/main/scala/u06/modelling/properties/SafetyProperties.scala) properties.
Moreover, a more efficient approach in searching through paths of a trace have been implemented by means of a breadth-first
search approach.

## Lab Activity 07 [Simulator]

The `Simulator` task is implemented inside the [`StochasticSimulationMetrics`](https://github.com/S-furi/asmd24-public-06-models/blob/master/src/main/scala/u07/modelling/StochasticSimulationMetrics.scala). The requested measures are printed out in the main function, while a simple generic API for CTMCs traces have been extracted, in order to perform tasks such as:
- simulate $n$ runs
- given a set of traces:
  - take first `n` elements of all traces
  - take elements of a trace up until a given state
  - compute the average running time
    - in total
    - at a given state

