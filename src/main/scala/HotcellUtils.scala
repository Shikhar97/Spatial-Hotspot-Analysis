package cse511

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

object HotcellUtils {
  val coordinateStep = 0.01

  def CalculateCoordinate(inputString: String, coordinateOffset: Int): Int =
  {
    // Configuration variable:
    // Coordinate step is the size of each cell on x and y
    var result = 0
    coordinateOffset match
    {
      case 0 => result = Math.floor((inputString.split(",")(0).replace("(","").toDouble/coordinateStep)).toInt
      case 1 => result = Math.floor(inputString.split(",")(1).replace(")","").toDouble/coordinateStep).toInt
      // We only consider the data from 2009 to 2012 inclusively, 4 years in total. Week 0 Day 0 is 2009-01-01
      case 2 => {
        val timestamp = HotcellUtils.timestampParser(inputString)
        result = HotcellUtils.dayOfMonth(timestamp) // Assume every month has 31 days
      }
    }
    return result
  }

  def timestampParser (timestampString: String): Timestamp =
  {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val parsedDate = dateFormat.parse(timestampString)
    val timeStamp = new Timestamp(parsedDate.getTime)
    return timeStamp
  }

  def dayOfYear (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_YEAR)
  }

  def dayOfMonth (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_MONTH)
  }

  // YOU NEED TO CHANGE THIS PART
  def isNeighbor(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Boolean = {
    if (Math.abs(x2 - x1) <= 1 && Math.abs(y2 - y1) <= 1 && Math.abs(z2 - z1) <= 1) {
      return true
    }
    return false
  }

  def countNeighbors(x: Int, y: Int, z: Int, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Int = {
    var counter = 0
    if (x == x1 || x == x2)
      counter += 1
    if (y == y1 || y == y2)
      counter += 1
    if (z == z1 || z == z2)
      counter += 1

    if (counter == 0)
      return 27
    else if (counter == 1)
      return 18
    else if (counter == 2)
      return 12
    else
      return 8
  }
  
}
