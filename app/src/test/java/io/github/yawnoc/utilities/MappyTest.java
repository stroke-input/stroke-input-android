/*
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MappyTest
{
  @Test
  public void invertMap_isCorrect()
  {
    final Map<Integer, String> forwardMap = new HashMap<>();
    forwardMap.put(1, "one");
    forwardMap.put(2, "two");
    forwardMap.put(3, "three");

    final Map<String, Integer> inverseMap = new HashMap<>();
    inverseMap.put("one", 1);
    inverseMap.put("two", 2);
    inverseMap.put("three", 3);

    assertEquals(Mappy.invertMap(forwardMap), inverseMap);
    assertEquals(Mappy.invertMap(inverseMap), forwardMap);
  }
}
