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

package io.github.yawnoc.strokeinput;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Xml;

import static io.github.yawnoc.Utilities.getDimensionOrFraction;

/*
  An individual key.
*/
public class Key {
  
  // Key behaviour
  public boolean isLongPressable;
  public boolean isRepeatable; // overrides isLongPressable
  public boolean isSwipeable;
  public boolean isShiftable;
  public boolean isExtendedLeft;
  public boolean isExtendedRight;
  public String valueText;
  public String displayText; // overrides valueText drawn
  public String valueTextShifted; // overrides displayText drawn when shifted
  
  // Key dimensions
  public int width;
  public int height;
  
  // Key styles
  public int keyFillColour;
  public int keyBorderColour;
  public int keyBorderThickness;
  public int keyTextColour;
  public int keyTextSwipeColour;
  public int keyTextSize;
  public int keyTextOffsetX;
  public int keyTextOffsetY;
  
  // Key position
  public int x;
  public int y;
  
  // Key meta-properties
  private final Keyboard grandparentKeyboard;
  
  public Key(final Row parentRow) {
    grandparentKeyboard = parentRow.parentKeyboard;
    width = parentRow.keyWidth;
    height = parentRow.keyHeight;
  }
  
  public Key(
    final Row parentRow,
    final int x,
    final int y,
    final Resources resources,
    final XmlResourceParser xmlResourceParser
  )
  {
    this(parentRow);
    
    this.x = x;
    this.y = y;
    
    final TypedArray attributesArray =
      resources.obtainAttributes(
        Xml.asAttributeSet(xmlResourceParser),
        R.styleable.Key
      );
    
    isLongPressable =
      attributesArray.getBoolean(R.styleable.Key_keyIsLongPressable, false);
    isRepeatable =
      attributesArray.getBoolean(R.styleable.Key_isRepeatable, false);
    isSwipeable =
      attributesArray.getBoolean(R.styleable.Key_isSwipeable, false);
    isShiftable =
      attributesArray.getBoolean(
        R.styleable.Key_isShiftable,
        parentRow.keysAreShiftable
      );
    isExtendedLeft =
      attributesArray.getBoolean(R.styleable.Key_isExtendedLeft, false);
    isExtendedRight =
      attributesArray.getBoolean(R.styleable.Key_isExtendedRight, false);
    
    valueText = attributesArray.getString(R.styleable.Key_valueText);
    displayText = attributesArray.getString(R.styleable.Key_displayText);
    if (displayText == null) {
      displayText = valueText;
    }
    valueTextShifted =
      attributesArray.getString(R.styleable.Key_valueTextShifted);
    if (isShiftable && valueTextShifted == null) {
      valueTextShifted = displayText.toUpperCase();
    }
    else if (valueTextShifted == null) {
      valueTextShifted = displayText;
    }
    
    width =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Key_keyWidth,
        grandparentKeyboard.screenWidth,
        parentRow.keyWidth
      );
    height =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Key_keyHeight,
        grandparentKeyboard.screenHeight,
        parentRow.keyHeight
      );
    
    keyFillColour =
      attributesArray.getColor(
        R.styleable.Key_keyFillColour,
        parentRow.keyFillColour
      );
    keyBorderColour =
      attributesArray.getColor(
        R.styleable.Key_keyBorderColour,
        parentRow.keyBorderColour
      );
    keyBorderThickness =
      attributesArray.getDimensionPixelSize(
        R.styleable.Key_keyBorderThickness,
        parentRow.keyBorderThickness
      );
    
    keyTextColour =
      attributesArray.getColor(
        R.styleable.Key_keyTextColour,
        parentRow.keyTextColour
      );
    keyTextSwipeColour =
      attributesArray.getColor(
        R.styleable.Key_keyTextSwipeColour,
        parentRow.keyTextSwipeColour
      );
    keyTextSize =
      attributesArray.getDimensionPixelSize(
        R.styleable.Key_keyTextSize,
        parentRow.keyTextSize
      );
    keyTextOffsetX =
      attributesArray.getDimensionPixelSize(
        R.styleable.Key_keyTextOffsetX,
        parentRow.keyTextOffsetX
      );
    keyTextOffsetY =
      attributesArray.getDimensionPixelSize(
        R.styleable.Key_keyTextOffsetY,
        parentRow.keyTextOffsetY
      );
    
    attributesArray.recycle();
  }
  
  public boolean containsPoint(final int x, final int y) {
    return (
      (this.isExtendedLeft || this.x <= x)
        &&
        (this.isExtendedRight || x <= this.x + this.width)
        &&
        this.y <= y && y <= this.y + this.height
    );
  }
}
