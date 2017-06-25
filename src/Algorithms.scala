
import scala.collection.mutable
import scala.util.control.Breaks
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created by jbuisine on 12/01/17.
  *
  * Class which contains all algorithms
  */
object Algorithms {

  /**
    * Attribute used to saved number of evaluation
    */
  val df = new java.text.DecimalFormat("0.##")
  var nbEvaluation: Int = 0
  var solutionsPassed = new ListBuffer[Array[Int]]

  /**
    * HillClimber First improvement method used to get best local solution
    *
    * @param numberElements : size of excepted solution (TODO check if it's unnecessary)
    * @param nbEval : number of evaluation asked for the HC Algorithm
    * @param arr : current solution passed
    * @return best solution
    */
  def HillClimberFirstImprovement(numberElements: Int, nbEval: Int, arr: Array[Int], eval: (Array[Int]) => Double): Array[Int] = {
    var solution = arr
    var i = 0

    val random = Random
    val inner = new Breaks

    if (solution == null) {
      solution = UtilityClass.generateRandomSolution(numberElements)
    }

    var bestResult = eval(solution)

    while (i < nbEval) {
      var result = 0.0

      inner.breakable {
        for (_ <- 0 to numberElements) {

          val firstRandomValue = random.nextInt(solution.length)
          val secondRandomValue = random.nextInt(solution.length)

          val tmpValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = tmpValue

          result = eval(solution)
          i += 1
          nbEvaluation += 1

          if (result < bestResult) {
            bestResult = result
            inner.break
          }

          solution(secondRandomValue) = solution(firstRandomValue)
          solution(firstRandomValue) = tmpValue
        }
      }
    }
    solution
  }

  /**
    * Method which used the iterated local search algorithm to find a better solution
    *
    * @param numberElements : size of excepted solution (TODO check if it's unnecessary)
    * @param iteration : number of iteration of Iterated Local Search
    * @param nbEvaluationHillClimber : number of iteration for HC Algorithm
    * @param perturbation : number of permuted cells of solution at each iteration
    * @return the best solution found
    */
  def IteratedLocalSearch(numberElements: Int, iteration: Int, nbEvaluationHillClimber: Int, perturbation: Int, eval: (Array[Int]) => Double): Array[Int] = {
    val random = Random

    var solution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, UtilityClass.generateRandomSolution(numberElements), eval)

    var bestResult = eval(solution)
    var bestSolution: Array[Int] = solution.clone()
    var i = 0
    var percentEvolution = ""

    do {
      UtilityClass.perturbationIterated(solution, perturbation, random)

      val currentSolution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, solution.clone(), eval)

      val currentResult = eval(currentSolution)

      if (currentResult < bestResult) {
        bestResult = currentResult
        bestSolution = currentSolution.clone()
        solution = bestSolution.clone()
      }

      i += 1

      val lengthText = percentEvolution.length()
      percentEvolution = "ILS -> " + df.format(i * 100.0 / iteration) + "%"
      UtilityClass.showEvolution(lengthText, percentEvolution)
    } while (i < iteration)

    bestSolution
  }

  /**
    * Method which implements the genetic evolutionary algorithm
    *
    * @param mu : number of mu solution
    * @param lambda : number of lambda solution
    * @param iteration : number of main iteration
    * @param nbEvaluationHillClimber : number of evaluation of HC Algorithm at each iteration
    * @param numberOfPermutations : number of permutations excepted to have new solution
    * @return best solution object found
    */
  def GeneticEvolutionnaryAlgorithm(numberElements: Int, mu: Int, lambda: Int, iteration: Int, nbEvaluationHillClimber: Int, numberOfHc: Int, numberOfPermutations: Int, eval: (Array[Int]) => Double): Array[Int] = {

    val rand = Random
    var percentEvolution = ""

    // Generate all parents solutions to start the algorithm
    var parentsSolutions = mutable.MutableList[Array[Int]]()

    (0 until mu).foreach(_ => parentsSolutions += UtilityClass.generateRandomSolution(numberElements))
    //println(parentsSolutions.length)

    // Loop which defined the stop search (Iteration number)
    for (i <- 0 until iteration) {
      var genitorsSolutions = mutable.MutableList[Array[Int]]()

      for (_ <- 0 until lambda) {
        val firstSelectedIndex = rand.nextInt(parentsSolutions.length - 1)
        val secondSelectedIndex = rand.nextInt(parentsSolutions.length - 1)

        if (eval(parentsSolutions(firstSelectedIndex)) >= eval(parentsSolutions(secondSelectedIndex))) {
          genitorsSolutions += parentsSolutions(firstSelectedIndex)
        } else {
          genitorsSolutions += parentsSolutions(secondSelectedIndex)
        }
      }

      // Do variations on "genitors" like mutation & HC
      // Mutation needs make probability
      for (j <- genitorsSolutions.indices) {

        // Do permutation
        UtilityClass.perturbationIterated(genitorsSolutions(j), numberOfPermutations, rand)

        // Make hill climber on the current solution to improve the genitor solution
        for (_ <- 0 until numberOfHc) {
          val currentSolution = HillClimberFirstImprovement(genitorsSolutions(j).length, nbEvaluationHillClimber,
            genitorsSolutions(j).clone(), eval)

          if (eval(currentSolution) < eval(genitorsSolutions(j))) {
            genitorsSolutions(j) = currentSolution
          }
        }
      }

      // Get the best between old parents & Children to get Survivors

      // First of all we need to add all children
      for (j <- 0 until lambda) {
        parentsSolutions += genitorsSolutions(j)
      }

      // Used to order list of solution by result
      parentsSolutions = parentsSolutions.sortWith((x, y) => eval(x) < eval(y))

      // Remove all elements without good result for the next step
      parentsSolutions = parentsSolutions.take(mu)

      val lengthText = percentEvolution.length()
      percentEvolution = "EA -> " + df.format(i * 100.0 / iteration) + "%"
      UtilityClass.showEvolution(lengthText, percentEvolution)

    }

    // Return the first element of the list
    parentsSolutions.head
  }

  /**
    * Method which used method implement pareto local search and find non determinist solution
    *
    * @param filename : file name which information will be stored
    * @param nbEval : number of evaluation
    * @param arr : array of solution passed as parameter if necessary
    * @return best solution
    */
  def ParetoLocalSearch(filename: String, numberElements: Int, nbEval: Int, arr: ListBuffer[Array[Int]], evals: Array[(Array[Int]) => Double]): ListBuffer[Array[Int]] = {

    val rand = new Random
    var percentEvolution = ""
    var numberEval: BigInt = nbEval
    var maxEval: BigInt = 0
    var solutions = arr
    var i = 0
    val inner = new Breaks

    val random = Random

    println("THERE !")
    if (solutions == null) {

      //Set header of tracking file
      UtilityClass.writeHeaderTracking(filename, evals.length)

      solutions = new ListBuffer[Array[Int]]()
      solutions += UtilityClass.generateRandomSolution(numberElements)

      maxEval = UtilityClass.factorial(numberElements)

    }else{
      maxEval = UtilityClass.factorial(numberElements) - solutionsPassed.length
    }

    println(UtilityClass.factorial(numberElements))
    println("Max number of evaluation", maxEval)


    //If user want to explore all solutions
    if (numberEval == 0)
      numberEval = maxEval
    //If numberEval if higher that all number of solutions to explore
    else if (numberEval > maxEval)
      numberEval = maxEval

    println("Number of evaluation ", numberEval, nbEval)


    inner.breakable {
      while (i < numberEval) {
        //Select a non visited solution of solutions
        var current_sol = new Array[Int](numberElements)
        do {
          val randIndex = rand.nextInt(solutions.length)
          current_sol = solutions(randIndex).clone()
          UtilityClass.perturbationIterated(current_sol, 1, rand)

        } while (!UtilityClass.checkExists(solutionsPassed, current_sol))

        // Flipping each bit of the current solution to get neighbors
        val neighbors = UtilityClass.getNeighbors(current_sol)

        neighbors.foreach(x => {
          //Add this new solutions with all old solutions
          if (!UtilityClass.checkExists(solutionsPassed, x)) {
            var sol = x.clone()
            solutionsPassed += sol
            solutions += sol

            i += 1
            nbEvaluation += 1

            //Add tracking to check algorithm performance
            UtilityClass.algorithmEvaluationTrack(filename, nbEvaluation, sol, numberElements, evals)

            //Take only non dominated solutions
            solutions = UtilityClass.getNonDominatedSolutions(solutions, evals)

            val lengthText = percentEvolution.length()
            percentEvolution = "PLS -> " + df.format(i * 100.0 / numberEval.toInt) + "% "
            UtilityClass.showEvolution(lengthText, percentEvolution)

            // Break if necessary
            if(i >= numberEval)
              inner.break
          }
        })
      }
    }
    solutions
  }

  /**
    *
    * MOEAD Algorithm implementation (PLS decomposed on sub problems)
    *
    * @param filename : file name which information will be stored
    * @param nbEval : number of evaluation for the MOEAD Algorithm excepted
    * @param N : Number of sub problems considered
    * @param T : the number of the weight vectors in the neighborhood of each weight vector.
    * @param evals : Criteria functions
    * @param choice : 0 => Tchebycheff approach || 1 => Weighted sum approach
    * @return
    */
  def MOEAD_Algorithm(filename: String, numberElements: Int, nbEval: Int, N: Int, T: Int, evals: Array[(Array[Int]) => Double], choice: Int): ListBuffer[Array[Int]] = {

    /**
      * All utilities local variables
      */
    val random = new Random
    var percentEvolution = ""

    //Set header of tracking file
    UtilityClass.writeHeaderTracking(filename, evals.length)

    /**
      * 1. Initialize of the context
      */

    //Step 1.0 : Initialization of each scalar sub problems
    val vectors = MOEADInit.generateVectors(N)

    //Step 1.1 : Initialization of EP (set to empty)
    var nonDominatedSolutions = new ListBuffer[Array[Int]]()

    //Step 1.2 : Compute distances between T closest weight vector to each weight vector
    val B = MOEADInit.getNeightborsVectors(vectors, T)

    //Step 1.3 : Initialize population (solution for each sub problems)
    val population = MOEADInit.generateRandomPopulation(numberElements, N)
    val values = MOEADInit.computeFunctionValues(population, evals)

    //Step 1.4 : Initialize reference point
    val z = MOEADInit.getRefPoint(values, evals.length)

    var evaluation = 0
    nbEvaluation = 0

    /**
      * 2. Update
      */
    do {

      (0 until N).foreach(i => {

        /**
          *  2.1 Reproduction : Select randomly two solutions to create new solution y
          *
          * For the moment we just permute randomly values of solution selected to create new solution
          */

        //2.1.1 : Getting random index of closest vectors and retrieve solution associated
        val firstIndex = B(i)(random.nextInt(B(i).length))
        //val secondIndex = B(i)(random.nextInt(B(i).length))

        val firstSol = population(firstIndex).clone()
        //var secondSol = population(secondIndex).clone()

        //2.1.1. Create new solution with the selected solutions

        UtilityClass.perturbationIterated(firstSol, 10, random)
        //UtilityClass.pertubationIterated(secondSol, 10, random)

        var newSol = firstSol

        // Solution added as shown if do not already exists
        if(!UtilityClass.checkExists(solutionsPassed, newSol)) solutionsPassed += newSol

        /**
          * 2.2 Improvement : Not developed at this time (Perhaps later)
          */


        /**
          * 2.3 Update z : reference point
          */
        //Setting new reference point if exists
        evals.indices.foreach(index => {
          z(index) = math.min(z(index), evals(index)(newSol))
        })

        /**
          * 2.4 Update of Neighboring Solutions
          */

        // For each solution into the population check if new solution is better
        B(i).indices.foreach(index => {
          val neighborIndex = B(i)(index)

          // Getting Tchebycheff function result for the new solution and current solution
          val gNeighbor = MOEADInit.computeCombinedValues(population(neighborIndex), z, vectors(neighborIndex), evals, choice)
          val gY = MOEADInit.computeCombinedValues(newSol, z, vectors(neighborIndex), evals, choice)

          // If better update population and values
          if (gY < gNeighbor) {
            population(neighborIndex) = newSol
            evals.indices.foreach(current => {
              values(neighborIndex)(current) = evals(current)(population(neighborIndex))
            })
          }
        })


        /**
          * 2.5 Update of EP : Update non dominated solutions
          */

        //Add new solution to EP
        nonDominatedSolutions += newSol

        //Add tracking to check algorithm performance
        UtilityClass.algorithmEvaluationTrack(filename, nbEvaluation, newSol, numberElements, evals)

        //Get new EP without dominated solutions (if they exist)
        nonDominatedSolutions = UtilityClass.getNonDominatedSolutions(nonDominatedSolutions, evals)

        //Increment number of evaluation
        nbEvaluation += 1
        evaluation += 1

        val lengthText = percentEvolution.length()
        percentEvolution = "MOEA/D -> " + df.format(evaluation * 100.0 / nbEval) + "% "
        UtilityClass.showEvolution(lengthText, percentEvolution)
      })

    } while (evaluation < nbEval)

    nonDominatedSolutions
  }

  /**
    *
    * TP-LS Algorithm implementation
    * Another multi-objective approach which consist to execute MOEA/D algorithm and then PLS algorithm
    *
    * The aim of the algorithm is to obtained PLS optimal front quickly
    *
    * @param filename : file name which information will be stored
    * @param nbEvalMOEAD : number of evaluation of MOEA/D algorithm
    * @param nbEvalPLS : number of iteration excepted of PLS (if 0 PLS will show all feasible solutions)
    * @param N : Number of sub problems considered
    * @param T : the number of the weight vectors in the neighborhood of each weight vector.
    * @param evals : Criteria functions
    * @param choice : 0 => Tchebycheff approach || 1 => Weighted sum approach
    * @return
    */
  def TPLS_Algorithm(filename: String, numberElements: Int, nbEvalMOEAD: Int, nbEvalPLS: Int, N: Int, T: Int, evals: Array[(Array[Int]) => Double], choice: Int): ListBuffer[Array[Int]] = {

    //Set header of tracking file
    UtilityClass.writeHeaderTracking(filename, evals.length)
    println(nbEvalMOEAD, " && ", nbEvalPLS)

    var solutions = MOEAD_Algorithm(filename, numberElements, nbEvalMOEAD, N, T, evals, choice)

    println("NUMBER SOLUTION", solutions.length)
    solutions = ParetoLocalSearch(filename, numberElements, nbEvalPLS, solutions, evals)

    solutions
  }

  /**
    *
    * Random wal algorithm mainly used for evaluating feature
    *
    * @param filename : file name which information at each iteration will be stored
    * @param numberElements : size of solution excepted
    * @param nbEval : number of evalution of the algorithm
    * @param evals : criteria functions to optimize
    * @return
    */
  def RandomWalkAlgorithm(filename: String, numberElements: Int, nbEval: Int, evals: Array[(Array[Int]) => Double]): ListBuffer[Array[Int]] = {

    /**
      * All utilities local variables
      */
    var percentEvolution = ""
    var evaluation = 0

    var spaceSearchSolutions = new ListBuffer[Array[Int]]()

    // Adding all solutions to spaceSearch
    /*UtilityClass.generateRandomSolution(numberElements).permutations.foreach( x => {
      spaceSearchSolutions += x
    })*/

    //Set header of tracking file
    UtilityClass.writeHeaderTracking(filename, evals.length)

    do {
      val solution = UtilityClass.generateRandomSolution(numberElements)

      spaceSearchSolutions += solution

      // Add tracking to check algorithm performance
      UtilityClass.algorithmEvaluationTrack(filename, evaluation, solution, numberElements, evals)

      evaluation += 1

      val lengthText = percentEvolution.length()
      percentEvolution = "Random walk -> " + df.format(evaluation * 100.0 / nbEval) + "% "
      UtilityClass.showEvolution(lengthText, percentEvolution)
    }  while(evaluation < nbEval)

    spaceSearchSolutions
  }
}
