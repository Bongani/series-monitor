package domain

/**
 * Object to carry time and value
 */
class TimeValueObject(capturedValue: Int, capturedTime: Long) {

  def value = capturedValue
  def time = capturedTime
}
