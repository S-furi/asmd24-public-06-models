package scala.u06.modelling.properties

import u06.utils.MSet

import scala.u06.examples.PNReadersAndWriters.Place
import scala.u06.examples.PNReadersAndWriters.Place.*

object SafetyProperties:
  sealed trait SafetyProperty[S]:
    def holds(s: S): Boolean

  /**
   *  Safety property that checks if a critical section contains
   *  at most one token.
   *
   * @param cs critical section place
   * @tparam P the type of places in the marking
   */
  case class MutualExclusion[P](cs: P) extends SafetyProperty[MSet[P]]:
    override def holds(m: MSet[P]): Boolean = m(cs) <= 1

  /**
   * Mutual exclusion [[SafetyProperty]] for readers and writers.
   * Multiple readers can read concurrently, but only one
   * writer can write at a time.
   *
   * The input map `rwPlaces` defines the relationship between
   * read and write places. Each key in the map is a read place,
   * and the associated value is a set of write places that
   * can be written to when the read place has no tokens.
   *
   * @param rwPlaces a map of places where the key is a read place
   *                 and the value is a set of write places associated with it.
   * @tparam P the type of places in the marking
   */
  case class MutualExclusionRW[P](rwPlaces: Map[P, Set[P]]) extends SafetyProperty[MSet[P]]:
    override def holds(m: MSet[P]): Boolean =
      rwPlaces.forall: (readPlace, writePlaces) =>
        val rc = m(readPlace)
        val wc = writePlaces.map(m(_)).sum
        !(rc > 0 && wc > 0) && wc <= 1

  /**
   * Safety property that checks if a place is bounded
   * to a maximum number of tokens.
   *
   * @param place the place to check
   * @param maxTokens the maximum number of tokens allowed in the place
   * @tparam P the type of places in the marking
   */
  case class Bounded[P](place: P, maxTokens: Int) extends SafetyProperty[MSet[P]]:
    override def holds(m: MSet[P]): Boolean = m(place) <= maxTokens

  /**
   * Safety property that checks if the total number of tokens
   * in a set of places equals an expected total.
   *
   * @param places the set of places to check
   * @param expectedTotal the expected total number of tokens in the places
   * @tparam P the type of places in the marking
   */
  case class TokenConservation[P](places: Set[P], expectedTotal: Int) extends SafetyProperty[MSet[P]]:
    override def holds(m: MSet[P]): Boolean = places.map(m(_)).sum == expectedTotal

  object Properties:
    def mutualExclusion[P](cs: P): SafetyProperty[MSet[P]] = MutualExclusion(cs)
    def mutualExclusionRW[P](rwPlaces: Map[P, Set[P]]): SafetyProperty[MSet[P]] = MutualExclusionRW(rwPlaces)
    def bounded[P](place: P, maxTokens: Int): SafetyProperty[MSet[P]] = Bounded(place, maxTokens)
    def tokenConservation[P](places: Set[P], expectedTotal: Int): SafetyProperty[MSet[P]] = TokenConservation(places, expectedTotal)