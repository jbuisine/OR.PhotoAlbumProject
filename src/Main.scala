

import scala.util.control.Breaks

/**
 * @author j.buisine
 *
 */
object Main {

  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "data/info-photo.json"
  val pathAlbum = "data/info-album.json"
  val solFile = "fichier.sol"
  val nbPhotos = 55
  val scanner = new java.util.Scanner(System.in)

  // Choices variables

  var functionChoice: Int = _
  var algorithmChoice: Int = _

  var generationTypeChoice: Int = _
  var evaluationFile: Array[String] = _
  var criteriaChoices: Array[String] = _
  var solutionFile = ""
  var bestResults: Array[Double] = _

  val numberFunction = 9
  val numberAlgo = 3

  //Objective function
  var f: Array[(Array[Int]) => Double] = null

  val hashTypes = Array("ahashdist", "phashdist", "dhashdist")
  var hashChoice: Int = _

  //Sanner utility object
  val breaker = new Breaks

  /**
   * Main method which throws all algorithms
   * @param args
   */
  def main(args: Array[String]): Unit = {

    //File solution value 
    if(args.length > 0)
      solutionFile = args(0)

    var solution = Array[Int](nbPhotos)
    var bestSolution = new Array[Int](nbPhotos)

    evaluationFile = new Array[String](0)

    val functionQuestion =
      "Which type of objective function do you want to use ?" +
        "\n1. Hash objective function (You need later to select between aHash, pHash & dHash attributes)" +
        "\n2. Commons Tags value objective function" +
        "\n3. Uncommon Tags value objective function" +
        "\n4. Uncommon number of tag objective function" +
        "\n5. Colors objective function" +
        "\n6. Grey AVG  objective function" +
        "\n7. Grey AVG and Colors multi objective function" +
        "\n8. Grey AVG, Colors and common tags multi objective function" +
        "\n9. Colors and uncommon tags multi objective function" +
        "\n";
    functionChoice = UtilityClass.getScannerValue(functionQuestion, "function", 0, numberFunction)

    if (functionChoice == 1) {

      val hashQuestion = "Which type of attributes do you want ?" +
        "\n1. Average hash" +
        "\n2. Perspective hash" +
        "\n3. Difference hash" +
        "\n";
      hashChoice = UtilityClass.getScannerValue(hashQuestion, "hash attribute", 0, hashTypes.length)
    }

    //Initialize problem modelisation
    val hashValue = if (hashChoice.toInt > 0) hashTypes(hashChoice.toInt - 1) else ""
    Modelisation.init(pathPhoto, pathAlbum, hashValue)

    functionChoice.toInt match {
      case 1 =>
        f = Array(Modelisation.hashEval)
        criteriaChoices = Array(hashTypes(hashChoice-1))
      case 2 =>
        f = Array(Modelisation.commonTagEval)
        criteriaChoices = Array("Common tags")
      case 3 =>
        f = Array(Modelisation.uncommonTagEval)
        criteriaChoices = Array("Uncommon tags")
      case 4 =>
        f = Array(Modelisation.nbUncommonTagEval)
        criteriaChoices = Array("Number of uncommon tags")
      case 5 =>
        f = Array(Modelisation.colorsEval)
        criteriaChoices = Array("Colors")
      case 6 =>
        f = Array(Modelisation.greyAVGEval)
        criteriaChoices = Array("Grey AVG")
      case 7 =>
        f = Array(Modelisation.greyAVGEval, Modelisation.colorsEval)
        criteriaChoices = Array("GreyAVG", "Colors")
      case 8 =>
        f = Array(Modelisation.greyAVGEval, Modelisation.colorsEval, Modelisation.commonTagEval)
        criteriaChoices = Array("GreyAVG", "Colors", "Common tags")
      case 9 =>
        f = Array(Modelisation.colorsEval, Modelisation.uncommonTagEval)
        criteriaChoices = Array("Colors", "Uncommon tags")
    }

    //Init bestResult
    bestResults = new Array[Double](f.length)
    (0 until f.length).foreach( i => bestResults(i) = Double.PositiveInfinity)

    val scoreSaved = "Before starting, indicate if you want to save the result(s) score and number of evaluation."+
      "\n1. I do not want to save my scores."+
      "\n2. Let me specify my files name (Just to inform, files are saved into 'scores' folder)."
    val choiceScoreSaved = UtilityClass.getScannerValue(scoreSaved, "save choice", 0, 2)

    if(choiceScoreSaved == 2){
      evaluationFile = new Array[String](f.length)
      (0 until f.length).foreach(index => {
        do {
          println("Select the file name for " + criteriaChoices(index) + " result")
          evaluationFile(index) = scanner.nextLine()
        }while(evaluationFile(index).length > 0)
      })
    }


    val algorithmQuestion = "Which type of algorithm do you want to executes ?" +
      "\n1. Hill Climber First Improvement" +
      "\n2. Iterated Local Search" +
      "\n3. Evolutionary Algorithm" +
      "\n";
    algorithmChoice = UtilityClass.getScannerValue(algorithmQuestion, "algorithm", 0, numberAlgo)

    algorithmChoice.toInt match {
      case 1 => {

        val iterationQuestion = "Please select number of evaluation you want for you HC (between 1 and 100000)"
        val numberEvaluation = UtilityClass.getScannerValue(iterationQuestion, "number of iteration", 1, 100000)

        val repetitionQuestion = "\nBefore starting your configured HC algorithm, please indicate how many times you want to execute it. (Between 0 and 1000000)" +
                                 "\n1. If you choose to saved solution, by default best solution found of these repetitions will be saved." +
                                 "\n2. Furthermore, if you decide to save number of evaluation and score of solution found, each results are saved."

        val numberRepetition = UtilityClass.getScannerValue(repetitionQuestion, " number of repetitions", 0, 1000000)


        if(numberRepetition > 0){

          for(i <- 0 until numberRepetition){
            Algorithms.nbEvaluation = 0
            println("\n--------------------------------------------------------------------------------------------")
            println("("+(i+1)+") HC algorithm starts search one of the best solution... It will take few seconds or more...")
            println("---------------------------------------------------------------------------------------------\n")
            solution = Algorithms.HillClimberFirstImprovement(nbPhotos, numberEvaluation, null, f)
            println("\n("+(i+1)+") HC better score found :")
            (0 until f.length).foreach(index => println("- " + criteriaChoices(index) + " score => " +f(index)(solution) +"\n"))

            var checkSolution = false

            (0 until f.length).foreach(index => {
              if(f(index)(solution) < bestResults(index))
                checkSolution = true
              else
                checkSolution = false
            })

            if (checkSolution) {
              bestSolution = solution
              //For each functions we saved the current result
              (0 until f.length).foreach(index => bestResults(index) = f(index)(solution))
            }

            if(evaluationFile.length > 0) {
              (0 until f.length).foreach(index => {
                UtilityClass.writeEvaluation(evaluationFile(index), Algorithms.nbEvaluation, f(index)(solution), solution)
              })
            }
          }
        }
        else {
          println("\n------------------------------------------------------------------------------------------")
          println("HC algorithm starts search one of the best solution... It will take few seconds or more...")
          println("------------------------------------------------------------------------------------------\n")
          solution = Algorithms.HillClimberFirstImprovement(nbPhotos, numberEvaluation, null, f)
          bestSolution = solution

          if (evaluationFile.length > 0)
            UtilityClass.writeEvaluation(evaluationFile(0), Algorithms.nbEvaluation, f(0)(solution), solution)
          }

        println("\nHC best score found :")
        (0 until f.length).foreach(index => {
          println("- " + criteriaChoices(index) + " score => " + f(index)(bestSolution))
        })

      }
      case 2 => {
        val ilsQuestion = "This algorithm need some paramaters : " +
          "\n1. Number of iteration for ILS (between 1 and 100000)" +
          "\n2. Number of evaluation for all HC (between 1 and 100000)" +
          "\n3. Number of maximum elements you want to permute for each solution (between 1 and " + nbPhotos + ")" +
          "\n\n"
        println(ilsQuestion)

        val iterationQuestion = "1. So, please select number of iteration for ILS"
        val numberIteration = UtilityClass.getScannerValue(iterationQuestion, "number of iteration", 1, 100000)

        val evaluationQuestion = "2. Select number of evaluation for all HC"
        val numberEvaluation = UtilityClass.getScannerValue(evaluationQuestion, "number of evaluation", 1, 100000)

        val permutationQuestion = "3. Select number of maximum elements permuted for each solution"
        val numberPermutation = UtilityClass.getScannerValue(permutationQuestion, "number of permutation", 1, nbPhotos)


        val repetitionQuestion = "\nBefore starting your configured ILS algorithm, please indicate how many times you want to execute it. (Between 0 and 1000000)" +
                                 "\n1. If you choose to saved solution, by default best solution found of these repetitions will be saved." +
                                 "\n2. Futhermore, if you decide to save number of evaluation and score of solution found, each results are saved."

        val numberRepetition = UtilityClass.getScannerValue(repetitionQuestion, " number of repetitions", 0, 1000000)

        if(numberRepetition > 0){

          for(i <- 0 until numberRepetition){
            Algorithms.nbEvaluation = 0
            println("\n------------------------------------------------------------------------------------------")
            println("("+(i+1)+") ILS algorithm starts search one of the best solution... It will take few minutes")
            println("------------------------------------------------------------------------------------------\n")
            solution = Algorithms.IteratedLocalSearch(nbPhotos, numberIteration, numberEvaluation, numberPermutation + 1, f)

            println("\n("+(i+1)+") ILS better score found : \n" )
            (0 until f.length).foreach(index => println("- "+ criteriaChoices(index) + " score => " +f(index)(solution) +"\n"))

            var checkSolution = false

            (0 until f.length).foreach(index => {
              if(f(index)(solution) < bestResults(index))
                checkSolution = true
              else
                checkSolution = false
            })

            if (checkSolution) {
              bestSolution = solution
              //For each functions we saved the current result
              (0 until f.length).foreach(index => bestResults(index) = f(index)(solution))
            }

            if(evaluationFile.length > 0) {
              (0 until f.length).foreach(index => {
                UtilityClass.writeEvaluation(evaluationFile(index), Algorithms.nbEvaluation, f(index)(solution), solution)
              })
            }
          }
        }
        else {
          println("\n------------------------------------------------------------------------------------------")
          println("ILS algorithm starts search one of the best solution... It will take few minutes")
          println("------------------------------------------------------------------------------------------\n")
          solution = Algorithms.IteratedLocalSearch(nbPhotos, numberIteration, numberEvaluation, numberPermutation + 1, f)
          bestSolution = solution

          if (evaluationFile.length > 0)
            UtilityClass.writeEvaluation(evaluationFile(0), Algorithms.nbEvaluation, f(0)(solution), solution)
        }

        println("\nILS best score found \n")
        (0 until f.length).foreach(index => {
          println("- " + criteriaChoices(index) + " score => " + f(index)(bestSolution))
        })
      }
      case 3 => {

        val eaQuestion = "This algorithm need some paramaters : " +
          "\n1. Number of mu (parents) elements (between 1 and 1000)" +
          "\n2. Number of lambda (children) elements (between 1 and 1000)" +
          "\n3. Number of iteration for EA algorithm (between 1 and 100000) " +
          "\n4. Number of evaluation for each HC (between 1 and 100000)" +
          "\n5. Number of HC you want to do for each genitors (same number of lambda) solutions (between 1 and 100000)" +
          "\n6. Number of maximum elements you want to permute for each solution (between 1 and " + nbPhotos + ")" +
          "\n\n"
        println(eaQuestion)

        val muQuestion = "1. So, please select number of mu elements"
        val mu = UtilityClass.getScannerValue(muQuestion, "number of mu", 1, 1000)

        val lambdaQuestion = "2. Select number of lambda elements"
        val lambda = UtilityClass.getScannerValue(lambdaQuestion, "number of lambda", 1, 1000)

        val iterationQuestion = "3. Select number of iteration you want for EA"
        val numberIteration = UtilityClass.getScannerValue(iterationQuestion, "number of iteration", 1, 100000)

        val evaluationQuestion = "4. Select number of evaluation for all HC"
        val numberEvaluation = UtilityClass.getScannerValue(evaluationQuestion, "number of evaluation", 1, 100000)

        val hcQuestion = "5. Select number of HC"
        val hcNumber = UtilityClass.getScannerValue(hcQuestion, "number of HC", 1, 100000)

        val permutationQuestion = "6. Select number of maximum elements permuted for each solution"
        val numberPermutation = UtilityClass.getScannerValue(permutationQuestion, "number of elements to permute", 1, nbPhotos)

        val repetitionQuestion = "\nBefore starting your configured EA algorithm, please indicate how many times you want to execute it. (Between 0 and 1000000)" +
                                 "\n1. If you choose to saved solution, by default best solution found of these repetitions will be saved." +
                                 "\n2. Futhermore, if you decide to save number of evaluation and score of solution found, each results are saved."

        val numberRepetition = UtilityClass.getScannerValue(repetitionQuestion, " number of repetitions", 0, 1000000)


        if(numberRepetition > 0){

          for(i <- 0 until numberRepetition){
            Algorithms.nbEvaluation = 0
            println("\n--------------------------------------------------------------------------------------------")
            println("("+(i+1)+") EA algorithm starts search one of the best solution... It will take few minutes or more...")
            println("---------------------------------------------------------------------------------------------\n")
            solution = Algorithms.GeneticEvolutionnaryAlgorithm(mu, lambda, nbPhotos, numberIteration, numberEvaluation, hcNumber, numberPermutation, f);

            println("\n("+(i+1)+") EA better score found : \n " )
            (0 until f.length).foreach(index => println("- "+ criteriaChoices(index) + " score => " +f(index)(solution) +"\n"))

            var checkSolution = false

            (0 until f.length).foreach(index => {
              if(f(index)(solution) < bestResults(index))
                checkSolution = true
              else
                checkSolution = false
            })

            if (checkSolution) {
              bestSolution = solution
              //For each functions we saved the current result
              (0 until f.length).foreach(index => bestResults(index) = f(index)(solution))
            }

            if(evaluationFile.length > 0) {
              (0 until f.length).foreach(index => {
                UtilityClass.writeEvaluation(evaluationFile(index), Algorithms.nbEvaluation, f(index)(solution), solution)
              })
            }
          }
        }
        else {
          println("\n------------------------------------------------------------------------------------------")
          println("EA algorithm starts search one of the best solution... It will take few minutes or more...")
          println("------------------------------------------------------------------------------------------\n")
          solution = Algorithms.GeneticEvolutionnaryAlgorithm(mu, lambda, nbPhotos, numberIteration, numberEvaluation, hcNumber, numberPermutation, f);
          bestSolution = solution

          if (evaluationFile.length > 0)
            UtilityClass.writeEvaluation(evaluationFile(0), Algorithms.nbEvaluation, f(0) (solution), solution)
        }

        println("\nEA best score found \n")
        (0 until f.length).foreach(index => {
          println("- " + criteriaChoices(index) + " score  => " + f(index)(bestSolution))
        })
      }
    }
    if(solutionFile.length() > 0)
      UtilityClass.writeSolution(solutionFile, bestSolution)
  }
}
