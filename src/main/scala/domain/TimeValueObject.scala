package domain

/**
 * Object to carry time and value
 */
class TimeValueObject(capturedTime: Long, capturedValue: Int) {

  def time = capturedTime
  def value = capturedValue
}
