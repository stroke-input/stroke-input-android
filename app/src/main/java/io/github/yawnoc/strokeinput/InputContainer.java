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
    - Cancel key press if move outside keyboard
    - Long press space for switching keyboard
    - Backspace
    - Switch to symbols keyboard
    - Candidates
*/
public class InputContainer
  extends View
  implements View.OnClickListener
{
  private static final int NONEXISTENT_POINTER_ID = -1;
  
  private static final int MESSAGE_KEY_REPEAT = 1;
  private static final int KEY_REPEAT_INTERVAL_MILLISECONDS = 100;
  private static final int KEY_REPEAT_START_MILLISECONDS = 500;
  
  private static final int DEFAULT_KEY_ALPHA = 0xFF;
  private static final int PRESSED_KEY_ALPHA = 0x7F;
  
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
          switch (message.what) {
            case MESSAGE_KEY_REPEAT:
              if (currentlyPressedKey != null) {
                inputListener.onKey(currentlyPressedKey.valueText);
                sendMessageExtendedPressHandler(
                  MESSAGE_KEY_REPEAT,
                  KEY_REPEAT_INTERVAL_MILLISECONDS
                );
              }
              break;
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
    
    super.onDraw(canvas);
    
    if (keyboard == null) {
      return;
    }
    
    for (final Keyboard.Key key : keyArray) {
      
      keyRectangle.set(0, 0, key.width, key.height);
      
      int key_fill_colour =
        ColorUtils.setAlphaComponent(
          key.keyFillColour,
          key == currentlyPressedKey ? PRESSED_KEY_ALPHA : DEFAULT_KEY_ALPHA
        );
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
    Handle logic for multiple pointers (e.g. two-thumb typing).
    // TODO: fix unexpected behaviour when two pointers merge
  */
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent motionEvent) {
    
    if (motionEvent.getPointerCount() > 2) {
      // Unset the pressed key & active pointer and abort
      setPressedKey(null);
      activePointerId = NONEXISTENT_POINTER_ID;
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
          sendMotionEventSinglePointer(
            eventTime,
            MotionEvent.ACTION_UP,
            activePointerX,
            activePointerY,
            eventMetaState
          );
        }
        // Send a down event for the event pointer
        eventHandled =
          sendMotionEventSinglePointer(
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
            sendMotionEventSinglePointer(
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
            sendMotionEventSinglePointer(
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
  
  private boolean sendMotionEventSinglePointer(
    long time,
    int action,
    int x,
    int y,
    int metaState
  )
  {
    MotionEvent sentEvent =
      MotionEvent.obtain(time, time, action, x, y, metaState);
    
    return onTouchEventSinglePointer(sentEvent);
  }
  
  private boolean onTouchEventSinglePointer(MotionEvent motionEvent) {
    
    int touchX = (int) motionEvent.getX() - getPaddingLeft();
    int touchY = (int) motionEvent.getY() - getPaddingTop();
    Keyboard.Key key = getKeyAtPoint(touchX, touchY);
    String valueText = key.valueText;
    
    int eventAction = motionEvent.getAction();
    
    switch (eventAction) {
      
      case MotionEvent.ACTION_DOWN:
        // TODO: long press behaviour
        setPressedKey(key);
        if (key.isRepeatable) {
          sendMessageExtendedPressHandler(
            MESSAGE_KEY_REPEAT,
            KEY_REPEAT_START_MILLISECONDS
          );
        }
        break;
      
      case MotionEvent.ACTION_MOVE:
        setPressedKey(key);
        break;
      
      case MotionEvent.ACTION_UP:
        setPressedKey(null);
        inputListener.onKey(valueText);
        break;
    }
    
    return true;
  }
  
  public Keyboard.Key getKeyAtPoint(int x, int y) {
    
    for (Keyboard.Key key : keyArray) {
      if (key.containsPoint(x, y)) {
        return key;
      }
    }
    
    return null;
  }
  
  public void setPressedKey(Keyboard.Key key) {
    currentlyPressedKey = key;
    invalidate();
  }
  
  private void sendMessageExtendedPressHandler(
    int messageWhat,
    long delayMilliseconds
  )
  {
    extendedPressHandler.sendMessageDelayed(
      extendedPressHandler.obtainMessage(messageWhat),
      delayMilliseconds
    );
  }
}
