package systems.boos;

import com.codepoetics.protonpack.StreamUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BadWorkHash {

  private BadWorkHash() {}

  public static String hashString(String toHash) {
    // pad with "a" until length is 32 characters
    var padding = "a".repeat(Math.max(0, 32 - toHash.length()));
    var padded = toHash + padding;

    var truncated = padded.substring(0, 32);

    // convert to list of ascii codes, sort it descending
    List<Integer> ascii = truncated
      .chars()
      .boxed()
      .collect(Collectors.toList());

    // Convert the values to strings before sorting
    List<String> valueStrings = ascii
      .stream()
      .map(Object::toString)
      .collect(Collectors.toList());

    // Sort the list lexicographically descending by the values
    Collections.sort(valueStrings);
    Collections.reverse(valueStrings);

    // add the index of each element to the element itself
    List<Long> withIndexAdded = StreamUtils.zipWithIndex(valueStrings.stream())
      .map(x -> x.getIndex() + Integer.parseInt(x.getValue()))
      .toList();

    // Project to hexadecimal numbers and convert to string
    String result = withIndexAdded
      .stream()
      .map(x -> x % 16)
      .map(x -> (char) (x < 10 ? '0' + x : 'A' + x - 10))
      .map(Object::toString)
      .reduce("", (acc, ch) -> acc + ch);

    return result;
  }
}
