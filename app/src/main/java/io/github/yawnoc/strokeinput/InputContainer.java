/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This file contains bytes copied from the deprecated `KeyboardView` class,
  which is licensed under the Apache License 2.0,
  see <https://www.apache.org/licenses/LICENSE-2.0.html>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;

/*
  A container that holds:
    - Candidates bar
    - Keyboard
  TODO:
    - Candidates
    - Increase backspace speed for ASCII text
*/
public class InputContainer
  extends View
  implements View.OnClickListener
{
  private static final int NONEXISTENT_POINTER_ID = -1;
  
  private static final int MESSAGE_KEY_REPEAT = 1;
  private static final int MESSAGE_LONG_PRESS = 2;
  private static final int DEFAULT_KEY_REPEAT_INTERVAL_MILLISECONDS = 100;
  private static final int KEY_REPEAT_START_MILLISECONDS = 500;
  private static final int KEY_LONG_PRESS_MILLISECONDS = 750;
  
  private static final int SWIPE_ACTIVATION_DISTANCE = 40;
  
  public static final int SHIFT_DISABLED = 0;
  public static final int SHIFT_SINGLE = 1;
  public static final int SHIFT_PERSISTENT = 2;
  
  private static final float COLOUR_LIGHTNESS_CUTOFF = 0.7f;
  
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
  private int shiftMode;
  
  // Keyboard drawing
  private final Rect keyRectangle;
  private final Paint keyFillPaint;
  private final Paint keyBorderPaint;
  private final Paint keyTextPaint;
  
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
                inputListener.onKey(
                  currentlyPressedKey.valueText,
                  currentlyPressedKey.valueTextShifted
                );
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
  }
  
  /*
    A listener for input events.
  */
  public interface OnInputListener {
    void onKey(String valueText, String valueTextShifted);
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
    keyArray = keyboard.getKeyList().toArray(new Keyboard.Key[0]);
    requestLayout();
  }
  
  public void resetKeyRepeatIntervalMilliseconds() {
    keyRepeatIntervalMilliseconds = DEFAULT_KEY_REPEAT_INTERVAL_MILLISECONDS;
  }
  
  public int getShiftMode() {
    return shiftMode;
  }
  
  public void setShiftMode(final int state) {
    shiftMode = state;
  }
  
  public void onClick(final View view) {
  }
  
  @Override
  public void onMeasure(
    final int widthMeasureSpec,
    final int heightMeasureSpec
  )
  {
    final int paddingHorizontal = getPaddingLeft() + getPaddingRight();
    final int paddingVertical = getPaddingTop() + getPaddingBottom();
    
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
    
    setMeasuredDimension(
      keyboardWidth + paddingHorizontal,
      keyboardHeight + paddingVertical
    );
  }
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    if (keyboard == null) {
      return;
    }
    
    canvas.drawColor(keyboard.fillColour);
    
    for (final Keyboard.Key key : keyArray) {
      
      keyRectangle.set(0, 0, key.width, key.height);
      
      int keyFillColour = key.keyFillColour;
      if (
        key == currentlyPressedKey
          ||
        key.valueText.equals("SHIFT") && getShiftMode() == SHIFT_PERSISTENT
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
      if (getShiftMode() == SHIFT_DISABLED) {
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
  public boolean onTouchEvent(final MotionEvent motionEvent) {
    
    if (motionEvent.getPointerCount() > 2) {
      abortAllKeyBehaviour();
      return true;
    }
    
    final long eventTime = motionEvent.getEventTime();
    final int eventAction = motionEvent.getActionMasked();
    final int eventActionIndex = motionEvent.getActionIndex();
    final int eventPointerId = motionEvent.getPointerId(eventActionIndex);
    final int eventPointerIndex = motionEvent.findPointerIndex(eventPointerId);
    final int eventPointerX = (int) motionEvent.getX(eventPointerIndex);
    final int eventPointerY = (int) motionEvent.getY(eventPointerIndex);
    final int eventMetaState = motionEvent.getMetaState();
    
    boolean eventHandled = true;
    
    switch (eventAction) {
      
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_POINTER_DOWN:
        if (
          eventPointerId != activePointerId
            &&
          activePointerId != NONEXISTENT_POINTER_ID
        )
        {
          // Send an up event for the active pointer
          sendSinglePointerMotionEvent(
            eventTime,
            MotionEvent.ACTION_UP,
            activePointerX,
            activePointerY,
            eventMetaState
          );
        }
        // Send a down event for the event pointer
        eventHandled =
          sendSinglePointerMotionEvent(
            eventTime,
            MotionEvent.ACTION_DOWN,
            eventPointerX,
            eventPointerY,
            eventMetaState
          );
        // Update the active pointer
        activePointerId = eventPointerId;
        activePointerX = eventPointerX;
        activePointerY = eventPointerY;
        break;
      
      case MotionEvent.ACTION_MOVE:
        if (
          eventPointerId == activePointerId
            &&
          (
            getKeyAtPoint(eventPointerX, eventPointerY) != currentlyPressedKey
              ||
            currentlyPressedKey != null && currentlyPressedKey.isSwipeable
          )
        )
        {
          // Send a move event for the event pointer
          eventHandled =
            sendSinglePointerMotionEvent(
              eventTime,
              MotionEvent.ACTION_MOVE,
              eventPointerX,
              eventPointerY,
              eventMetaState
            );
          // Update the active pointer
          activePointerId = eventPointerId;
          activePointerX = eventPointerX;
          activePointerY = eventPointerY;
        }
        break;
      
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        if (eventPointerId == activePointerId) {
          // Send an up event for the event pointer
          eventHandled =
            sendSinglePointerMotionEvent(
              eventTime,
              MotionEvent.ACTION_UP,
              eventPointerX,
              eventPointerY,
              eventMetaState
            );
          // Unset the active pointer
          activePointerId = NONEXISTENT_POINTER_ID;
        }
        break;
    }
    
    return eventHandled;
  }
  
  private boolean sendSinglePointerMotionEvent(
    final long time,
    final int action,
    final int x,
    final int y,
    final int metaState
  )
  {
    final MotionEvent sentEvent =
      MotionEvent.obtain(time, time, action, x, y, metaState);
    
    return onSinglePointerTouchEvent(sentEvent);
  }
  
  private boolean onSinglePointerTouchEvent(final MotionEvent motionEvent) {
    
    final int eventX = (int) motionEvent.getX() - getPaddingLeft();
    final int eventY = (int) motionEvent.getY() - getPaddingTop();
    
    final Keyboard.Key key;
    if (swipeModeIsActivated) {
      key = currentlyPressedKey;
    }
    else {
      key = getKeyAtPoint(eventX, eventY);
    }
    if (key == null) {
      abortAllKeyBehaviour();
      return true;
    }
    final String valueText = key.valueText;
    final String valueTextShifted = key.valueTextShifted;
    
    final int eventAction = motionEvent.getAction();
    
    switch (eventAction) {
      
      case MotionEvent.ACTION_DOWN:
        setCurrentlyPressedKey(key);
        sendAppropriateExtendedPressHandlerMessage(key);
        deactivateSwipeMode();
        if (key.isSwipeable) {
          pointerDownX = eventX;
        }
        break;
      
      case MotionEvent.ACTION_MOVE:
        if (swipeModeIsActivated) {
          if (Math.abs(eventX - pointerDownX) < SWIPE_ACTIVATION_DISTANCE) {
            deactivateSwipeMode();
          }
        }
        else {
          if (key == currentlyPressedKey && key.isSwipeable) {
            if (Math.abs(eventX - pointerDownX) > SWIPE_ACTIVATION_DISTANCE) {
              activateSwipeMode();
              removeAllExtendedPressHandlerMessages();
            }
          }
          else { // move is a key change
            removeAllExtendedPressHandlerMessages();
            setCurrentlyPressedKey(key);
            sendAppropriateExtendedPressHandlerMessage(key);
          }
        }
        break;
      
      case MotionEvent.ACTION_UP:
        removeAllExtendedPressHandlerMessages();
        setCurrentlyPressedKey(null);
        if (swipeModeIsActivated) {
          inputListener.onSwipe(valueText);
          deactivateSwipeMode();
        }
        else {
          inputListener.onKey(valueText, valueTextShifted);
        }
        break;
    }
    
    return true;
  }
  
  private Keyboard.Key getKeyAtPoint(final int x, final int y) {
    
    for (final Keyboard.Key key : keyArray) {
      if (key.containsPoint(x, y)) {
        return key;
      }
    }
    
    return null;
  }
  
  private void setCurrentlyPressedKey(final Keyboard.Key key) {
    currentlyPressedKey = key;
    invalidate();
  }
  
  private void activateSwipeMode() {
    swipeModeIsActivated = true;
    invalidate();
  }
  
  private void deactivateSwipeMode() {
    swipeModeIsActivated = false;
    invalidate();
  }
  
  private void sendAppropriateExtendedPressHandlerMessage(
    final Keyboard.Key key
  )
  {
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
    setCurrentlyPressedKey(null);
    activePointerId = NONEXISTENT_POINTER_ID;
  }
}
