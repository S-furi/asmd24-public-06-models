package scala.u07.modelling

import u07.examples.StochasticChannel.State.*
import u07.examples.StochasticChannel.{State, stocChannel}
import u07.modelling.CTMC
import u07.modelling.CTMCSimulation.*

import java.util.Random
object StochasticSimulationMetrics:
  type Simulations[S] = LazyList[Trace[S]]
  given Random = new Random

  extension [S](self: CTMC[S])
    def simulateNRuns(n: Int, s0: S)(using rnd: Random): Simulations[S] =
      LazyList.range(0, n) map (i => self.newSimulationTrace(s0, rnd))

  extension [S](self: Trace[S])
    private def intervals: Iterator[(S, Double)] = self.sliding(2).collect:
      case LazyList(e1: Event[S], e2: Event[S]) => (e1.state, e2.time - e1.time)

  extension [S](self: Simulations[S])
    def prune(n: Int): Simulations[S] = self map (_.take(n))
    def pruneAllAt(s: S): Simulations[S] = self map (trace => trace.takeWhile(_.state != s))
    def getAverageTime: Double = if (self.nonEmpty) self.map(_.last.time).sum / self.size else 0.0
    def getAverageSpentTimeAt(p: S => Boolean): Double = self.map(trace => trace.intervals.collect { case (s, t) if p(s) => t }.sum).sum / self.size
    def getRelativeTimeSpentAt(p: S => Boolean): Double = getAverageSpentTimeAt(p) / self.getAverageTime

object TryStochasticChannelSimulationMetrics:
  import StochasticSimulationMetrics.{*, given}

  @main def mainStochasticChannelSimulationMetrics =
    val successful = stocChannel.simulateNRuns(50, IDLE).pruneAllAt(DONE)
    val avgTime = successful.getAverageTime
    val avgTimeSpentFailing = successful.getAverageSpentTimeAt(_ == FAIL)
    val percentageTimeFailing = successful.getRelativeTimeSpentAt(_ == FAIL) * 100

    println(s"Percentage of time spent in FAIL state: $percentageTimeFailing % ($avgTimeSpentFailing s / $avgTime s)")

  @main def mainStochasticPNRWSimulationMetrics =
    import scala.u07.examples.StochasticReadersAndWriters.*
    import u07.utils.MSet

    val successful = toCTMC(spn).simulateNRuns(50, MSet(P1, P1, P1, P5)).prune(10_000)
    val avgTime = successful.getAverageTime

    val avgTimeSpentWriting = successful.getAverageSpentTimeAt(_(P7) > 0)
    val percentageTimeSpentWriting = successful.getRelativeTimeSpentAt(_(P7) > 0) * 100

    val avgTimeSpentReading = successful.getAverageSpentTimeAt(_(P6) > 0)
    val percentageTimeSpentReading = successful.getRelativeTimeSpentAt(_(P6) > 0) * 100

    val avgTimeSpentTransitioning = successful.getAverageSpentTimeAt(m => m(P6) == 0 && m(P7) == 0)
    val percentageTimeSpentTransitioning = successful.getRelativeTimeSpentAt(m => m(P6) == 0 && m(P7) == 0) * 100

    println(s"Percentage of time spent Writing: $percentageTimeSpentWriting % ($avgTimeSpentWriting / $avgTime s)")
    println(s"Percentage of time spent Reading: $percentageTimeSpentReading % ($avgTimeSpentReading / $avgTime s)")
    println(s"Percentage of time spent Transitioning: $percentageTimeSpentTransitioning % ($avgTimeSpentTransitioning / $avgTime s)")
