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
 A container that holds keys.
*/
public class Row {
  
  private static final int DEFAULT_OFFSET_X = 0;
  
  // Key properties
  public final boolean keysAreShiftable;
  public final int keyWidth;
  public final int keyHeight;
  public final int keyFillColour;
  public final int keyBorderColour;
  public final int keyBorderThickness;
  public final int keyTextColour;
  public final int keyTextSwipeColour;
  public final int keyTextSize;
  public final int keyTextOffsetX;
  public final int keyTextOffsetY;
  
  // Row properties
  public final Keyboard parentKeyboard;
  public final int offsetX;
  
  public Row(
    final Keyboard parentKeyboard,
    final Resources resources,
    final XmlResourceParser xmlResourceParser
  )
  {
    this.parentKeyboard = parentKeyboard;
    
    final TypedArray attributesArray =
      resources.obtainAttributes(
        Xml.asAttributeSet(xmlResourceParser),
        R.styleable.Keyboard
      );
    
    offsetX =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_offsetX,
        parentKeyboard.screenWidth,
        DEFAULT_OFFSET_X
      );
    
    keysAreShiftable =
      attributesArray.getBoolean(
        R.styleable.Keyboard_isShiftable,
        parentKeyboard.keysAreShiftable
      );
    
    keyWidth =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyWidth,
        parentKeyboard.screenWidth,
        parentKeyboard.keyWidth
      );
    keyHeight =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyHeight,
        parentKeyboard.screenHeight,
        parentKeyboard.keyHeight
      );
    
    keyFillColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyFillColour,
        parentKeyboard.keyFillColour
      );
    keyBorderColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyBorderColour,
        parentKeyboard.keyBorderColour
      );
    keyBorderThickness =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyBorderThickness,
        parentKeyboard.keyBorderThickness
      );
    
    keyTextColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyTextColour,
        parentKeyboard.keyTextColour
      );
    keyTextSwipeColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyTextSwipeColour,
        parentKeyboard.keyTextSwipeColour
      );
    keyTextSize =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextSize,
        parentKeyboard.keyTextSize
      );
    
    keyTextOffsetX =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextOffsetX,
        parentKeyboard.keyTextOffsetX
      );
    keyTextOffsetY =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextOffsetY,
        parentKeyboard.keyTextOffsetY
      );
    
    attributesArray.recycle();
  }
  
}
