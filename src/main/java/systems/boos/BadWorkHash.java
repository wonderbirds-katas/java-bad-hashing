package systems.boos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BadWorkHash {

  private BadWorkHash() {}

  public static String hashString(String toHash) {
    // Pad with "a" until length is 32 characters
    var padding = "a".repeat(Math.max(0, 32 - toHash.length()));
    var padded = toHash + padding;

    // Remove characters after the 32nd
    var truncated = padded.substring(0, 32);

    // Convert to list of ascii codes, sort it descending
    Stream<Integer> ascii = truncated.chars().boxed();

    // Convert the values to mutable list of strings
    List<String> valueStrings = ascii
      .map(Object::toString)
      .collect(Collectors.toList());

    // Sort the list lexicographically descending by the value strings
    Collections.sort(valueStrings);
    Collections.reverse(valueStrings);

    // Add the index of each element to the element itself
    List<Integer> withIndexAdded = new LinkedList<>();
    for (int i = 0; i < valueStrings.size(); i++) {
      int indexPlusValue = i + Integer.parseInt(valueStrings.get(i));
      withIndexAdded.add(indexPlusValue);
    }

    // Project to hexadecimal characters and convert to string
    return withIndexAdded
      .stream()
      .map(x -> x % 16)
      .map(x -> (char) (x < 10 ? '0' + x : 'A' + x - 10))
      .map(Object::toString)
      .reduce("", (acc, ch) -> acc + ch);
  }
}
