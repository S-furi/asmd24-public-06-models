package u06.modelling

// Basical analysis helpers
object SystemAnalysis:

  type Path[S] = List[S]

  extension [S](system: System[S])

    def normalForm(s: S): Boolean = system.next(s).isEmpty

    def complete(p: Path[S]): Boolean = normalForm(p.last)

    def isReachable(initial: S, goal: S => Boolean): Boolean =
      var visited = Set[S]()
      var queue = List(initial)

      while queue.nonEmpty do
        val current = queue.head
        queue = queue.tail
        if goal(current) then return true
        if !visited.contains(current) then
          visited += current
          queue ++= system.next(current).filter(!visited.contains(_))

      false

    def findDeadlocks(initial: S): Set[S] =
      var visisted = Set[S]()
      var queue = List(initial)
      var deadlocks = Set[S]()
      ???


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
      (1 to depth).to(LazyList) flatMap (paths(s, _)) filter (complete(_))