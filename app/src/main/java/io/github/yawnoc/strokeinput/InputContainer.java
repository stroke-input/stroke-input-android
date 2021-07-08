/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This is a re-implementation of the deprecated `KeyboardView` class,
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
    - Switch to symbols keyboard
    - Candidates
*/
public class InputContainer
  extends View
  implements View.OnClickListener
{
  private static final int NONEXISTENT_POINTER_ID = -1;
  
  private static final int MESSAGE_KEY_REPEAT = 1;
  private static final int MESSAGE_LONG_PRESS = 2;
  private static final int KEY_REPEAT_INTERVAL_MILLISECONDS = 100;
  private static final int KEY_REPEAT_START_MILLISECONDS = 500;
  private static final int KEY_LONG_PRESS_MILLISECONDS = 750;
  
  private static final float COLOUR_LIGHTNESS_CUTOFF = 0.7f;
  
  // Container meta-properties
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
  
  // Keyboard drawing
  private final Rect keyRectangle;
  private final Paint keyFillPaint;
  private final Paint keyBorderPaint;
  private final Paint keyTextPaint;
  
  public InputContainer(Context context, AttributeSet attributes) {
    
    super(context, attributes);
    
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
                  KEY_REPEAT_INTERVAL_MILLISECONDS
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
    
    /*
      Send a key event for a key.
    */
    void onKey(String valueText);
    
    /*
      Send a long press event for a key.
    */
    void onLongPress(String valueText);
  }
  
  public void setOnInputListener(OnInputListener listener) {
    inputListener = listener;
  }
  
  public void setKeyboard(Keyboard keyboard) {
    this.keyboard = keyboard;
    keyArray = keyboard.getKeyList().toArray(new Keyboard.Key[0]);
    requestLayout();
  }
  
  public void onClick(View view) {
  }
  
  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    
    int paddingHorizontal = getPaddingLeft() + getPaddingRight();
    int paddingVertical = getPaddingTop() + getPaddingBottom();
    
    int keyboardWidth;
    int keyboardHeight;
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
  public void onDraw(Canvas canvas) {
    
    if (keyboard == null) {
      return;
    }
    
    canvas.drawColor(keyboard.fillColour);
    
    for (final Keyboard.Key key : keyArray) {
      
      keyRectangle.set(0, 0, key.width, key.height);
      
      int key_fill_colour = key.keyFillColour;
      if (key == currentlyPressedKey) {
        key_fill_colour = getContrastingColour(key_fill_colour);
      }
      
      keyFillPaint.setColor(key_fill_colour);
      keyBorderPaint.setColor(key.keyBorderColour);
      keyBorderPaint.setStrokeWidth(key.keyBorderThickness);
      
      keyTextPaint.setColor(key.keyTextColour);
      keyTextPaint.setTextSize(key.keyTextSize);
      
      float key_text_x = (
        key.width / 2f
          + key.keyTextOffsetX
      );
      float key_text_y = (
        (key.height - keyTextPaint.ascent() - keyTextPaint.descent()) / 2f
          + key.keyTextOffsetY
      );
      
      canvas.translate(key.x, key.y);
      
      canvas.drawRect(keyRectangle, keyFillPaint);
      canvas.drawRect(keyRectangle, keyBorderPaint);
      canvas.drawText(
        key.displayText,
        key_text_x,
        key_text_y,
        keyTextPaint
      );
      
      canvas.translate(-key.x, -key.y);
    }
    
  }
  
  /*
    Lighten a dark colour and darken a light colour.
    Used for key press colour changes.
  */
  private static int getContrastingColour(int colour) {
    
    float[] colourHSL = new float[3];
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
    // TODO: fix unexpected behaviour when two pointers merge
  */
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent motionEvent) {
    
    if (motionEvent.getPointerCount() > 2) {
      abortAllKeyBehaviour();
      return true;
    }
    
    long eventTime = motionEvent.getEventTime();
    int eventAction = motionEvent.getActionMasked();
    int eventActionIndex = motionEvent.getActionIndex();
    int eventPointerId = motionEvent.getPointerId(eventActionIndex);
    int eventPointerIndex = motionEvent.findPointerIndex(eventPointerId);
    int eventPointerX = (int) motionEvent.getX(eventPointerIndex);
    int eventPointerY = (int) motionEvent.getY(eventPointerIndex);
    int eventMetaState = motionEvent.getMetaState();
    
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
          getKeyAtPoint(eventPointerX, eventPointerY) != currentlyPressedKey
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
    long time,
    int action,
    int x,
    int y,
    int metaState
  )
  {
    MotionEvent sentEvent =
      MotionEvent.obtain(time, time, action, x, y, metaState);
    
    return onSinglePointerTouchEvent(sentEvent);
  }
  
  private boolean onSinglePointerTouchEvent(MotionEvent motionEvent) {
    
    int eventX = (int) motionEvent.getX() - getPaddingLeft();
    int eventY = (int) motionEvent.getY() - getPaddingTop();
    
    Keyboard.Key key = getKeyAtPoint(eventX, eventY);
    if (key == null) {
      abortAllKeyBehaviour();
      return true;
    }
    String valueText = key.valueText;
    
    int eventAction = motionEvent.getAction();
    
    switch (eventAction) {
      
      case MotionEvent.ACTION_DOWN:
        setCurrentlyPressedKey(key);
        sendAppropriateExtendedPressHandlerMessage(key);
        break;
      
      case MotionEvent.ACTION_MOVE:
        removeAllExtendedPressHandlerMessages();
        setCurrentlyPressedKey(key);
        sendAppropriateExtendedPressHandlerMessage(key);
        break;
      
      case MotionEvent.ACTION_UP:
        removeAllExtendedPressHandlerMessages();
        setCurrentlyPressedKey(null);
        inputListener.onKey(valueText);
        break;
    }
    
    return true;
  }
  
  private Keyboard.Key getKeyAtPoint(int x, int y) {
    
    for (Keyboard.Key key : keyArray) {
      if (key.containsPoint(x, y)) {
        return key;
      }
    }
    
    return null;
  }
  
  private void setCurrentlyPressedKey(Keyboard.Key key) {
    currentlyPressedKey = key;
    invalidate();
  }
  
  private void sendAppropriateExtendedPressHandlerMessage(Keyboard.Key key) {
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
    int messageWhat,
    long delayMilliseconds
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
  
  private void removeExtendedPressHandlerMessages(int messageWhat) {
    extendedPressHandler.removeMessages(messageWhat);
  }
  
  private void abortAllKeyBehaviour() {
    setCurrentlyPressedKey(null);
    activePointerId = NONEXISTENT_POINTER_ID;
  }
}
