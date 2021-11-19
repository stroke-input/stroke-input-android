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
import android.util.Xml;

import java.util.ArrayList;
import java.util.List;

import io.github.yawnoc.utilities.Valuey;

/*
  A keyboard that holds rows of keys, to be declared in a layout XML.
*/
public class Keyboard
{
  private static final String KEYBOARD_TAG = "Keyboard";
  private static final String ROW_TAG = "Row";
  private static final String KEY_TAG = "Key";
  
  private static final int KEYBOARD_GUTTER_HEIGHT_PX = 1;
  private static final int DEFAULT_KEYBOARD_FILL_COLOUR = Color.BLACK;
  
  private static final float KEYBOARD_HEIGHT_MAX_FRACTION = 0.45f;
  private static final float DEFAULT_KEY_WIDTH_FRACTION = 0.1f;
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
  
  private static final float DEFAULT_KEY_PREVIEW_MAGNIFICATION = 1.2f;
  private static final int DEFAULT_KEY_PREVIEW_MARGIN_Y_DP = 16;
  private final int defaultKeyPreviewMarginYPx;
  
  // Keyboard properties
  private int width;
  private int height;
  private final List<Key> keyList;
  public int fillColour;
  
  // Key properties
  public boolean keysAreShiftable;
  public boolean keysArePreviewable;
  public int keyWidth;
  public int keyHeight;
  public int keyFillColour;
  public int keyBorderColour;
  public int keyBorderThickness;
  public int keyTextColour;
  public int keyTextSwipeColour;
  public int keyTextSize;
  public int keyTextOffsetX;
  public int keyTextOffsetY;
  public float keyPreviewMagnification;
  public int keyPreviewMarginY;
  
  // Screen properties
  private final int screenWidth;
  private final int screenHeight;
  
  public Keyboard(final Context context, final int layoutResourceId)
  {
    final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    
    screenWidth = displayMetrics.widthPixels;
    screenHeight = displayMetrics.heightPixels;
    
    defaultKeyHeightPx = (int) Valuey.pxFromDp(DEFAULT_KEY_HEIGHT_DP, displayMetrics);
    defaultKeyBorderThicknessPx = (int) Valuey.pxFromDp(DEFAULT_KEY_BORDER_THICKNESS_DP, displayMetrics);
    defaultKeyTextSizePx = (int) Valuey.pxFromSp(DEFAULT_KEY_TEXT_SIZE_SP, displayMetrics);
    defaultKeyPreviewMarginYPx = (int) Valuey.pxFromDp(DEFAULT_KEY_PREVIEW_MARGIN_Y_DP, displayMetrics);
    
    keyList = new ArrayList<>();
    
    makeKeyboard(context, context.getResources().getXml(layoutResourceId));
    capKeyboardHeight();
  }
  
  public List<Key> getKeyList()
  {
    return keyList;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int getScreenWidth()
  {
    return screenWidth;
  }
  
  public int getScreenHeight()
  {
    return screenHeight;
  }
  
  private void makeKeyboard(final Context context, final XmlResourceParser xmlResourceParser)
  {
    try
    {
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
      while ((event = xmlResourceParser.next()) != XmlResourceParser.END_DOCUMENT)
      {
        switch (event)
        {
          case XmlResourceParser.START_TAG:
            final String xmlTag = xmlResourceParser.getName();
            switch (xmlTag)
            {
              case KEYBOARD_TAG:
                parseKeyboardAttributes(resources, xmlResourceParser);
                break;
              
              case ROW_TAG:
                inRow = true;
                row = new Row(this, resources, xmlResourceParser);
                x = row.offsetX;
                break;
              
              case KEY_TAG:
                inKey = true;
                key = new Key(row, x, y, resources, xmlResourceParser);
                keyList.add(key);
                break;
            }
            break;
          
          case XmlResourceParser.END_TAG:
            if (inKey)
            {
              inKey = false;
              x += key.width;
              maximumX = Math.max(x, maximumX);
            }
            else if (inRow)
            {
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
    catch (Exception exception)
    {
      Log.e("Keyboard.makeKeyboard", "Exception: " + exception);
      exception.printStackTrace();
    }
  }
  
  private void capKeyboardHeight()
  {
    final float keyboardHeightCorrectionFactor = Math.min(1, KEYBOARD_HEIGHT_MAX_FRACTION * screenHeight / height);
    for (final Key key : keyList)
    {
      key.y *= keyboardHeightCorrectionFactor;
      key.height *= keyboardHeightCorrectionFactor;
      key.textOffsetY *= keyboardHeightCorrectionFactor;
      key.previewMarginY *= keyboardHeightCorrectionFactor;
    }
    height *= keyboardHeightCorrectionFactor;
  }
  
  private void parseKeyboardAttributes(final Resources resources, final XmlResourceParser xmlResourceParser)
  {
    final TypedArray attributesArray =
            resources.obtainAttributes(Xml.asAttributeSet(xmlResourceParser), R.styleable.Keyboard);
    
    fillColour = attributesArray.getColor(R.styleable.Keyboard_keyboardFillColour, DEFAULT_KEYBOARD_FILL_COLOUR);
    
    keysAreShiftable = attributesArray.getBoolean(R.styleable.Keyboard_keysAreShiftable, false);
    keysArePreviewable = attributesArray.getBoolean(R.styleable.Keyboard_keysArePreviewable, true);
    
    keyWidth =
            Valuey.getDimensionOrFraction(
              attributesArray,
              R.styleable.Keyboard_keyWidth,
              screenWidth,
              (int) (DEFAULT_KEY_WIDTH_FRACTION * screenWidth)
            );
    keyHeight =
            Valuey.getDimensionOrFraction(
              attributesArray,
              R.styleable.Keyboard_keyHeight,
              screenHeight,
              defaultKeyHeightPx
            );
    
    keyFillColour =
            attributesArray.getColor(R.styleable.Keyboard_keyFillColour, DEFAULT_KEY_FILL_COLOUR);
    keyBorderColour =
            attributesArray.getColor(R.styleable.Keyboard_keyBorderColour, DEFAULT_KEY_BORDER_COLOUR);
    keyBorderThickness =
            attributesArray.getDimensionPixelSize(R.styleable.Keyboard_keyBorderThickness, defaultKeyBorderThicknessPx);
    
    keyTextColour =
            attributesArray.getColor(R.styleable.Keyboard_keyTextColour, DEFAULT_KEY_TEXT_COLOUR);
    keyTextSwipeColour =
            attributesArray.getColor(R.styleable.Keyboard_keyTextSwipeColour, DEFAULT_KEY_TEXT_SWIPE_COLOUR);
    keyTextSize =
            attributesArray.getDimensionPixelSize(R.styleable.Keyboard_keyTextSize, defaultKeyTextSizePx);
    
    keyTextOffsetX = attributesArray.getDimensionPixelSize(R.styleable.Keyboard_keyTextOffsetX, 0);
    keyTextOffsetY = attributesArray.getDimensionPixelSize(R.styleable.Keyboard_keyTextOffsetY, 0);
    
    keyPreviewMagnification =
            attributesArray.getFloat(R.styleable.Keyboard_keyPreviewMagnification, DEFAULT_KEY_PREVIEW_MAGNIFICATION);
    keyPreviewMarginY =
            Valuey.getDimensionOrFraction(
              attributesArray,
              R.styleable.Keyboard_keyPreviewMarginY,
              screenHeight,
              defaultKeyPreviewMarginYPx
            );
    
    attributesArray.recycle();
  }
}
