package domain

/**
 * The object carrying information for stream 2
 */
class StreamData(slidingAverage: Double, quantTime: Double, ageOfOldestValue: Double, ageOfYoungestValue: Double) {

  def average = slidingAverage
  def quantizedTime = quantTime
  def oldestAge = ageOfOldestValue
  def youngestAge = ageOfYoungestValue
}
