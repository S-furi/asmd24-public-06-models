package scala.u06.modelling

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.u06.modelling.properties.LivenessProperties.Properties.*
import scala.u06.modelling.properties.SafetyProperties.Properties.*

class PNReadersAndWriters extends AnyFunSuite:

  import u06.examples.PNReadersAndWriters.*

  val initialMarking = MSet(P1, P1, P5)

  test("PN readers and writers should return empty marking if place p5 does not contain a token"):
    pnRW.paths(MSet(P1, P1, P1), 10).toList should contain theSameElementsAs List()

  test("mutual exclusion never fails within 100 steps"):
    assert(
      pnRW.neverViolates(initialMarking, 100)(mutualExclusionRW(Map(P6 -> Set(P7)))),
      "Mutual exclusion violated within 100 steps"
    )

  test("Shared resource (place P5) is bounded to 1"):
    assert(
      pnRW.neverViolates(initialMarking, 100)(bounded(P5, 1)),
      "Bounded place P5 violated within 100 steps"
    )

  test("no deadlock reachable from initial state"):
    assert(
      pnRW.checkLiveness(initialMarking, 100)(noGlobalDeadlock(s => pnRW.next(s))),
      "Deadlock reachable within 100 steps"
    )

  test("Writers will eventually write"):
    assert(
      pnRW.checkLiveness(initialMarking, 100)(eventuallyMarked(P6)),
      "Writer never marked within 100 steps"
    )

  test("Readers will eventually read"):
    assert(
      pnRW.checkLiveness(initialMarking, 100)(eventuallyMarked(P7)),
      "Reader never marked within 100 steps"
    )

  test("A blocked writer must eventually be unblocked"):
    def writerEnabled(m: MSet[Place]): Boolean =
      pnRW.next(m).exists(nextMarking => nextMarking(P7) == 0)

    val marking = MSet(P1, P1, P7)

    assert(
      pnRW.checkLiveness(marking, 100)(eventuallyEnabled(writerEnabled)),
      "Writer was blocked indefinitely within 100 steps"
    )
