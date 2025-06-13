package scala.u06.modelling.properties

import u06.utils.MSet

object LivenessProperties:
  sealed trait LivenessProperty[S]:
    def holds(trace: LazyList[S]): Boolean

  /**
   * A place is eventually marked if it is marked at some point in the trace.
   *
   * @param place the place to check for eventual marking
   * @tparam P the type of the place
   */
  case class EventuallyMarked[P](place: P) extends LivenessProperty[MSet[P]]:
    override def holds(trace: LazyList[MSet[P]]): Boolean = trace.exists(m => m(place) > 0)

  /**
   * A property that checks if a certain predicate holds for at least one state in the trace.
   * This can be used to check if a certain condition is eventually satisfied in the system's execution.
   *
   * @param p the predicate to check
   * @tparam T the type of the state to check against
   */
  case class EventuallyEnabled[T](p: T => Boolean) extends LivenessProperty[T]:
    override def holds(trace: LazyList[T]): Boolean = trace.exists(p)

  /**
   * A property that checks if there is no global deadlock in the system.
   *
   * @param next the function that returns the next states from a given state
   * @tparam S the type of the state in the system
   */
  case class NoGlobalDeadlock[S](next: S => Iterable[S]) extends LivenessProperty[S]:
    override def holds(trace: LazyList[S]): Boolean =
      !trace.exists(s => next(s).isEmpty)

  object Properties:
    def eventuallyMarked[P](place: P): LivenessProperty[MSet[P]] = EventuallyMarked(place)
    def eventuallyEnabled[T](p: T => Boolean): LivenessProperty[T] = EventuallyEnabled(p)
    def noGlobalDeadlock[S](next: S => Iterable[S]): LivenessProperty[S] = NoGlobalDeadlock(next)