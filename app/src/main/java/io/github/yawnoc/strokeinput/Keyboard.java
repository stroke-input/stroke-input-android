/*
  Copyright 2021--2024 Conway
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

  private static final int DEFAULT_KEYBOARD_FILL_COLOUR = Color.BLACK;
  private static final int KEYBOARD_GUTTER_HEIGHT_DP = 4;
  private final int keyboardGutterHeightPx;

  private static final float KEYBOARD_HEIGHT_MAX_FRACTION = 0.5f;
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
  private static final int DEFAULT_KEY_PREVIEW_MARGIN_Y_DP = 24;
  private final int defaultKeyPreviewMarginYPx;

  // Keyboard properties
  private final Context applicationContext;
  private int width;
  private int height, naturalHeight;
  private final List<Key> keys;
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
    final Resources resources = context.getResources();
    final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
    applicationContext = context.getApplicationContext();

    screenWidth = displayMetrics.widthPixels;
    screenHeight = displayMetrics.heightPixels;

    keyboardGutterHeightPx = (int) Valuey.pxFromDp(KEYBOARD_GUTTER_HEIGHT_DP, displayMetrics);
    defaultKeyHeightPx = (int) Valuey.pxFromDp(DEFAULT_KEY_HEIGHT_DP, displayMetrics);
    defaultKeyBorderThicknessPx = (int) Valuey.pxFromDp(DEFAULT_KEY_BORDER_THICKNESS_DP, displayMetrics);
    defaultKeyTextSizePx = (int) Valuey.pxFromSp(DEFAULT_KEY_TEXT_SIZE_SP, displayMetrics);
    defaultKeyPreviewMarginYPx = (int) Valuey.pxFromDp(DEFAULT_KEY_PREVIEW_MARGIN_Y_DP, displayMetrics);

    keys = new ArrayList<>();

    makeKeyboard(context, resources.getXml(layoutResourceId));
    adjustKeyboardHeight();
  }

  public List<Key> getKeys()
  {
    return keys;
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
      int y = keyboardGutterHeightPx;
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
                keys.add(key);
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
      height = naturalHeight = maximumY;
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  public void correctKeyboardWidth(int inputContainerWidth)
  {
    final float correctionFactor = ((float) inputContainerWidth) / screenWidth;
    for (final Key key : keys)
    {
      key.x = (int) (key.naturalX * correctionFactor);
      key.width = (int) (key.naturalWidth * correctionFactor);
      key.textOffsetX = (int) (key.naturalTextOffsetX * correctionFactor);
    }
  }

  public void adjustKeyboardHeight()
  {
    final int userAdjustmentProgress = MainActivity.loadSavedKeyboardHeightAdjustmentProgress(applicationContext);
    final float userAdjustmentFactor = MainActivity.keyboardHeightAdjustmentProgressToFactor(userAdjustmentProgress);
    final float actualAdjustmentFactor =
            Math.min(userAdjustmentFactor, KEYBOARD_HEIGHT_MAX_FRACTION * screenHeight / naturalHeight);
    for (final Key key : keys)
    {
      key.y = (int) (key.naturalY * actualAdjustmentFactor);
      key.height = (int) (key.naturalHeight * actualAdjustmentFactor);
      key.textOffsetY = (int) (key.naturalTextOffsetY * actualAdjustmentFactor);
      key.previewMarginY = (int) (key.naturalPreviewMarginY * actualAdjustmentFactor);
    }
    height = (int) (naturalHeight * actualAdjustmentFactor);
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
