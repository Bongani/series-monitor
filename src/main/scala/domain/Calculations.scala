package domain

object Calculations {

  def minimumTime(dataList: List[TimeValueObject]): Long ={
    return dataList.head.time
  }

  def maximumTime(dataList: List[TimeValueObject]): Long ={
    return dataList.last.time
  }

  def slideAverage(dataList: List[TimeValueObject]): Double ={
    return (dataList.map(_.value).sum / dataList.size).toDouble
  }

  def quantizedTime(dataList: List[TimeValueObject]): Long ={
    if (dataList.isEmpty || dataList.size == 1)
      return 0
    else if (dataList.size == 2)
      return quantizedCalc(dataList(0), dataList(1))
    else
      return quantizedCalc(dataList(0), dataList(1)) + quantizedTime(dataList.tail)
  }

  private def quantizedCalc(o1: TimeValueObject, o2: TimeValueObject): Long={
    return (o2.time - o1.time) * o2.value
  }
}
