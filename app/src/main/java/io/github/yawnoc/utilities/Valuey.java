/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This file contains bytes copied from the deprecated `Keyboard` class,
  i.e. `core/java/android/inputmethodservice/Keyboard.java`
  from <https://android.googlesource.com/platform/frameworks/base>,
  which is licensed under the Apache License 2.0,
  see <https://www.apache.org/licenses/LICENSE-2.0.html>.
  ---
  Take your pick from the following out-of-date notices:
  In `core/java/android/inputmethodservice/Keyboard.java`:
    Copyright (C) 2008-2009 Google Inc.
  In `NOTICE`:
    Copyright 2005-2008 The Android Open Source Project
*/

package io.github.yawnoc.utilities;

import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public final class Valuey
{
  private Valuey()
  {
    // Do not instantiate
  }
  
  public static float clipValueToRange(final float value, final float rangeMin, final float rangeMax)
  {
    return Math.max(rangeMin, Math.min(rangeMax, value));
  }
  
  public static float pxFromDp(final float dp, final DisplayMetrics displayMetrics)
  {
    return dp * displayMetrics.density;
  }
  
  public static float pxFromSp(final float sp, final DisplayMetrics displayMetrics)
  {
    return sp * displayMetrics.scaledDensity;
  }
  
  public static int getDimensionOrFraction(
    final TypedArray array,
    final int attributeIndex,
    final int baseValue,
    final int defaultValue
  )
  {
    final TypedValue value = array.peekValue(attributeIndex);
    if (value == null)
    {
      return defaultValue;
    }
    
    switch (value.type)
    {
      case TypedValue.TYPE_DIMENSION:
        return array.getDimensionPixelOffset(attributeIndex, defaultValue);
      
      case TypedValue.TYPE_FRACTION:
        return Math.round(array.getFraction(attributeIndex, baseValue, baseValue, defaultValue));
      
      default:
        return defaultValue;
    }
  }
}
