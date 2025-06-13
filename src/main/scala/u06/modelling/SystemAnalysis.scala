package u06.modelling

import u06.modelling.System

import scala.collection.immutable.{LazyList, List, Seq, Set}
import scala.u06.modelling.properties.LivenessProperties.LivenessProperty
import scala.u06.modelling.properties.SafetyProperties.SafetyProperty

// Basical analysis helpers
object SystemAnalysis:

  type Path[S] = List[S]

  extension [S](system: System[S])

    def normalForm(s: S): Boolean = system.next(s).isEmpty

    def complete(p: Path[S]): Boolean = normalForm(p.last)

    // paths of exactly length `depth`
    def paths(s: S, depth: Int): Seq[Path[S]] = depth match
      case 0 => LazyList()
      case 1 => LazyList(List(s))
      case _ =>
        for
          path <- paths(s, depth - 1)
          next <- system.next(path.last)
        yield path :+ next

    // complete paths with length '<= depth' (could be optimised)
    def completePathsUpToDepth(s: S, depth:Int): Seq[Path[S]] =
      (1 to depth).to(LazyList) flatMap (paths(s, _)) filter complete

    /**
     * Find all states reachable from the initial state within a given depth,
     * using a breadth-first search approach. This dramatically reduces the
     * search space compared to [[paths]] searching.
     *
     * @param initial the initial state from which to start the search
     * @param depth   the maximum depth to search for reachable states
     * @return a set of all reachable states within the specified depth
     */
    def bfsReachableStates(initial: S, depth: Int): Set[S] =
      var visited = Set[S]()
      var frontier = Set(initial)

      var d = 0
      while d < depth && frontier.nonEmpty do
        val next = for
          state <- frontier
          succ <- system.next(state)
          if !visited.contains(succ)
        yield succ

        visited ++= next
        frontier = next
        d += 1

      visited

    /**
     * Find all states reachable from an initial state, in an unbounded
     * fashion (goes on to infinity), lazily evaluating states. As
     * for [[bfsReachableStates()]] a breadth first search approach
     * is used.
     *
     * @param initial the initial state from which to start the search
     * @return a lazy list of all reachable states from given initial state
     */
    private def lazyReachableStates(initial: S): LazyList[S] =
      def _rec(visited: Set[S], frontier: LazyList[S]): LazyList[S] = frontier match
        case LazyList() => LazyList.empty
        case head #:: tail =>
          if visited.contains(head) then
            _rec(visited, tail)
          else
            val next = system.next(head).to(LazyList)
            head #:: _rec(visited + head, tail ++ next)

      _rec(Set.empty, LazyList(initial))

    /**
     * Check if the system never violates a property `p` starting from an initial state
     * within a given maximum depth. This is done by checking all reachable states
     * from the initial state and ensuring that all of them satisfy the property `p`.
     *
     * @param initial the initial state from which to start the check
     * @param maxdepth the maximum depth to search for reachable states
     * @param p the property to check against each reachable state
     * @return true if all reachable states satisfy the property `p`, false otherwise
     */
    def neverViolates(initial: S, maxdepth: Int)(p: SafetyProperty[S]): Boolean =
      bfsReachableStates(initial, maxdepth).forall(p.holds)


    /**
     * Check if a liveness property holds for all reachable states
     * from an initial state within a given maximum depth.
     *
     * @param initial the initial state from which to start the check
     * @param maxDepth the maximum depth to search for reachable states
     * @param p the liveness property to check against reachable states
     * @return true if the property holds for all reachable states, false otherwise
     */
    def checkLiveness(initial: S, maxDepth: Int)(p: LivenessProperty[S]): Boolean =
      p.holds(lazyReachableStates(initial).take(maxDepth))
