# COSC 3P05 – Assignment 1: Step-by-Step Guide

This guide walks you through each question of Assignment 1 step by step, explaining the concepts, strategies, and worked solutions.

---

## Question 1 – Narayana Cow Sequence

**Recurrence:** `N(n) = N(n-1) + N(n-3)` for n ≥ 3, with `N(0) = N(1) = N(2) = 1`

**Sequence:** 1, 1, 1, 2, 3, 4, 6, 9, 13, …

### Part (a) – Draw Recursion Trees for n = 3, 4, 5, 6

**Strategy:** Each node in the tree represents one recursive call. A leaf is a base case (n = 0, 1, or 2).

**Step 1 – Understand the branching:** Every call `N(n)` spawns two children: `N(n-1)` and `N(n-3)`.

**Step 2 – Draw the tree for n = 3:**
```
       N(3)
      /    \
   N(2)   N(0)
```
Both children are base cases. **Node count T(3) = 3.**

**Step 3 – Draw the tree for n = 4:**
```
         N(4)
        /    \
     N(3)   N(1)
    /    \
 N(2)   N(0)
```
**Node count T(4) = 5.**

**Step 4 – Draw the tree for n = 5:**
```
           N(5)
          /    \
       N(4)   N(2)
      /    \
   N(3)   N(1)
  /    \
N(2)  N(0)
```
**Node count T(5) = 7.**

**Step 5 – Draw the tree for n = 6:**
```
              N(6)
             /    \
          N(5)   N(3)
         /    \  /  \
      N(4)  N(2) N(2) N(0)
      /   \
   N(3)  N(1)
  /   \
N(2) N(0)
```
**Node count T(6) = 11.**

**Summary table:**

| n | T(n) |
|---|------|
| 3 |  3   |
| 4 |  5   |
| 5 |  7   |
| 6 |  11  |

---

### Part (b) – Solve the Recurrence for T(n)

**Step 1 – Write the recurrence for T(n):**

From the tree structure, each internal node spawns two sub-problems:
```
T(n) = 1 + T(n-1) + T(n-3)   for n ≥ 3
T(0) = T(1) = T(2) = 1
```

**Step 2 – Guess the closed form:** Notice the values match `2·N(n) - 1`:
- T(3) = 2·N(3) - 1 = 2·2 - 1 = 3 ✓
- T(4) = 2·N(4) - 1 = 2·3 - 1 = 5 ✓
- T(5) = 2·N(5) - 1 = 2·4 - 1 = 7 ✓
- T(6) = 2·N(6) - 1 = 2·6 - 1 = 11 ✓

where N(3)=2, N(4)=3, N(5)=4, N(6)=6 from the Narayana sequence.

**Closed form:** `T(n) = 2·N(n) - 1`

**Step 3 – Verify algebraically:** Substituting the inductive hypothesis into the recurrence:
```
T(n) = 1 + T(n-1) + T(n-3)
     = 1 + [2N(n-1) - 1] + [2N(n-3) - 1]
     = 2N(n-1) + 2N(n-3) - 1
     = 2[N(n-1) + N(n-3)] - 1
     = 2N(n) - 1      ← using the recurrence N(n) = N(n-1) + N(n-3)
```

---

### Part (c) – Proof by Strong Induction

**Claim:** `T(n) = 2·N(n) - 1` for all n ≥ 0.

**Step 1 – Base cases** (n = 0, 1, 2 are single-node trees):
- T(0) = 1 and 2·N(0) - 1 = 2·1 - 1 = 1 ✓
- T(1) = 1 and 2·N(1) - 1 = 2·1 - 1 = 1 ✓
- T(2) = 1 and 2·N(2) - 1 = 2·1 - 1 = 1 ✓

All three base cases hold.

**Step 2 – Inductive hypothesis:** Assume T(k) = 2·N(k) - 1 for all k < n (where n ≥ 3).

**Step 3 – Inductive step:**
```
T(n) = 1 + T(n-1) + T(n-3)
     = 1 + [2N(n-1) - 1] + [2N(n-3) - 1]     ← by IH
     = 2N(n-1) + 2N(n-3) - 1
     = 2[N(n-1) + N(n-3)] - 1
     = 2N(n) - 1                               ← by the Narayana recurrence
```

**Step 4 – Conclude:** By strong induction, T(n) = 2·N(n) - 1 for all n ≥ 0. □

---

## Question 2 (Bonus) – Lucas Numbers

**Recurrence:** `L(n) = L(n-1) + L(n-2)` for n ≥ 2, with `L(0) = 2, L(1) = 1`

### Part (a) – First 10 Lucas Numbers

**Step 1 – Compute iteratively:**

| n | L(n)       |
|---|-----------|
| 0 |  2        |
| 1 |  1        |
| 2 |  3        |
| 3 |  4        |
| 4 |  7        |
| 5 | 11        |
| 6 | 18        |
| 7 | 29        |
| 8 | 47        |
| 9 | 76        |

**First 10 Lucas numbers: 2, 1, 3, 4, 7, 11, 18, 29, 47, 76**

---

### Part (b) – Recursion Trees for n = 3, 4, 5, 6

**Strategy:** Each call L(n) branches into L(n-1) and L(n-2). Base cases are L(0) and L(1).

**T(3) = 5, T(4) = 9, T(5) = 15, T(6) = 25**

**Step 1 – Tree for n = 3:**
```
       L(3)
      /    \
   L(2)   L(1)
  /   \
L(1) L(0)
```
Nodes: 5

**Step 2 – Tree for n = 4:**
```
            L(4)
           /    \
        L(3)   L(2)
       /   \   /  \
    L(2) L(1) L(1) L(0)
    /  \
  L(1) L(0)
```
Nodes: 9

**Step 3 – Tree for n = 5:**
```
T(5) = 1 + T(4) + T(3) = 1 + 9 + 5 = 15
```

**Step 4 – Tree for n = 6:**
```
T(6) = 1 + T(5) + T(4) = 1 + 15 + 9 = 25
```

---

### Part (c) – Formula for T(n)

**Step 1 – Write the recurrence:** `T(n) = 1 + T(n-1) + T(n-2)`, `T(0) = T(1) = 1`

**Step 2 – Observe the pattern:** Let U(n) = T(n) + 1. Then:
```
U(n) = T(n) + 1 = [1 + T(n-1) + T(n-2)] + 1 = [T(n-1)+1] + [T(n-2)+1] = U(n-1) + U(n-2)
```
So U(n) satisfies the **Fibonacci recurrence** with U(0) = 2, U(1) = 2.

**Step 3 – Identify the closed form:** Since U(n) satisfies the Fibonacci recurrence with U(0) = U(1) = 2, and the standard Fibonacci sequence F has F(1) = F(2) = 1, we get U(n) = 2·F(n+1):
- U(0) = 2 = 2·F(1) = 2·1 ✓
- U(1) = 2 = 2·F(2) = 2·1 ✓
- U(2) = 4 = 2·F(3) = 2·2 ✓

Therefore: **T(n) = 2·F(n+1) - 1** where F(1) = F(2) = 1, F(3) = 2, F(4) = 3, …

Verify:
- T(3) = 2·F(4) - 1 = 2·3 - 1 = 5 ✓
- T(4) = 2·F(5) - 1 = 2·5 - 1 = 9 ✓
- T(5) = 2·F(6) - 1 = 2·8 - 1 = 15 ✓
- T(6) = 2·F(7) - 1 = 2·13 - 1 = 25 ✓

> **Note:** The closed form is expressed in terms of Fibonacci numbers (F), not Lucas numbers (L), because the number of recursive calls T(n) depends only on the *structure* of the recurrence (not on the initial values L(0), L(1)).

**Step 4 – Prove by strong induction** using the same structure as Q1 Part (c), substituting F for N.

---

## Question 3 – Minheap Data Structure

**Input array:** 3, 5, 8, 1, 4, 7, 11, 20, 2

### Part (a) – Build the Minheap

**Step 1 – Understand the min-heap property:** In a min-heap, every parent node is ≤ its children. The minimum element is always at the root.

**Step 2 – Build by repeated insertion (or use the heapify algorithm):**

Insert each element and sift up (bubble up) after each insertion:

1. Insert 3 → `[3]`
2. Insert 5 → `[3, 5]` (5 > parent 3, no swap)
3. Insert 8 → `[3, 5, 8]`
4. Insert 1 → `[3, 5, 8, 1]` → sift up: 1 < parent 5, swap → `[3, 1, 8, 5]` → 1 < parent 3, swap → `[1, 3, 8, 5]`
5. Insert 4 → `[1, 3, 8, 5, 4]` → sift up: parent of 4 is 3 (index 2); 4 > 3, no swap
6. Insert 7 → `[1, 3, 8, 5, 4, 7]` → sift up: parent of 7 is 8; 7 < 8, swap → `[1, 3, 7, 5, 4, 8]` → parent is 3; 7 > 3, no swap
7. Insert 11 → `[1, 3, 7, 5, 4, 8, 11]` → parent is 7; 11 > 7, no swap
8. Insert 20 → parent is 5; 20 > 5, no swap
9. Insert 2 → sift up: parent is 5; 2 < 5, swap → parent is 3; 2 < 3, swap → parent is 1; 2 > 1, no swap

**Final heap (result of insertion-based building with 1-based array indexing):**

Array: `T = [-, 1, 2, 7, 3, 4, 8, 11, 20, 5]`
```
           1
         /   \
        2     7
       / \   / \
      3   4 8  11
     / \
   20   5
```

> **Tip:** The exact shape of the heap can vary depending on the building algorithm used (sequential insertion vs. linear-time heapify). Both yield valid min-heaps; verify that each parent ≤ its children.

---

### Part (b) – Linked List Representation

**Step 1 – Define the node structure:**
```python
class HeapNode:
    def __init__(self, value):
        self.value = value
        self.left = None
        self.right = None
        self.parent = None
```

**Step 2 – Map the tree above to linked nodes:**
```
root → Node(1)
root.left → Node(2),  root.right → Node(7)
root.left.left → Node(3),   root.left.right → Node(4)
root.right.left → Node(8),  root.right.right → Node(11)
root.left.left.left → Node(20), root.left.left.right → Node(5)
```

**Step 3 – Key operations** require maintaining a pointer to the last node and using BFS to find the insertion point.

---

### Part (c) – Array Representation

**Step 1 – Use 1-based indexing** (read directly from the final heap array):

| Index | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|-------|---|---|---|---|---|---|---|---|---|
| Value | 1 | 2 | 7 | 3 | 4 | 8 |11 |20 | 5 |

**Step 2 – Index formulas:**
- Left child of index `i` → index `2i`
- Right child of index `i` → index `2i + 1`
- Parent of index `i` → index `⌊i/2⌋`

**Array (0-index unused):** `T = [-, 1, 2, 7, 3, 4, 8, 11, 20, 5]`

---

### Part (d) – ExtractMin Operation

**Step 1 – Save and remove the root (min = 1).**

**Step 2 – Move the last element (5) to the root position; heap now has 8 elements.**
```
           5
         /   \
        2     7
       / \   / \
      3   4 8  11
     /
   20
```

**Step 3 – Sift down (heapify down):**
- Compare 5 with children 2 and 7; smallest child = 2. Since 5 > 2, swap.
```
           2
         /   \
        5     7
       / \   / \
      3   4 8  11
     /
   20
```
- Compare 5 with children 3 and 4; smallest child = 3. Since 5 > 3, swap.
```
           2
         /   \
        3     7
       / \   / \
      5   4 8  11
     /
   20
```
- Compare 5 with its only child 20. Since 5 < 20, stop.

**Final array after extractMin:** `T = [-, 2, 3, 7, 5, 4, 8, 11, 20]`

The minimum value 1 was extracted, and the heap property is restored.

---

## Question 4 – Asymptotic Notations

**Given:**
- `f(n) = n + 1000n = 1001n`
- `g(n) = n³`
- `h(n) = log n`

> **Note:** If your assignment specifies h(n) = n log n instead, apply the same limit technique below with that function.

### Part (a) – Is f(n) = O(g(n))? **YES**

**Step 1 – Recall the definition:** f(n) = O(g(n)) iff ∃ c > 0, n₀ such that f(n) ≤ c·g(n) for all n ≥ n₀.

**Step 2 – Find constants:** Choose c = 1001, n₀ = 1.
```
1001n ≤ 1001·n³  for all n ≥ 1   (since n ≤ n³)   ✓
```

---

### Part (b) – Is f(n) = O(h(n))? **NO**

**Step 1 – Use the limit definition:**

| Limit of f(n)/g(n) | Relationship |
|--------------------|-------------|
| 0                  | f = o(g) ⊆ O(g) |
| c > 0 (finite)     | f = Θ(g) ⊆ O(g) |
| ∞                  | f = ω(g); f ≠ O(g) |

**Step 2 – Compute (using h(n) = log n):**
```
lim(n→∞)  1001n / log n  =  ∞     (since n grows faster than log n)
```

Since the limit is ∞, f grows **faster** than h, so f(n) ≠ O(h(n)). In fact, h(n) = O(f(n)). ✓

---

### Part (c) – Is f(n) = ω(g(n))? **NO**

**Step 1 – Recall:** f(n) = ω(g(n)) requires lim(n→∞) f(n)/g(n) = ∞.

**Step 2 – Compute:**
```
lim(n→∞) 1001n / n³ = lim(n→∞) 1001/n² = 0
```

Since the limit is 0, f grows **slower** than g, so f(n) ≠ ω(g(n)). In fact, f(n) = o(g(n)).

---

### Part (d) – Is f(n) = ω(h(n))? **YES**

**Step 1 – Compute (using h(n) = log n):**
```
lim(n→∞) 1001n / log n  =  ∞     (n grows much faster than log n)
```

Since the limit is ∞, f(n) = ω(h(n)). ✓

---

**General approach for all asymptotic parts:**
1. Write down the definition (O, o, Ω, ω, Θ)
2. Compute the limit lim(n→∞) f(n)/g(n)
3. Use the limit laws table to determine the relationship

| Limit | Relationship |
|-------|-------------|
| 0     | f = o(g) and f = O(g) |
| c > 0 (finite) | f = Θ(g) |
| ∞     | f = ω(g) and f = Ω(g) |

---

## Question 5 – Bubble Sort Analysis

### Part (a) – Time Complexity Analysis

**The algorithm:**
```
for i = 1 to n:
    for j = 1 to n-1:
        if A[j] > A[j+1]:
            swap(A[j], A[j+1])
```

**Step 1 – Count operations:**
- Outer loop: n iterations
- Inner loop: n-1 iterations per outer iteration
- Total comparisons: n × (n-1) = n² - n

**Step 2 – Simplify:** n² - n = O(n²)

**Step 3 – All cases:**
- Best case: O(n²) — no early termination in this version
- Worst case: O(n²) — reverse-sorted input
- Average case: O(n²)

**Conclusion:** Time complexity is **Θ(n²)**

> **Optimization tip:** An optimized version with an early-exit flag achieves O(n) best case.

---

### Part (b) – Proof of Correctness by Induction

**Loop invariant:** After k iterations of the outer loop, the k largest elements occupy positions A[n-k+1..n] in sorted order.

**Step 1 – Base case (k = 0):** No elements placed yet — vacuously true.

**Step 2 – Inductive hypothesis:** After k iterations, the k largest elements are in their final positions.

**Step 3 – Inductive step:** During iteration k+1:
- The inner loop scans A[1..n-k]
- The maximum of A[1..n-k] "bubbles" to position n-k
- This element is the (k+1)-th largest element overall
- It is now in its final position

**Step 4 – Termination:** After n iterations, all n elements are in sorted order. □

---

## Question 6 – Celebrity Problem Implementation

**Definition:** A celebrity is someone who is known by everyone but knows nobody.

**Given:** An n×n boolean matrix `knows[i][j]` = true if person i knows person j.

### Algorithm (O(n) time)

**Step 1 – Eliminate candidates (n-1 comparisons):**
```python
def find_celebrity(knows, n):
    candidate = 0
    for i in range(1, n):
        if knows[candidate][i]:
            candidate = i  # candidate knows i → candidate can't be celebrity
    # Now 'candidate' is the only possible celebrity
```

**Step 2 – Verify the candidate (2(n-1) comparisons):**
```python
    for i in range(n):
        if i != candidate:
            if knows[candidate][i] or not knows[i][candidate]:
                return -1  # No celebrity
    return candidate
```

**Total:** O(n) time, O(1) extra space.

### File I/O Requirements

Per the assignment specification:

**Step 1 – Read input from a `.txt` file:**
```python
def read_input(filename):
    with open(filename, 'r') as f:
        lines = f.readlines()
    n = int(lines[0].strip())
    knows = []
    for i in range(1, n + 1):
        row = list(map(int, lines[i].strip().split()))
        knows.append(row)
    return n, knows
```

**Step 2 – Write output to `"output " + input_filename`:**
```python
def write_output(input_filename, result):
    output_filename = "output " + input_filename
    with open(output_filename, 'w') as f:
        if result == -1:
            f.write("No celebrity found\n")
        else:
            f.write(f"Celebrity is person {result}\n")
```

**Step 3 – Name your main function with your name appended**, e.g.:
```python
def find_celebrity_YourName(knows, n):
    ...
```

**Step 4 – Main entry point:**
```python
import sys

if __name__ == "__main__":
    input_file = sys.argv[1]
    n, knows = read_input(input_file)
    result = find_celebrity_YourName(knows, n)
    write_output(input_file, result)
```

**Step 5 – Run with:**
```
python solution.py input.txt
```

---

## Compiling the LaTeX Document

If you are writing your solutions in LaTeX:

**Step 1 – Install LaTeX** (if not already installed):
- Windows: [MiKTeX](https://miktex.org/) or [TeX Live](https://tug.org/texlive/)
- macOS: [MacTeX](https://tug.org/mactex/)
- Linux: `sudo apt install texlive-full`

**Step 2 – Required packages:** `amsmath`, `amssymb`, `forest`, `algorithm`, `algpseudocode`, `geometry`, `tikz`

**Step 3 – Compile:**
```bash
pdflatex assignment1_solution.tex
```

**Step 4 – If using `forest` package for recursion trees**, run twice:
```bash
pdflatex assignment1_solution.tex
pdflatex assignment1_solution.tex
```

**Step 5 – View the output PDF** to verify all diagrams render correctly.

---

## Quick Reference

| Question | Topic | Key Concept |
|----------|-------|-------------|
| Q1 | Narayana Cow Sequence | Recursion trees, closed form T(n) = 2·N(n)−1, strong induction |
| Q2 | Lucas Numbers | Fibonacci-like recurrence, same tree-node counting technique |
| Q3 | Minheap | Sift-up (insert), sift-down (extractMin), array indexing |
| Q4 | Asymptotic Notation | Limit method: 0→O, finite→Θ, ∞→Ω |
| Q5 | Bubble Sort | Loop invariant, Θ(n²) complexity |
| Q6 | Celebrity Problem | Two-pass O(n) elimination + verification, file I/O |
