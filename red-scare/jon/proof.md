# Proof

The many problem is defined in the assignment description as follows:

>$Many$
>
>Return the maximum number of red vertices on any path from $s$ to $t$.
>
>To be precise, let $P$ be the set of $s \rightarrow t$-paths and let $r(p)$ denote the number of red vertices on a path $p$.
>
>Return $max \{ r(p): p \in P \}$.
>
>If no path from $s$ to $t$ exists, return ‘-1’.
>
>In Gex, the answer is ‘$2$’ (because of the path $0$, $5$, $6$, $7$, $3$).

## Definitions

A graph $G$ consists of two parts:

* A set $V$ of $n$ vertices: $\{ v_0, ..., v_{n-1} \}$, some of these vertices are also in the red set $R$.
* A set $E$ of $m$ edges: $\{ (v_0, u_0), ..., (v_{m-1}, u_{m-1}) \}$, where $v \in V$ and $u \in V$.

## Special case

When we are working with DAGs (Directed Acyclic Graphs), it is possible for us to solve the problem by redefining the graph and then applying the Bellman-Ford shortest path algorithm.

The redefinition is done by assigning particular weights to every edge in the graph, depending on whether the edge is going towards a red vertex or not.
First we define a weight function $w(e)$, where $e \in E$:

$w(e) = w(v, u) = \begin{cases} -1 & u\textrm{ in }R\\1 & \textrm{otherwise}\end{cases}$

We then define a new set of weighted edges $W'$:

$W' = \{ w(e), \forall e \in E \}$

Then, we simply apply Bellman-Ford's shortest path algorithm.
Since the graph is a DAG, the algorithm will encounter no cycles and is guaranteed to return.

The running time of the redefinition is $O(E)$, the final running time when Bellman-Ford has been applied is $O(VE)$

## General case

The general case of the problem cannot be solved in polynomial time.
In order to convince ourselves that this is the case, we reduce an instance of the $Many$-problem to an instance of the [Hamiltonian Path Problem](https://en.wikipedia.org/wiki/Hamiltonian_path).

<!-- As we are only interested in the Red set of vertices, we should redefine the graph so that it only contains the red vertices. -->
<!-- This is tricky, but it can be done by recursively redefining the edges in the graph: -->
<!-- Fold over the set of edges with an empty set: if v, u (of the edge (v, u)) are both Red vertices, add them to the accumulating parameter. -->

We should write this in mathematical terms only, but that is too hard for me to grasp right now.
Here's the transformation in F#:

```fsharp
module Graph

type Vertex = V of int
type Edge = E of Vertex * Vertex
type Graph = { vertices: Vertex set; edges: Edge set }

let foldEdges (g: Graph) (state: 'S) (folder: 'S -> Edge -> 'S) =
  Set.fold g.edges state folder

let redefine (g: Graph) =
  let rec removeWhites acc vertices edges =
    1
```