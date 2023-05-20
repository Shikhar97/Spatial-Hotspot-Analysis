package cse511

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._

object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

  def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame = {
    // Load the original data from a data source
    var pickupInfo = spark.read.format("com.databricks.spark.csv").option("delimiter", ";").option("header", "false").load(pointPath);
    pickupInfo.createOrReplaceTempView("nyctaxitrips")
    pickupInfo.show()

    // Assign cell coordinates based on pickup points
    spark.udf.register("CalculateX", (pickupPoint: String) => ((
      HotcellUtils.CalculateCoordinate(pickupPoint, 0)
      )))
    spark.udf.register("CalculateY", (pickupPoint: String) => ((
      HotcellUtils.CalculateCoordinate(pickupPoint, 1)
      )))
    spark.udf.register("CalculateZ", (pickupTime: String) => ((
      HotcellUtils.CalculateCoordinate(pickupTime, 2)
      )))
    pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
    var newCoordinateName = Seq("x", "y", "z")
    pickupInfo = pickupInfo.toDF(newCoordinateName: _*)
    pickupInfo.show()

    // Define the min and max of x, y, z
    val minX = -74.50 / HotcellUtils.coordinateStep
    val maxX = -73.70 / HotcellUtils.coordinateStep
    val minY = 40.50 / HotcellUtils.coordinateStep
    val maxY = 40.90 / HotcellUtils.coordinateStep
    val minZ = 1
    val maxZ = 31
    val numCells = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)

    // YOU NEED TO CHANGE THIS PART

    // Cap the data between the minimum and maximum range for latitude and longitude
    pickupInfo.createOrReplaceTempView("pickupInfo")
    pickupInfo = spark.sql("select x,y,z from pickupInfo where x>= " + minX + " and x<= " + maxX + " and y>= " + minY + " and y<= " + maxY)

    //Count the number of trips in each cell
    var clusters = pickupInfo.groupBy(pickupInfo("x"), pickupInfo("y"), pickupInfo("z")).count()
    clusters.createOrReplaceTempView("clusters")

    //Calculates the mean and standard deviation for the data
    val sq_sum = clusters.agg(sum(clusters("count")).as("sum"), sum(clusters("count") * clusters("count").as("squaredsum"))).head()
    val trips = sq_sum.getLong(0)
    val squaredSum = sq_sum.getLong(1)
    val mean_val = trips / numCells
    val S = Math.sqrt((squaredSum / numCells) - (mean_val * mean_val))
    System.out.println("Mean: " + mean_val + " S: " + S + " num: " + numCells)


    //Register the functions for checking neighbors and counting neighbors
    spark.udf.register("Is_Neighbor", (x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) => (HotcellUtils.isNeighbor(x1, y1, z1, x2, y2, z2)))
    spark.udf.register("CountNeighbors", (x: Int, y: Int, z: Int) => (HotcellUtils.countNeighbors(x, y, z, minX.toInt, minY.toInt, minZ.toInt, maxX.toInt, maxY.toInt, maxZ.toInt)))

    //Count the number of neighbors for each cell
    val cells = clusters.select(clusters("x"), clusters("y"), clusters("z"))
    cells.createOrReplaceTempView("cells")
    val neighborCount = spark.sql("select x, y, z, CountNeighbors(x, y, z) as w from cells").toDF()
    neighborCount.where("x=-7399 and y=4075 and z=15").show()

    //Sums the xj from it's neighbors for each ith cell
    val countX = cells.as("a").join(clusters.as("b")).where("Is_Neighbor(a.x, a.y, a.z, b.x, b.y, b.z)")
      .select(col("a.x").as("x"),
        col("a.y").as("y"),
        col("a.z").as("z"),
        col("b.count").as("count")).groupBy("x", "y", "z").agg(sum("count").as("count"))
    countX.show()
    //Calculates the numerator value of the score for cells
    val num = countX.as("c").join(neighborCount.as("n")).where("c.x = n.x and c.y = n.y and c.z = n.z")
      .select(countX("x").as("x"),
        countX("y").as("y"),
        countX("z").as("z"),
        (countX("count") - lit(mean_val) * neighborCount("w")).as("num"))
    //    num.show()

    //Calculates the denominator value of the score for cells
    val den = neighborCount.select(neighborCount("x").as("x"),
      neighborCount("y").as("y"),
      neighborCount("z").as("z"),
      (lit(S) * sqrt((lit(numCells) * neighborCount("w") - neighborCount("w") * neighborCount("w")) / lit(numCells - 1))).as("den"))
    //den.where("x=-7399 and y=4075 and z=15").show()

    //Calculates the final scores and generates a view
    val scores = num.join(den, num("x") === den("x") and num("y") === den("y") and num("z") === den("z")).select(num("x"), num("y"), num("z"), (num("num") / den("den")).as("score")).orderBy(desc("score"))
    scores.show(50)
    scores.createOrReplaceTempView("scores")
    val result = spark.sql("select x, y, z from scores limit 50")

    return result
  }
}