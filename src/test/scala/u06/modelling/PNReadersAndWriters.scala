package scala.u06.modelling

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class PNReadersAndWriters extends AnyFunSuite:

  import u06.examples.PNReadersAndWriters.*

  test("PN readers and writers should return empty marking if place p5 does not contain a token"):
    pnRW.paths(MSet(P1), 10).toList should contain theSameElementsAs List()

  test("PN readers and writers should not allow to write and read in the same time"):
    pnRW.paths(MSet(P1, P1, P5), 10) foreach: path =>
      path should not contain MSet(P7, P6) // P7: writing, P6 reading