/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import android.content.res.TypedArray;
import android.util.TypedValue;

public class Valuey {
  
  public static int getDimensionOrFraction(
    final TypedArray array,
    final int attributeIndex,
    final int baseValue,
    final int defaultValue
  )
  {
    final TypedValue value = array.peekValue(attributeIndex);
    if (value == null) {
      return defaultValue;
    }
    switch (value.type) {
      case TypedValue.TYPE_DIMENSION:
        return array.getDimensionPixelOffset(attributeIndex, defaultValue);
      case TypedValue.TYPE_FRACTION:
        return Math.round(
          array.getFraction(attributeIndex, baseValue, baseValue, defaultValue)
        );
      default:
        return defaultValue;
    }
  }
  
}
