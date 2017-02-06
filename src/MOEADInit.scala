import scala.collection.immutable.ListMap
/**
  * Created by jbuisine on 30/01/17.
  *
  * This object contains some support methods used for initialize MOEA/D Algorithm
  */
object MOEADInit {

  /**
    * Method which created to initialize directions for each sub problems
    *
    * @param N : Number of directions
    * @return the vectors generated
    */
  def generateVectors(N: Int): Array[Array[Double]] = {

    val directions = new Array[Array[Double]](N)

    //Init vectors direction
    (0 until N).foreach( index => {
      directions(index) = new Array[Double](2)
      directions(index)(0) = Math.cos(index * Math.PI / (2*(N-1)))
      directions(index)(1) = Math.sin(index * Math.PI / (2*(N-1)))
    })

    directions
  }

  /**
    * Method which returns the T closest vectors of each vectors
    *
    * @param vectors : All vectors computed previously
    * @param T : Number of closest vectors considered
    * @return B : Matrix with indications of closest vectors for each vectors
    */
  def getNeightborsVectors(vectors: Array[Array[Double]], T: Int): Array[Array[Int]] = {

    //Keep number of sub problems in new variable (Just to keep code clean)
    val N = vectors.length

    //Variable which stock matrix with distances between each vectors
    val distances = new Array[ListMap[Int, Double]](N)

    //Init list map of each item
    (0 until distances.length).foreach(index => distances(index) = new ListMap[Int, Double])

    //Initialize matrix distances with Euclidean distances method for each vector
    (0 until distances.length).foreach( i => {
      (0 until distances.length).foreach( j => {
        val distance = math.sqrt(math.pow(vectors(i)(0) - vectors(j)(0), 2) + math.pow(vectors(i)(1) - vectors(j)(1), 2))
        distances(i) += j -> distance
        distances(j) += j -> distance
      })
    })

    //Return variable which just return the closest vectors for each vectors
    val closests = new Array[Array[Int]](N)

    (0 until closests.length).foreach( index => {
      //Get the T indices sorted
      closests(index) = distances(index).toSeq.sortBy(_._2).unzip._1.slice(0, T).toArray
    })

    closests
  }


  /**
    * Method which generate solution for each sub problems of MOEA/D algorithm
    *
    * @param N : Number of sub problems
    * @return random population
    */
  def generateRandomPopulation(N: Int): Array[Array[Int]] = {

    val population = new Array[Array[Int]](N)
    (0 until N).foreach(i => {
        population(i)= UtilityClass.generateRandomSolution(Main.nbPhotos)
    })

    population
  }

  /**
    * Method which compute values for each solution of the population
    *
    * @param population : Set of solution
    * @param f : Functions used for multi objective problem
    * @return values obtained by each solution of population
    */
  def computeFunctionValues(population: Array[Array[Int]], f : Array[(Array[Int]) => Double]): Array[Array[Double]] = {
    val values = new Array[Array[Double]](population.length)

    //Compute for each solution of population its scores obtained with each function
    (0 until population.length).foreach( i => {
      values(i) = new Array[Double](f.length)
      (0 until f.length).foreach( j => values(i)(j) = f(j)(population(i)))
    })

    values
  }

  /**
    * Method used for create reference point
    *
    * @param v : function values of the set of solution (population)
    * @param nbAxes : number of function
    * @return reference point
    */
  def getRefPoint(v: Array[Array[Double]], nbAxes: Int): Array[Double] = {
    val point = new Array[Double](nbAxes)

    //Compute point with each max values of each axes (each function)
    (0 until point.length).foreach( i => {
       point(i) = v(i).min
    })

    point
  }

  /**
    *  Computed method
    *
    * @param y : new solution of the iteration
    * @param z : reference point
    * @param vector : current vector used
    * @param f : all functions of the multi objective problem
    * @param choice : choice between Tchebivech or weigthed function
    * @return min computed value
    */
  def computeCombinedValues(y: Array[Int], z: Array[Double], vector: Array[Double], f : Array[(Array[Int]) => Double], choice: Int): Double = {
    val values = new Array[Double](f.length)
    (0 until f.length).foreach( i => {
      if(choice == 0)
        values(i) = vector(i) * math.abs(f(i)(y) - z(i))
      else
        values(i) = vector(i) * f(i)(y)
    })

    if(choice == 0)
      values.min
    else
      values.sum
  }
}
