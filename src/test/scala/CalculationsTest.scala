import domain.{Calculations, TimeValueObject}
import org.scalatest.FunSuite

class CalculationsTest extends FunSuite{

  var dataList = List(new TimeValueObject(5, 9),
    new TimeValueObject(2, 12),
    new TimeValueObject(6, 13),
    new TimeValueObject(7, 15),
    new TimeValueObject(2, 18))

  test("quantized time should be 32"){
    assert(Calculations.quantizedTime(dataList) == 32)
  }
}
