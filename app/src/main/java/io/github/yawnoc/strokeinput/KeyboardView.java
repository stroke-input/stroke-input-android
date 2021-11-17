/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This file contains bytes copied from the deprecated `KeyboardView` class,
  i.e. `core/java/android/inputmethodservice/KeyboardView.java`
  from <https://android.googlesource.com/platform/frameworks/base>,
  which is licensed under the Apache License 2.0,
  see <https://www.apache.org/licenses/LICENSE-2.0.html>.
  ---
  Take your pick from the following out-of-date notices:
  In `core/java/android/inputmethodservice/KeyboardView.java`:
    Copyright (C) 2008-2009 Google Inc.
  In `NOTICE`:
    Copyright 2005-2008 The Android Open Source Project
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

import androidx.core.graphics.ColorUtils;

import java.util.List;

/*
  A container that holds a keyboard.
*/
public class KeyboardView
  extends View
  implements View.OnClickListener
{
  
  private static final int NONEXISTENT_POINTER_ID = -1;
  
  private static final int MESSAGE_KEY_REPEAT = 1;
  private static final int MESSAGE_LONG_PRESS = 2;
  private static final int DEFAULT_KEY_REPEAT_INTERVAL_MILLISECONDS = 75;
  private static final int KEY_REPEAT_START_MILLISECONDS = 500;
  private static final int KEY_LONG_PRESS_MILLISECONDS = 750;
  
  private static final int SWIPE_ACTIVATION_DISTANCE = 40;
  
  public static final int SHIFT_DISABLED = 0;
  private static final int SHIFT_SINGLE = 1;
  private static final int SHIFT_PERSISTENT = 2;
  private static final int SHIFT_INITIATED = 3;
  private static final int SHIFT_HELD = 4;
  
  public static final String KEYBOARD_FONT_FILE_NAME = "StrokeInputKeyboard.ttf";
  private static final float COLOUR_LIGHTNESS_CUTOFF = 0.7f;
  
  // Container properties
  private KeyboardListener keyboardListener;
  private Keyboard keyboard;
  private List<Key> keyList;
  
  // Active key
  private Key activeKey;
  private int activePointerId = NONEXISTENT_POINTER_ID;
  
  // Long presses and key repeats
  private Handler extendedPressHandler;
  private int keyRepeatIntervalMilliseconds;
  
  // Horizontal swipes
  private int pointerDownX;
  private boolean swipeModeIsActivated = false;
  
  // Shift key
  private int shiftPointerId = NONEXISTENT_POINTER_ID;
  private int shiftMode;
  
  // Keyboard drawing
  private Typeface keyboardFont;
  private Rect inputRectangle;
  private Paint inputFillPaint;
  
  // Key drawing
  private Rect keyRectangle;
  private Paint keyFillPaint;
  private Paint keyBorderPaint;
  private Paint keyTextPaint;
  
  // Key preview plane
  private KeyPreviewPlane keyPreviewPlane;
  private PopupWindow keyPreviewPlanePopup;
  
  public KeyboardView(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  public interface KeyboardListener {
    void saveKeyboard(Keyboard keyboard);
  }
  
  public void setKeyboardListener(final KeyboardListener keyboardListener) {
    this.keyboardListener = keyboardListener;
  }
  
  public void setKeyboard(final Keyboard keyboard) {
    keyboardListener.saveKeyboard(keyboard);
    this.keyboard = keyboard;
    keyList = keyboard.getKeyList();
    // TODO: paints
    // TODO: shift mode
    requestLayout();
  }
  
  @Override
  public void onClick(final View view) {
  }
  
  /*
    Lighten a dark colour and darken a light colour.
    Used for key press colour changes.
  */
  public static int toPressedColour(final int colour) {
    
    final float[] colourHSLArray = new float[3];
    ColorUtils.colorToHSL(colour, colourHSLArray);
    
    float colourLightness = colourHSLArray[2];
    if (colourLightness < COLOUR_LIGHTNESS_CUTOFF) {
      colourLightness = (2 * colourLightness + 1) / 3;
    }
    else {
      colourLightness = (2 * colourLightness) / 3;
    }
    
    colourHSLArray[2] = colourLightness;
    
    return ColorUtils.HSLToColor(colourHSLArray);
    
  }
  
}
