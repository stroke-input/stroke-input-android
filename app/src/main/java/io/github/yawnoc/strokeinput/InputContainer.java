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
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

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
  private static final int SHIFT_SINGLE = 1;
  private static final int SHIFT_PERSISTENT = 2;
  private static final int SHIFT_INITIATED = 3;
  private static final int SHIFT_HELD = 4;
  
  public static final String KEYBOARD_FONT = "stroke_input_keyboard.ttf";
  
  private static final float COLOUR_LIGHTNESS_CUTOFF = 0.7f;
  
  private static final int DEBUG_ACTIVE_POINTER_COLOUR = Color.RED;
  private static final int DEBUG_ACTIVE_POINTER_RADIUS = 60;
  
  // Container properties
  private OnInputListener inputListener;
  private Keyboard keyboard;
  private Key[] keyArray;
  
  // Active key
  private Key activeKey;
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
  
  // Keyboard drawing
  private final Rect keyboardRectangle;
  private final Paint keyboardFillPaint;
  
  // Key drawing
  private final Rect keyRectangle;
  private final Paint keyFillPaint;
  private final Paint keyBorderPaint;
  private final Paint keyTextPaint;
  
  // Key preview plane
  private final KeyPreviewPlane keyPreviewPlane;
  private final PopupWindow keyPreviewPlanePopup;
  
  // Debugging
  private final Paint debugPaint;
  private final Toast debugToast;
  private boolean debugModeIsActivated = false;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    
    super(context, attributes);
    
    resetKeyRepeatIntervalMilliseconds();
    extendedPressHandler =
      new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
          if (activeKey != null) {
            switch (message.what) {
              case MESSAGE_KEY_REPEAT:
                inputListener.onKey(activeKey.valueText);
                sendExtendedPressHandlerMessage(
                  MESSAGE_KEY_REPEAT,
                  keyRepeatIntervalMilliseconds
                );
                break;
              case MESSAGE_LONG_PRESS:
                inputListener.onLongPress(activeKey.valueText);
                activeKey = null;
                activePointerId = NONEXISTENT_POINTER_ID;
                keyPreviewPlane.dismissAllImmediately();
                invalidate();
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
      Typeface.createFromAsset(context.getAssets(), KEYBOARD_FONT)
    );
    keyTextPaint.setTextAlign(Paint.Align.CENTER);
    
    final int popup_size = LinearLayout.LayoutParams.WRAP_CONTENT;
    
    keyPreviewPlane = new KeyPreviewPlane(context);
    keyPreviewPlanePopup =
      new PopupWindow(keyPreviewPlane, popup_size, popup_size);
    keyPreviewPlanePopup.setTouchable(false);
    keyPreviewPlanePopup.setClippingEnabled(false);
    
    debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    debugPaint.setStyle(Paint.Style.STROKE);
    
    debugToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
  }
  
  @Override
  protected void onDetachedFromWindow() {
    
    // Prevent key preview plane persisting on screen rotate
    keyPreviewPlanePopup.dismiss();
    
    super.onDetachedFromWindow();
  }
  
  /*
    A listener for input events.
  */
  public interface OnInputListener {
    void onKey(String valueText);
    void onLongPress(String valueText);
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
    keyArray = keyboard.getKeyList().toArray(new Key[0]);
    keyboardFillPaint.setColor(keyboard.fillColour);
    if (shiftMode != SHIFT_PERSISTENT) {
      shiftMode = SHIFT_DISABLED;
    }
    requestLayout();
  }
  
  public void resetKeyRepeatIntervalMilliseconds() {
    keyRepeatIntervalMilliseconds = DEFAULT_KEY_REPEAT_INTERVAL_MILLISECONDS;
  }
  
  public void setKeyRepeatIntervalMilliseconds(final int milliseconds) {
    keyRepeatIntervalMilliseconds = milliseconds;
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
    
    final int screenWidth = keyboard.getScreenWidth();
    final int screenHeight = keyboard.getScreenHeight();
    
    keyPreviewPlane.updateDimensions(
      screenWidth,
      screenHeight,
      keyboardHeight
    );
    keyPreviewPlanePopup.setWidth(screenWidth);
    keyPreviewPlanePopup.setHeight(screenHeight);
    keyPreviewPlanePopup.showAtLocation(
      this,
      Gravity.NO_GRAVITY,
      0,
      keyboardHeight - screenHeight
    );
    
    setMeasuredDimension(keyboardWidth, keyboardHeight);
  }
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    if (keyboard == null) {
      return;
    }
    
    canvas.drawRect(keyboardRectangle, keyboardFillPaint);
    
    for (final Key key : keyArray) {
      
      keyRectangle.set(0, 0, key.width, key.height);
      
      int keyFillColour = key.fillColour;
      if (
        key == activeKey
          ||
        key.valueText.equals("SHIFT") && (
          shiftPointerId != NONEXISTENT_POINTER_ID
            ||
          shiftMode == SHIFT_PERSISTENT
            ||
          shiftMode == SHIFT_INITIATED
            ||
          shiftMode == SHIFT_HELD
        )
      )
      {
        keyFillColour = toPressedColour(keyFillColour);
      }
      
      keyFillPaint.setColor(keyFillColour);
      keyBorderPaint.setColor(key.borderColour);
      keyBorderPaint.setStrokeWidth(key.borderThickness);
      
      final int keyTextColour;
      if (key == activeKey && swipeModeIsActivated) {
        keyTextColour = key.textSwipeColour;
      }
      else {
        keyTextColour = key.textColour;
      }
      keyTextPaint.setColor(keyTextColour);
      keyTextPaint.setTextSize(key.textSize);
      
      final String keyDisplayText;
      if (debugModeIsActivated && key.valueText.equals("SPACE")) {
        keyDisplayText = (
          activeKey == null
            ? "null"
            : activeKey.valueText
        );
      }
      else {
        keyDisplayText = key.shiftAwareDisplayText(shiftMode);
      }
      
      final float keyTextX = (
        key.width / 2f
          + key.textOffsetX
      );
      final float keyTextY = (
        (key.height - keyTextPaint.ascent() - keyTextPaint.descent()) / 2f
          + key.textOffsetY
      );
      
      canvas.translate(key.x, key.y);
      
      canvas.drawRect(keyRectangle, keyFillPaint);
      canvas.drawRect(keyRectangle, keyBorderPaint);
      canvas.drawText(keyDisplayText, keyTextX, keyTextY, keyTextPaint);
      
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
  public static int toPressedColour(final int colour) {
    
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
      shiftPointerId = NONEXISTENT_POINTER_ID;
      activeKey = null;
      activePointerId = NONEXISTENT_POINTER_ID;
      keyPreviewPlane.dismissAllImmediately();
      invalidate();
      return true;
    }
    
    touchLogic:
    switch (event.getActionMasked()) {
      
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_POINTER_DOWN:
        
        final int downPointerIndex = event.getActionIndex();
        final int downPointerId = event.getPointerId(downPointerIndex);
        final int downPointerX = (int) event.getX(downPointerIndex);
        final int downPointerY = (int) event.getY(downPointerIndex);
        final Key downKey = getKeyAtPoint(downPointerX, downPointerY);
        
        if (isShiftKey(downKey)) {
          sendShiftDownEvent(downPointerId);
          break;
        }
        
        if (activePointerId != NONEXISTENT_POINTER_ID) {
          sendUpEvent(activeKey, false);
          keyPreviewPlane.dismissLatest();
        }
        sendDownEvent(downKey, downPointerId, downPointerX, downPointerY);
        break;
      
      case MotionEvent.ACTION_MOVE:
        
        for (int index = 0; index < eventPointerCount; index++) {
          
          final int movePointerId = event.getPointerId(index);
          final int movePointerX = (int) event.getX(index);
          final int movePointerY = (int) event.getY(index);
          final Key moveKey =
            getKeyAtPoint(movePointerX, movePointerY);
          
          if (movePointerId == activePointerId) {
            
            if (isShiftKey(moveKey) && !isSwipeableKey(activeKey)) {
              sendShiftMoveToEvent(movePointerId);
              break touchLogic;
            }
            
            if (moveKey != activeKey || isSwipeableKey(activeKey)) {
              sendMoveEvent(
                moveKey,
                movePointerId,
                movePointerX,
                movePointerY
              );
              break touchLogic;
            }
            
            break touchLogic;
          }
          
          if (movePointerId == shiftPointerId) {
            
            if (!isShiftKey(moveKey)) {
              sendShiftMoveFromEvent(
                moveKey,
                movePointerId,
                movePointerX,
                movePointerY
              );
              break touchLogic;
            }
          }
        }
        
        break;
      
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        
        final int upPointerIndex = event.getActionIndex();
        final int upPointerId = event.getPointerId(upPointerIndex);
        final int upPointerX = (int) event.getX(upPointerIndex);
        final int upPointerY = (int) event.getY(upPointerIndex);
        final Key upKey = getKeyAtPoint(upPointerX, upPointerY);
        
        if (
          (upPointerId == shiftPointerId || isShiftKey(upKey))
            &&
          !isSwipeableKey(activeKey)
        )
        {
          sendShiftUpEvent(true);
          break;
        }
        
        if (upPointerId == activePointerId) {
          sendUpEvent(upKey, true);
          break;
        }
        
        break;
    }
    
    return true;
  }
  
  private void sendDownEvent(
    final Key key,
    final int pointerId,
    final int x,
    final int y
  )
  {
    if (isSwipeableKey(key)) {
      pointerDownX = x;
    }
    swipeModeIsActivated = false;
    
    if (shiftPointerId != NONEXISTENT_POINTER_ID) {
      shiftMode = SHIFT_HELD;
    }
    
    activeKey = key;
    activePointerId = pointerId;
    activePointerX = x;
    activePointerY = y;
    
    sendAppropriateExtendedPressHandlerMessage(key);
    keyPreviewPlane.show(key);
    invalidate();
  }
  
  private void sendMoveEvent(
    final Key key,
    final int pointerId,
    final int x,
    final int y
  )
  {
    boolean shouldRedrawKeyboard = false;
    
    if (swipeModeIsActivated) {
      if (Math.abs(x - pointerDownX) < SWIPE_ACTIVATION_DISTANCE) {
        swipeModeIsActivated = false;
        shouldRedrawKeyboard = true;
      }
    }
    else if (key == activeKey && isSwipeableKey(key)) {
      if (Math.abs(x - pointerDownX) > SWIPE_ACTIVATION_DISTANCE) {
        swipeModeIsActivated = true;
        removeAllExtendedPressHandlerMessages();
        shouldRedrawKeyboard = true;
      }
    }
    else { // move is a key change
      activeKey = key;
      removeAllExtendedPressHandlerMessages();
      sendAppropriateExtendedPressHandlerMessage(key);
      resetKeyRepeatIntervalMilliseconds();
      keyPreviewPlane.move(key);
      shouldRedrawKeyboard = true;
    }
    
    activePointerId = pointerId;
    activePointerX = x;
    activePointerY = y;
    
    if (shouldRedrawKeyboard) {
      invalidate();
    }
  }
  
  private void sendUpEvent(
    final Key key,
    final boolean shouldRedrawKeyboard
  )
  {
    if (swipeModeIsActivated) {
      inputListener.onSwipe(activeKey.valueText);
    }
    else if (key != null) {
      
      if (shiftMode != SHIFT_DISABLED && key.isShiftable) {
        inputListener.onKey(key.valueTextShifted);
      }
      else {
        inputListener.onKey(key.valueText);
      }
      
      if (shiftMode == SHIFT_SINGLE) {
        shiftMode = SHIFT_DISABLED;
      }
    }
    
    activeKey = null;
    activePointerId = NONEXISTENT_POINTER_ID;
    
    removeAllExtendedPressHandlerMessages();
    resetKeyRepeatIntervalMilliseconds();
    if (shouldRedrawKeyboard) {
      keyPreviewPlane.dismissLatest();
      invalidate();
    }
  }
  
  private void sendShiftDownEvent(final int pointerId) {
    
    if (shiftMode == SHIFT_DISABLED) {
      shiftMode = (
        activeKey == null
          ? SHIFT_INITIATED
          : SHIFT_HELD
      );
    }
    shiftPointerId = pointerId;
    
    //updateKeyPreview();
    invalidate();
  }
  
  private void sendShiftMoveToEvent(final int pointerId) {
    
    shiftMode = SHIFT_HELD;
    shiftPointerId = pointerId;
    
    activeKey = null;
    activePointerId = NONEXISTENT_POINTER_ID;
    
    removeAllExtendedPressHandlerMessages();
    //updateKeyPreview();
    invalidate();
  }
  
  private void sendShiftMoveFromEvent(
    final Key key,
    final int pointerId,
    final int x,
    final int y
  )
  {
    sendShiftUpEvent(false);
    
    activeKey = key;
    activePointerId = pointerId;
    activePointerX = x;
    activePointerY = y;
    
    removeAllExtendedPressHandlerMessages();
    sendAppropriateExtendedPressHandlerMessage(key);
    resetKeyRepeatIntervalMilliseconds();
    keyPreviewPlane.move(key);
    invalidate();
  }
  
  private void sendShiftUpEvent(boolean shouldRedrawKeyboard) {
    
    switch (shiftMode) {
      case SHIFT_SINGLE:
        shiftMode = SHIFT_PERSISTENT;
        break;
      case SHIFT_INITIATED:
        shiftMode = SHIFT_SINGLE;
        break;
      case SHIFT_PERSISTENT:
      case SHIFT_HELD:
        shiftMode = SHIFT_DISABLED;
        break;
    }
    shiftPointerId = NONEXISTENT_POINTER_ID;
    
    if (shouldRedrawKeyboard) {
      //updateKeyPreview();
      invalidate();
    }
  }
  
  private Key getKeyAtPoint(final int x, final int y) {
    
    for (final Key key : keyArray) {
      if (key.containsPoint(x, y)) {
        return key;
      }
    }
    
    return null;
  }
  
  private boolean isShiftKey(final Key key) {
    return key != null && key.valueText.equals("SHIFT");
  }
  
  private boolean isSwipeableKey(final Key key) {
    return key != null && key.isSwipeable;
  }
  
  private void sendAppropriateExtendedPressHandlerMessage(
    final Key key
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
    extendedPressHandler.removeCallbacksAndMessages(null);
  }
  
  private void showDebugToast(final String message) {
    debugToast.setText(message);
    debugToast.show();
  }
  
}
