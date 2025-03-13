package systems.boos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BadWorkHashTest {

  @Test
  void hashString_1LetterA() {
    assertEquals(
      "123456789ABCDEF0123456789ABCDEF0",
      BadWorkHash.hashString("a")
    );
  }

  @Test
  void hashString_1LetterB() {
    assertEquals(
      "223456789ABCDEF0123456789ABCDEF0",
      BadWorkHash.hashString("b")
    );
  }

  @Test
  void hashString_32LettersAllMappingTo0() {
    // "ABCDEFGHIJKLMNOPQRSTUVWXYZdefghi"                       = input
    // ..................................                       = ascii
    // "ZYX.............C|.B|.A|...i|...h|...g|...f|...e|...d"  = lexicographically sorted, reversed
    // 90, 89, 87, ...... 64,65, 105, 104, 103, 102, 101, 100   = ascii
    // 90, 90, 90, ....., 90, 90, 131, 130, ...                += index
    // 10, ...                  ,   3, ...                     %= 16
    //  A,  A, ...             A,   3, ...                      = converted to hex
    assertEquals(
      "AAAAAAAAAAAAAAAAAAAAAAAAAA333333",
      BadWorkHash.hashString("ABCDEFGHIJKLMNOPQRSTUVWXYZdefghi")
    );
  }

  @Test
  void hashString_33Letters_lastGetsRemoved() {
    assertEquals(
      "AAAAAAAAAAAAAAAAAAAAAAAAAA333333",
      BadWorkHash.hashString("ABCDEFGHIJKLMNOPQRSTUVWXYZdefghiZ")
    );
  }

  @Test
  void exampleTestCases() {
    /*
                                                                1                                                 2                                                 3
                   1    2    3    4    5    6    7    8    9    0    1    2    3    4    5    6    7    8    9    0    1    2    3    4    5    6    7    8    9    0    1    2
               "   a    p    p    l    e         j    u    i    c    e"
               [  97, 112, 112, 108, 101,  32, 106, 117, 105,  99, 101,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97 ]
               [  99,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  97,  32, 117, 112, 112, 108, 106, 105, 101, 101 ]

                                                                     1                                                 2                                                 3
                   0    1    2    3    4    5    6    7    8    9    0    1    2    3    4    5    6    7    8    9    0    1    2    3    4    5    6    7    8    9    0    1
               [  99,  98,  99, 100, 101, 102, 103, 104, 105, 106, 107,  ..............................................., 118, 119,  55, 131, ....................136, 131, 132 ]
               [   3,   2,   3, ........................................................................................,   6,   7,   7,   3, ......................8,   3,   4]

               ...
       */
    assertEquals(
      "323456789ABCDEF012345677D9A76634",
      BadWorkHash.hashString("apple juice")
    );
    assertEquals(
      "22334567893453234456645544344434",
      BadWorkHash.hashString("this is a very long test okay bye wow cya")
    );
  }
}
