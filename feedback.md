# Project 3 (Chess) Feedback #
## CSE 332 Autumn 2019 ##

Team: Nathan White (nwhite4) and Ben Yu (bmy5)
<br>

## Unit Tests ##

<!-- For the P3 Unit Test, go to gitlab and check the pipeline    -->
<!-- results for the most recent commit                           -->

**Minimax** `(4/4)` 
<!-- -2pts per failed test                                        -->

**ParallelMinimax** `(15/15)`
<!-- -5pts per failed test                                        -->

**AlphaBeta** `(9/9)`
<!-- -3pts per failed test                                        -->

**Jamboree** `(20/20)`
<!-- -5pts per failed test                                        -->

--------

## Clamps Tests ##

*Score*
`(8/8)`


--------

## Miscellaneous ##
<!-- For the misc category, you must LOOK AT THE CODE.  Each line -->
<!-- explains how many points that particular error is worth. If  -->
<!-- an error does not occur in the project, delete that line     -->
<!-- from this file.                                              -->
<!--                                                              -->
<!-- For the purposes of grading, "copying in the parent" means   -->
<!-- that any of these three operations are done in the base case -->
<!-- of divide-and-conquer prior to calling fork():               -->
<!--    * Making a copy of a board                                -->
<!--    * Applying a move to a board                              -->
<!--    * Generating a new movelist                               -->
<!--                                                              -->
<!-- In ParallelSearcher:                                         -->
<!--    -3pt for N forks instead of N-1 forks and 1 compute in    -->
<!--       the divide-and-conquer base case                       -->
<!--    -5pt for using compute() in the divide-and-conquer base   -->
<!--       case (instead of N forks)                              -->
<!--    -6pt for not using both a divide-and-conquer cutoff and   -->
<!--          a depth cutoff                                      -->
<!--    -ALL for no parallelism (i.e. no fork() anywhere)         -->
<!--                                                              -->
<!-- In JamboreeSearcher:                                         -->
<!--    -3pt for copying in the parent instead of child           -->
<!--    -3pt for N forks instead of N-1 forks and 1 compute in    -->
<!--       the divide-and-conquer base case                       -->
<!--    -8pt for using compute() in the divide-and-conquer base   -->
<!--       case (instead of N forks)                              -->
<!--    -6pt for not using both a divide-and-conquer cutoff and   -->
<!--          a depth cutoff                                      -->
<!--    -2pt for skipping percent-sequential on the root movelist -->
<!--    -7pt for only doing percent-sequential on the root        -->
<!--          movelist                                            -->
<!--    -7pt for only doing percent-sequential on the first       -->
<!--          movelist at every level                             -->
<!--    -10pt for forking all child tasks in a loop instead of    -->
<!--          divide-and-conquer (i.e. their code is the          -->
<!--          functional equivalent of an infinite divide cutoff) -->
<!--    -ALL for no parallelism (i.e. no fork() anywhere)         -->
<!--                                                              -->
<!-- Ask in Slack if you see:                                     -->
<!--    * Multiple calls to POOL.invoke() (e.g. in a loop)        -->
<!--    * Using locks, or any other synchronization mechanism     -->
<!--    * High Clamps score but low Jamboree unit test score      -->
<!--                                                              -->
<!-- They shouldn't lose more points than they got on the unit    -->
<!-- tests for either searcher. (Cap the deduction if this is the -->
<!-- case.)                                                       -->

`(-6/0)`
- Your ParallelSearcher copies boards in the parent instead of in the child,
- In the divide and conquer cutoff, your ParallelSearcher should save one move to compute
in the current thread (instead of forking it) <br />
<!-- LOST: Your ParallelSearcher calls `invoke()` more than once  -->
<!-- LOST: Your ParallelSearcher should not need to rely on locks -->
<!--       to work properly                                       -->
<!-- LOST: In the divide-and-conquer cutoff, your                 -->
<!--       ParallelSearcher should save one move to compute in    -->
<!--       the current thread (instead of forking it)             -->
<!-- LOST: Your ParallelSearcher doesn't respect the              -->
<!--       divide-and-conquer cutoff                              -->
<!-- LOST: Your ParallelSearcher doesn't respect the depth cutoff -->

`(-6/0)`
- Your JamboreeSearcher copies boards in the parent instead of in the child,
- In the divide and conquer cutoff, your JamboreeSearcher should save one move to compute
in the current thread (instead of forking it)  <br />
<!-- LOST: Your JamboreeSearcher copies boards in the parent      -->
<!--       thread instead of in the child                         -->
<!-- LOST: Your JamboreeSearcher calls `invoke()` more than once  -->
<!-- LOST: Your JamboreeSearcher should not need to rely on locks -->
<!--       to work properly                                       -->
<!-- LOST: In the divide-and-conquer cutoff, your                 -->
<!--       JamboreeSearcher should save one move to compute in    -->
<!--       the current thread (instead of forking it)             -->
<!-- LOST: Your JamboreeSearcher doesn't respect the              -->
<!--       divide-and-conquer cutoff                              -->
<!-- LOST: Your JamboreeSearcher doesn't respect the depth cutoff -->


--------

### Above and Beyond ###

**Above and Beyond**
`(EX: 0)`
