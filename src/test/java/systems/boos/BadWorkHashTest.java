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
  void codewarsExampleTestCases() {
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

  /**
   * Breaking the hash: Fill with 'a' until 32 characters reached. Then add numbers > 99000 to the end to produce the
   * desired hash
   * <p>
   * To break the hash means to counter its purpose. Some purposes of a hash are:
   * <p>
   * Prevent password disclosure
   * <p>
   * A one way function shall be used to hash passwords. It shall be easy to calculate the hash, but very hard to
   * construct a value that results in the hash.
   * <p>
   * Map from an input to a numeric key in a lookup table (hash table)
   * <p>
   * The hash shall evenly distribute input values to numbers serving as array indexes for lookup tables. The hash
   * function must be fast and collisions shall be avoided.
   * <p>
   * From these two scenarios, there the following weaknesses are possible:
   * <p>
   * - construct an input value, which results in a given hash, i.e. calculate a valid password
   * - construct collisions of the hash function, i.e. calculate a large number of ijnputs which produce the same
   *   output.
   * <p>
   * Seeing the given B.A.D. hash function, both is quite simple (see tests). All operations of the hash function
   * can be reversed easily.
   */
  @Test
  void breakHash() {
    var password = "SomeFancyPassword";
    var hash = BadWorkHash.hashString(password);
    assertEquals("323456789ABCDEF01253AEDABB9AAA33", hash);

    // simple way of producing a collision: just shuffle the input characters
    var collision = "PasswordSomeFancy";
    assertEquals(hash, BadWorkHash.hashString(collision));
    /*
      Forge a collision from a known hash

      Desired hash:
      323456789ABCDEF01253AEDABB9AAA33 = desired hash
      hex:
      [   3,   2,   3,   4,   5,   6,   7,   8,   9,   A,   B,   C,   D,   E,   F,   0,   1,   2,   5,   3,   A,   E,   D,   A,   B,   B,   9,   A,   A,   A,   3,   3 ]
      decimal:
      [   3,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12,  13,  14,  15,   0,   1,   2,   5,   3,  10,  14,  13,  10,  11,  11,   9,  10,  10,  10,   3,   3 ]

      Indices:
                                                            1                                                 2                                                 3
          0    1    2    3    4    5    6    7    8    9    0    1    2    3    4    5    6    7    8    9    0    1    2    3    4    5    6    7    8    9    0    1

      Subtract indices; If the result gets < 0, then add 16 until result is >= 0
      [   3,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   3,   0,   6,   9,   7,   3,   3,   2,  15,  15,  14,  13,   5,   4 ]
      Convert to an ASCII character by adding 'A' (=65)
      [  68,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  66,  68,  65,  71,  74,  72,  68,  68,  67,  80,  80,  79,  78,  70,  69 ]
      Fix sorting by starting from the back and adding 16 until the array is sorted descending
      ... better do that programmatically
      Collision:
     */
  }
}
