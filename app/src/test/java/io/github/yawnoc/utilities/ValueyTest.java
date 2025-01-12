/*
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ValueyTest
{
  private static final float FLOAT_ASSERTION_DELTA = 0f;

  @Test
  public void clipValueToRange_isCorrect()
  {
    final List<Float> LESS_THAN_ZERO_VALUES = Arrays.asList(-Float.MAX_VALUE, -10f, -1f, -0.5f, -Float.MIN_VALUE, -0f);
    final List<Float> ZERO_TO_ONE_VALUES = Arrays.asList(0f, +0f, Float.MIN_VALUE, 0.7f, 1f - Float.MIN_VALUE, 1f);
    final List<Float> GREATER_THAN_ONE_VALUES = Arrays.asList(1 + Float.MIN_VALUE, 2f, 10000f, Float.MAX_VALUE);

    for (final float value : LESS_THAN_ZERO_VALUES)
    {
      assertEquals(Valuey.clipValueToRange(value, 0f, 1f), 0f, FLOAT_ASSERTION_DELTA);
    }
    for (final float value : ZERO_TO_ONE_VALUES)
    {
      assertEquals(Valuey.clipValueToRange(value, 0f, 1f), value, FLOAT_ASSERTION_DELTA);
    }
    for (final float value : GREATER_THAN_ONE_VALUES)
    {
      assertEquals(Valuey.clipValueToRange(value, 0f, 1f), 1f, FLOAT_ASSERTION_DELTA);
    }

    assertEquals(Valuey.clipValueToRange(3f, 4f, 6f), 4f, FLOAT_ASSERTION_DELTA);
    assertEquals(Valuey.clipValueToRange(5f, 4f, 6f), 5f, FLOAT_ASSERTION_DELTA);
    assertEquals(Valuey.clipValueToRange(7f, 4f, 6f), 6f, FLOAT_ASSERTION_DELTA);
  }
}
