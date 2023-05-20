package cse511

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, pointString: String ): Boolean = {
    val rectangle = queryRectangle.split(",").map(_.trim.toDouble)
    val pointCordinates = pointString.split(",").map(_.trim.toDouble)
    val lower_rectangle_x = math.min(rectangle(0), rectangle(2))
	  val upper_rectangle_x = math.max(rectangle(0), rectangle(2))
	  val upper_rectangle_y = math.max(rectangle(1), rectangle(3))
	  val lower_rectangle_y = math.min(rectangle(1), rectangle(3))
	  val point_x = pointCordinates(0)
	  val point_y = pointCordinates(1)
    
	  if(point_x > upper_rectangle_x || point_x < lower_rectangle_x || point_y > upper_rectangle_y || point_y < lower_rectangle_y) {
		  return false
	  }
    return true
  }
}