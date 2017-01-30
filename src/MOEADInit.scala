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
  def generateDirections(N: Int): Array[Array[Double]] = {

    val directions = new Array[Array[Double]](N)(2)

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
    val distances = new Array[Array[Double]](N)(N)

    //Initialize matrix distances with Euclidean distances method for each vector
    (0 until distances.length).foreach( i => {
      (0 until distances.length).foreach( j => {
        val distance = math.sqrt(math.pow(vectors(i)(0) - vectors(j)(0), 2) + math.pow(vectors(i)(1), vectors(j)(1)))
        distances(i)(j) = distance
        distances(j)(i) = distance
      })
    })

    //Return variable which just return the closest vectors for each vectors
    val closests = new Array[Array[Int]](N)(T)

    (0 until closests.length).foreach( index => {
      //Get the T indices sorted
      closests(index) = distances(index).indices.sorted.toArray.slice(0, T)
    })

    closests
  }


  /**
    * Method which generate population for each sub problems of MOEA/D algorithm
    *
    * @param N : Number of sub problems
    * @param genomeSize : Number of genome for each sub problems
    * @return random populations
    */
  def generateRandomPopulation(N: Int, genomeSize: Int): Array[Array[Array[Int]]] = {

    val populations = new Array[Array[Array[Int]]](N)(genomeSize)(Main.nbPhotos)
    (0 until N).foreach(i => {
      (0 until genomeSize).foreach( j => {
        populations(i)(j) = UtilityClass.generateRandomSolution(Main.nbPhotos)
      })
    })

    populations
  }
}
