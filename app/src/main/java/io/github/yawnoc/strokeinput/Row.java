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

import io.github.yawnoc.utilities.Valuey;

/*
 A container that holds keys.
*/
public class Row {
  
  private static final int DEFAULT_OFFSET_X = 0;
  
  // Row properties
  public final Keyboard parentKeyboard;
  public final int offsetX;
  
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
  public final float keyPreviewMagnification;
  public final int keyPreviewMargin;
  
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
        R.styleable.Row
      );
    
    offsetX =
      Valuey.getDimensionOrFraction(
        attributesArray,
        R.styleable.Row_rowOffsetX,
        parentKeyboard.screenWidth,
        DEFAULT_OFFSET_X
      );
    
    keysAreShiftable =
      attributesArray.getBoolean(
        R.styleable.Row_keysAreShiftable,
        parentKeyboard.keysAreShiftable
      );
    
    keyWidth =
      Valuey.getDimensionOrFraction(
        attributesArray,
        R.styleable.Row_keyWidth,
        parentKeyboard.screenWidth,
        parentKeyboard.keyWidth
      );
    keyHeight =
      Valuey.getDimensionOrFraction(
        attributesArray,
        R.styleable.Row_keyHeight,
        parentKeyboard.screenHeight,
        parentKeyboard.keyHeight
      );
    
    keyFillColour =
      attributesArray.getColor(
        R.styleable.Row_keyFillColour,
        parentKeyboard.keyFillColour
      );
    keyBorderColour =
      attributesArray.getColor(
        R.styleable.Row_keyBorderColour,
        parentKeyboard.keyBorderColour
      );
    keyBorderThickness =
      attributesArray.getDimensionPixelSize(
        R.styleable.Row_keyBorderThickness,
        parentKeyboard.keyBorderThickness
      );
    
    keyTextColour =
      attributesArray.getColor(
        R.styleable.Row_keyTextColour,
        parentKeyboard.keyTextColour
      );
    keyTextSwipeColour =
      attributesArray.getColor(
        R.styleable.Row_keyTextSwipeColour,
        parentKeyboard.keyTextSwipeColour
      );
    keyTextSize =
      attributesArray.getDimensionPixelSize(
        R.styleable.Row_keyTextSize,
        parentKeyboard.keyTextSize
      );
    
    keyTextOffsetX =
      attributesArray.getDimensionPixelSize(
        R.styleable.Row_keyTextOffsetX,
        parentKeyboard.keyTextOffsetX
      );
    keyTextOffsetY =
      attributesArray.getDimensionPixelSize(
        R.styleable.Row_keyTextOffsetY,
        parentKeyboard.keyTextOffsetY
      );
    
    keyPreviewMagnification =
      attributesArray.getFloat(
        R.styleable.Row_keyPreviewMagnification,
        parentKeyboard.keyPreviewMagnification
      );
    keyPreviewMargin =
      Valuey.getDimensionOrFraction(
        attributesArray,
        R.styleable.Row_keyPreviewMargin,
        parentKeyboard.screenHeight,
        parentKeyboard.keyPreviewMargin
      );
    
    attributesArray.recycle();
  }
  
}
