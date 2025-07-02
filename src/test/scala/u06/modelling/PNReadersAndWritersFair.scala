package scala.u06.modelling

import org.scalatest.funsuite.AnyFunSuite

import u06.utils.MSet
import scala.u06.examples.PNReadersAndWriters.Place.*
import scala.u06.examples.PNReadersAndWriters.pnRWFair
import scala.u06.modelling.properties.LivenessProperties.Properties.eventuallyMarked

class PNReadersAndWritersFair extends AnyFunSuite:

  val initialMarking = MSet(P1, P1, P5)

  test("Readers will eventually (surely) read"):
    assert(
      pnRWFair.checkLiveness(initialMarking, 10)(eventuallyMarked(P7)),
      "Reader never marked within 10 steps"
    )

