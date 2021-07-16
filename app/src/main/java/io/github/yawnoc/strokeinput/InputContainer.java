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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;

/*
  A container that holds:
    - Candidates bar
    - Keyboard
  TODO:
    - Candidates
*/
public class InputContainer
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
  public static final int SHIFT_SINGLE = 1;
  public static final int SHIFT_PERSISTENT = 2;
  public static final int SHIFT_INITIATED = 3;
  public static final int SHIFT_HELD = 4;
  
  private static final float COLOUR_LIGHTNESS_CUTOFF = 0.7f;
  
  public static final int DEBUG_ACTIVE_POINTER_COLOUR = Color.RED;
  public static final int DEBUG_ACTIVE_POINTER_RADIUS = 60;
  
  // Container properties
  private OnInputListener inputListener;
  private Keyboard keyboard;
  private Keyboard.Key[] keyArray;
  private Keyboard.Key currentlyPressedKey;
  
  // Pointer properties
  private int activePointerId = NONEXISTENT_POINTER_ID;
  private int activePointerX;
  private int activePointerY;
  
  // Long presses and key repeats
  private final Handler extendedPressHandler;
  private int keyRepeatIntervalMilliseconds;
  
  // Horizontal swipes
  private int pointerDownX;
  private boolean swipeModeIsActivated = false;
  
  // Shift key
  private int shiftPointerId = NONEXISTENT_POINTER_ID;
  private int shiftMode;
  private boolean shiftModeIsActivated = false;
  
  // Keyboard drawing
  private final Rect keyboardRectangle;
  private final Paint keyboardFillPaint;
  private final Rect keyRectangle;
  private final Paint keyFillPaint;
  private final Paint keyBorderPaint;
  private final Paint keyTextPaint;
  
  // Debugging
  private final Paint debugPaint;
  private final Toast debugToast;
  private boolean debugModeIsActivated = true;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    
    super(context, attributes);
    
    resetKeyRepeatIntervalMilliseconds();
    extendedPressHandler =
      new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
          if (currentlyPressedKey != null) {
            switch (message.what) {
              case MESSAGE_KEY_REPEAT:
                inputListener.onKey(currentlyPressedKey.valueText);
                sendExtendedPressHandlerMessage(
                  MESSAGE_KEY_REPEAT,
                  keyRepeatIntervalMilliseconds
                );
                break;
              case MESSAGE_LONG_PRESS:
                inputListener.onLongPress(currentlyPressedKey.valueText);
                abortAllKeyBehaviour();
                break;
            }
          }
        }
      };
    
    this.setBackgroundColor(Color.TRANSPARENT);
    
    keyboardRectangle = new Rect();
    keyboardFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    keyRectangle = new Rect();
    
    keyFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyFillPaint.setStyle(Paint.Style.FILL);
    
    keyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyBorderPaint.setStyle(Paint.Style.STROKE);
    
    keyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyTextPaint.setTypeface(
      ResourcesCompat.getFont(context, R.font.stroke_input_keyboard)
    );
    keyTextPaint.setTextAlign(Paint.Align.CENTER);
    
    debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    debugPaint.setStyle(Paint.Style.STROKE);
    
    debugToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
  }
  
  /*
    A listener for input events.
  */
  public interface OnInputListener {
    void onKey(String valueText);
    void onLongPress(String valueText);
    void onShiftDown();
    void onShiftUp();
    void onKeyDownWhileShiftPressed();
    void onSwipe(String valueText);
  }
  
  public void setOnInputListener(final OnInputListener listener) {
    inputListener = listener;
  }
  
  public Keyboard getKeyboard() {
    return keyboard;
  }
  
  public void setKeyboard(final Keyboard keyboard) {
    this.keyboard = keyboard;
    keyArray = keyboard.getKeyList().toArray(new Keyboard.Key[0]);
    keyboardFillPaint.setColor(keyboard.fillColour);
    if (getShiftMode() != SHIFT_PERSISTENT) {
      setShiftMode(SHIFT_DISABLED);
    }
    requestLayout();
  }
  
  public void resetKeyRepeatIntervalMilliseconds() {
    keyRepeatIntervalMilliseconds = DEFAULT_KEY_REPEAT_INTERVAL_MILLISECONDS;
  }
  
  public void setKeyRepeatIntervalMilliseconds(final int milliseconds) {
    keyRepeatIntervalMilliseconds = milliseconds;
  }
  
  public int getShiftMode() {
    return shiftMode;
  }
  
  public void setShiftMode(final int mode) {
    shiftMode = mode;
    shiftModeIsActivated = shiftMode != SHIFT_DISABLED;
  }
  
  public void toggleDebugMode() {
    debugModeIsActivated = !debugModeIsActivated;
    showDebugToast("debug mode " + (debugModeIsActivated ? "ON" : "OFF"));
  }
  
  public void onClick(final View view) {
  }
  
  @Override
  public void onMeasure(
    final int widthMeasureSpec,
    final int heightMeasureSpec
  )
  {
    final int keyboardWidth;
    final int keyboardHeight;
    
    if (keyboard == null) {
      keyboardWidth = 0;
      keyboardHeight = 0;
    }
    else {
      keyboardWidth = keyboard.getWidth();
      keyboardHeight = keyboard.getHeight();
    }
    
    keyboardRectangle.set(
      0,
      Keyboard.KEYBOARD_GUTTER_HEIGHT_PX,
      keyboardWidth,
      Keyboard.KEYBOARD_GUTTER_HEIGHT_PX + keyboardHeight
    );
    
    setMeasuredDimension(keyboardWidth, keyboardHeight);
  }
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    if (keyboard == null) {
      return;
    }
    
    canvas.drawRect(keyboardRectangle, keyboardFillPaint);
    
    for (final Keyboard.Key key : keyArray) {
      
      keyRectangle.set(0, 0, key.width, key.height);
      
      int keyFillColour = key.keyFillColour;
      if (
        key == currentlyPressedKey
          ||
        key.valueText.equals("SHIFT") && (
          shiftPointerId != NONEXISTENT_POINTER_ID
            ||
          getShiftMode() == SHIFT_PERSISTENT
            ||
          getShiftMode() == SHIFT_INITIATED
            ||
          getShiftMode() == SHIFT_HELD
        )
      )
      {
        keyFillColour = getContrastingColour(keyFillColour);
      }
      
      keyFillPaint.setColor(keyFillColour);
      keyBorderPaint.setColor(key.keyBorderColour);
      keyBorderPaint.setStrokeWidth(key.keyBorderThickness);
      
      final int keyTextColour;
      if (key == currentlyPressedKey && swipeModeIsActivated) {
        keyTextColour = key.keyTextSwipeColour;
      }
      else {
        keyTextColour = key.keyTextColour;
      }
      keyTextPaint.setColor(keyTextColour);
      keyTextPaint.setTextSize(key.keyTextSize);
      
      final String keyDisplayText;
      if (debugModeIsActivated && key.valueText.equals("SPACE")) {
        keyDisplayText = (
          currentlyPressedKey == null
            ? "null"
            : currentlyPressedKey.valueText
        );
      }
      else if (getShiftMode() == SHIFT_DISABLED) {
        keyDisplayText = key.displayText;
      }
      else {
        keyDisplayText = key.valueTextShifted;
      }
      
      final float keyTextX = (
        key.width / 2f
          + key.keyTextOffsetX
      );
      final float keyTextY = (
        (key.height - keyTextPaint.ascent() - keyTextPaint.descent()) / 2f
          + key.keyTextOffsetY
      );
      
      canvas.translate(key.x, key.y);
      
      canvas.drawRect(keyRectangle, keyFillPaint);
      canvas.drawRect(keyRectangle, keyBorderPaint);
      canvas.drawText(
        keyDisplayText,
        keyTextX,
        keyTextY,
        keyTextPaint
      );
      
      canvas.translate(-key.x, -key.y);
    }
    
    if (debugModeIsActivated && activePointerId != NONEXISTENT_POINTER_ID) {
      debugPaint.setColor(DEBUG_ACTIVE_POINTER_COLOUR);
      canvas.drawCircle(
        activePointerX,
        activePointerY,
        DEBUG_ACTIVE_POINTER_RADIUS,
        debugPaint
      );
    }
  }
  
  /*
    Lighten a dark colour and darken a light colour.
    Used for key press colour changes.
  */
  private static int getContrastingColour(final int colour) {
    
    final float[] colourHSL = new float[3];
    ColorUtils.colorToHSL(colour, colourHSL);
    
    float colourLightness = colourHSL[2];
    if (colourLightness < COLOUR_LIGHTNESS_CUTOFF) {
      colourLightness = (2 * colourLightness + 1) / 3;
    }
    else {
      colourLightness = (2 * colourLightness) / 3;
    }
    
    colourHSL[2] = colourLightness;
    
    return ColorUtils.HSLToColor(colourHSL);
  }
  
  /*
    Handle logic for multiple pointers (e.g. two-thumb typing).
    The correct handling of a pointer moving outside the keyboard
    is ensured by including a 1-pixel gutter at the top of the keyboard
    (so that the pointer must move through a key-free row of pixels).
    The correct handling of merging pointers has not been implemented.
  */
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(final MotionEvent event) {
    
    final int eventPointerCount = event.getPointerCount();
    
    if (eventPointerCount > 2) {
      abortAllKeyBehaviour();
      return true;
    }
    
    final boolean currentlyPressedKeyIsSwipeable =
      currentlyPressedKey != null && currentlyPressedKey.isSwipeable;
    
    switch (event.getActionMasked()) {
      
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_POINTER_DOWN:
        
        final int downPointerIndex = event.getActionIndex();
        final int downPointerId = event.getPointerId(downPointerIndex);
        final int downPointerX = (int) event.getX(downPointerIndex);
        final int downPointerY = (int) event.getY(downPointerIndex);
        final Keyboard.Key downKey = getKeyAtPoint(downPointerX, downPointerY);
        
        if (isShiftKey(downKey)) {
          inputListener.onShiftDown();
          shiftPointerId = downPointerId;
          invalidate();
          return true;
        }
        
        if (activePointerId != NONEXISTENT_POINTER_ID) {
          sendUpEvent(currentlyPressedKey, false);
        }
        
        sendDownEvent(downKey, downPointerX);
        activePointerId = downPointerId;
        activePointerX = downPointerX;
        activePointerY = downPointerY;
        
        invalidate();
        
        break;
      
      case MotionEvent.ACTION_MOVE:
        
        for (int index = 0; index < eventPointerCount; index++) {
          
          final int movePointerId = event.getPointerId(index);
          final int movePointerX = (int) event.getX(index);
          final int movePointerY = (int) event.getY(index);
          final Keyboard.Key moveKey =
            getKeyAtPoint(movePointerX, movePointerY);
          
          if (movePointerId == activePointerId) {
            
            if (isShiftKey(moveKey) && !currentlyPressedKeyIsSwipeable) {
              inputListener.onShiftDown();
              shiftPointerId = movePointerId;
              abortAllKeyBehaviour();
              return true;
            }
            
            if (
              moveKey != currentlyPressedKey
                ||
              currentlyPressedKeyIsSwipeable
            )
            {
              sendMoveEvent(moveKey, movePointerX);
              activePointerId = movePointerId;
              activePointerX = movePointerX;
              activePointerY = movePointerY;
              invalidate();
            }
            
            break;
          }
        }
        
        break;
      
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        
        final int upPointerIndex = event.getActionIndex();
        final int upPointerId = event.getPointerId(upPointerIndex);
        final int upPointerX = (int) event.getX(upPointerIndex);
        final int upPointerY = (int) event.getY(upPointerIndex);
        final Keyboard.Key upKey = getKeyAtPoint(upPointerX, upPointerY);
        
        if (
          (upPointerId == shiftPointerId || isShiftKey(upKey))
            &&
          !currentlyPressedKeyIsSwipeable
        )
        {
          inputListener.onShiftUp();
          shiftPointerId = NONEXISTENT_POINTER_ID;
          invalidate();
          return true;
        }
        
        if (upPointerId == activePointerId) {
          sendUpEvent(upKey, true);
        }
        
        break;
    }
    
    return true;
  }
  
  private void sendDownEvent(final Keyboard.Key key, final int x) {
    
    if (shiftPointerId != NONEXISTENT_POINTER_ID) {
      inputListener.onKeyDownWhileShiftPressed();
    }
    
    currentlyPressedKey = key;
    sendAppropriateExtendedPressHandlerMessage(key);
    
    swipeModeIsActivated = false;
    if (key != null && key.isSwipeable) {
      pointerDownX = x;
    }
  }
  
  private void sendMoveEvent(final Keyboard.Key key, final int x) {
    
    if (swipeModeIsActivated) {
      if (Math.abs(x - pointerDownX) < SWIPE_ACTIVATION_DISTANCE) {
        swipeModeIsActivated = false;
      }
      return;
    }
    
    if (key != null && key == currentlyPressedKey && key.isSwipeable) {
      if (Math.abs(x - pointerDownX) > SWIPE_ACTIVATION_DISTANCE) {
        swipeModeIsActivated = true;
        removeAllExtendedPressHandlerMessages();
      }
      return;
    }
    
    removeAllExtendedPressHandlerMessages();
    currentlyPressedKey = key;
    sendAppropriateExtendedPressHandlerMessage(key);
    resetKeyRepeatIntervalMilliseconds();
  }
  
  private void sendUpEvent(
    final Keyboard.Key key,
    final boolean shouldRedrawKeyboard
  )
  {
    if (swipeModeIsActivated) {
      inputListener.onSwipe(currentlyPressedKey.valueText);
    }
    else if (key != null) {
      if (shiftModeIsActivated && key.isShiftable) {
        inputListener.onKey(key.valueTextShifted);
      }
      else {
        inputListener.onKey(key.valueText);
      }
    }
    
    removeAllExtendedPressHandlerMessages();
    activePointerId = NONEXISTENT_POINTER_ID;
    currentlyPressedKey = null;
    resetKeyRepeatIntervalMilliseconds();
    
    if (shouldRedrawKeyboard) {
      invalidate();
    }
  }
  
  private Keyboard.Key getKeyAtPoint(final int x, final int y) {
    
    for (final Keyboard.Key key : keyArray) {
      if (key.containsPoint(x, y)) {
        return key;
      }
    }
    
    return null;
  }
  
  private boolean isShiftKey(final Keyboard.Key key) {
    return key != null && key.valueText.equals("SHIFT");
  }
  
  private void sendAppropriateExtendedPressHandlerMessage(
    final Keyboard.Key key
  )
  {
    if (key == null) {
      return;
    }
    if (key.isRepeatable) {
      sendExtendedPressHandlerMessage(
        MESSAGE_KEY_REPEAT,
        KEY_REPEAT_START_MILLISECONDS
      );
    }
    else if (key.isLongPressable) {
      sendExtendedPressHandlerMessage(
        MESSAGE_LONG_PRESS,
        KEY_LONG_PRESS_MILLISECONDS
      );
    }
  }
  
  private void sendExtendedPressHandlerMessage(
    final int messageWhat,
    final long delayMilliseconds
  )
  {
    extendedPressHandler.sendMessageDelayed(
      extendedPressHandler.obtainMessage(messageWhat),
      delayMilliseconds
    );
  }
  
  private void removeAllExtendedPressHandlerMessages() {
    removeExtendedPressHandlerMessages(MESSAGE_KEY_REPEAT);
    removeExtendedPressHandlerMessages(MESSAGE_LONG_PRESS);
  }
  
  private void removeExtendedPressHandlerMessages(final int messageWhat) {
    extendedPressHandler.removeMessages(messageWhat);
  }
  
  private void abortAllKeyBehaviour() {
    currentlyPressedKey = null;
    activePointerId = NONEXISTENT_POINTER_ID;
    invalidate();
  }
  
  private void showDebugToast(final String message) {
    debugToast.setText(message);
    debugToast.show();
  }
}
