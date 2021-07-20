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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import java.util.ArrayList;
import java.util.List;

/*
  A container that holds rows of keys, to be declared in a layout XML.
*/
public class Keyboard {
  
  private static final float DEFAULT_KEY_WIDTH_PROPORTION = 0.1f;
  private static final int DEFAULT_KEY_HEIGHT_DP = 64;
  private final int defaultKeyHeightPx;
  
  private static final int DEFAULT_KEY_FILL_COLOUR = Color.BLACK;
  private static final int DEFAULT_KEY_BORDER_COLOUR = Color.GRAY;
  private static final int DEFAULT_KEY_BORDER_THICKNESS_DP = 2;
  private final int defaultKeyBorderThicknessPx;
  private static final int DEFAULT_KEY_TEXT_COLOUR = Color.WHITE;
  private static final int DEFAULT_KEY_TEXT_SWIPE_COLOUR = Color.RED;
  private static final int DEFAULT_KEY_TEXT_SIZE_SP = 32;
  private final int defaultKeyTextSizePx;
  
  private static final int DEFAULT_OFFSET_X = 0;
  private static final int DEFAULT_KEYBOARD_FILL_COLOUR = Color.BLACK;
  public static final int KEYBOARD_GUTTER_HEIGHT_PX = 1;
  
  // Key properties
  private boolean keysAreShiftable;
  private int keyWidth;
  private int keyHeight;
  private int keyFillColour;
  private int keyBorderColour;
  private int keyBorderThickness;
  private int keyTextColour;
  private int keyTextSwipeColour;
  private int keyTextSize;
  private int keyTextOffsetX;
  private int keyTextOffsetY;
  
  // Keyboard properties
  private int width;
  private int height;
  private final List<Key> keyList;
  public int fillColour;
  
  // Screen properties
  public final int screenWidth;
  public final int screenHeight;
  
  public Keyboard(final Context context, final int layoutResourceId) {
    
    final DisplayMetrics displayMetrics =
      context.getResources().getDisplayMetrics();
    
    screenWidth = displayMetrics.widthPixels;
    screenHeight = displayMetrics.heightPixels;
    
    defaultKeyHeightPx =
      (int) (DEFAULT_KEY_HEIGHT_DP * displayMetrics.density);
    defaultKeyBorderThicknessPx =
      (int) (DEFAULT_KEY_BORDER_THICKNESS_DP * displayMetrics.density);
    defaultKeyTextSizePx =
      (int) (DEFAULT_KEY_TEXT_SIZE_SP * displayMetrics.scaledDensity);
    
    keyList = new ArrayList<>();
    
    loadKeyboard(context, context.getResources().getXml(layoutResourceId));
  }
  
  public List<Key> getKeyList() {
    return keyList;
  }
  
  /*
   A container that holds keys.
  */
  public static class Row {
    
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
    private final int offsetX;
    
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
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  private void loadKeyboard(
    final Context context,
    final XmlResourceParser xmlResourceParser
  )
  {
    try {
      
      boolean inKey = false;
      boolean inRow = false;
      
      int x = 0;
      int y = KEYBOARD_GUTTER_HEIGHT_PX;
      Key key = null;
      Row row = null;
      
      int maximumX = x;
      int maximumY = y;
      
      final Resources resources = context.getResources();
      
      int event;
      
      while (
        (event = xmlResourceParser.next()) != XmlResourceParser.END_DOCUMENT
      )
      {
        switch (event) {
          case XmlResourceParser.START_TAG:
            final String xmlTag = xmlResourceParser.getName();
            switch (xmlTag) {
              case "Keyboard":
                parseKeyboardAttributes(resources, xmlResourceParser);
                break;
              case "Row":
                inRow = true;
                row = new Row(this, resources, xmlResourceParser);
                x = row.offsetX;
                break;
              case "Key":
                inKey = true;
                key = new Key(row, x, y, resources, xmlResourceParser);
                keyList.add(key);
                break;
            }
            break;
          case XmlResourceParser.END_TAG:
            if (inKey) {
              inKey = false;
              x += key.width;
              maximumX = Math.max(x, maximumX);
            }
            else if (inRow) {
              inRow = false;
              y += row.keyHeight;
              maximumY = Math.max(y, maximumY);
            }
            break;
        }
      }
      
      width = maximumX;
      height = maximumY;
      
    }
    catch (Exception exception) {
      Log.e("Keyboard.loadKeyboard", "Exception: " + exception);
      exception.printStackTrace();
    }
  }
  
  private void parseKeyboardAttributes(
    final Resources resources,
    final XmlResourceParser xmlResourceParser
  )
  {
    final TypedArray attributesArray =
      resources.obtainAttributes(
        Xml.asAttributeSet(xmlResourceParser),
        R.styleable.Keyboard
      );
    
    keyWidth =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyWidth,
        screenWidth,
        (int) (DEFAULT_KEY_WIDTH_PROPORTION * screenWidth)
      );
    keyHeight =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyHeight,
        screenHeight,
        defaultKeyHeightPx
      );
    
    keysAreShiftable =
      attributesArray.getBoolean(R.styleable.Keyboard_isShiftable, false);
    
    keyFillColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyFillColour,
        DEFAULT_KEY_FILL_COLOUR
      );
    keyBorderColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyBorderColour,
        DEFAULT_KEY_BORDER_COLOUR
      );
    keyBorderThickness =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyBorderThickness,
        defaultKeyBorderThicknessPx
      );
    
    keyTextColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyTextColour,
        DEFAULT_KEY_TEXT_COLOUR
      );
    keyTextSwipeColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyTextSwipeColour,
        DEFAULT_KEY_TEXT_SWIPE_COLOUR
      );
    keyTextSize =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextSize,
        defaultKeyTextSizePx
      );
    
    keyTextOffsetX =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextOffsetX,
        0
      );
    keyTextOffsetY =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextOffsetY,
        0
      );
    
    fillColour =
      attributesArray.getColor(
        R.styleable.Keyboard_fillColour,
        DEFAULT_KEYBOARD_FILL_COLOUR
      );
    
    attributesArray.recycle();
  }
  
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
