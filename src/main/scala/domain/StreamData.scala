package domain

/**
 * The object carrying information for stream 2
 */
class StreamData(slidingAverage: Double, quantTime: Long, ageOfOldestValue: Long, ageOfYoungestValue: Long) {

  def average = slidingAverage
  def quantizedTime = quantTime
  def oldestAge = ageOfOldestValue
  def youngestAge = ageOfYoungestValue
}
