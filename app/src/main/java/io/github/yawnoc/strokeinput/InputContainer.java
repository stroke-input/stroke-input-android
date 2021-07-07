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
    - Change appearance of currently pressed key
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
  private static final int NONEXISTENT_KEY_INDEX = -1;
  private static final int NONEXISTENT_POINTER_ID = -1;
  
  private static final int DEFAULT_KEY_ALPHA = 0xFF;
  private static final int PRESSED_KEY_ALPHA = 0x7F;
  
  // Container meta-properties
  private OnInputListener inputListener;
  private Keyboard keyboard;
  private Keyboard.Key[] keyArray;
  
  // Pointer properties
  private int activePointerId = NONEXISTENT_POINTER_ID;
  private int activePointerX;
  private int activePointerY;
  
  // Keyboard drawing
  private final Rect keyRectangle;
  private final Paint keyFillPaint;
  private final Paint keyBorderPaint;
  private final Paint keyTextPaint;
  
  public InputContainer(Context context, AttributeSet attributes) {
    
    super(context, attributes);
    
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
          key.isPressed ? PRESSED_KEY_ALPHA : DEFAULT_KEY_ALPHA
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
  */
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent motionEvent) {
    
    if (motionEvent.getPointerCount() > 2) {
      // Unset the active pointer and abort
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
    
    MotionEvent sentEvent;
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
          sentEvent =
            MotionEvent.obtain(
              eventTime,
              eventTime,
              MotionEvent.ACTION_UP,
              activePointerX,
              activePointerY,
              eventMetaState
            );
          onTouchEventSinglePointer(sentEvent);
        }
        // Send a down event for the event pointer
        sentEvent =
          MotionEvent.obtain(
            eventTime,
            eventTime,
            MotionEvent.ACTION_DOWN,
            eventPointerX,
            eventPointerY,
            eventMetaState
          );
        eventHandled = onTouchEventSinglePointer(sentEvent);
        // Update the active pointer
        activePointerId = eventPointerId;
        activePointerX = eventPointerX;
        activePointerY = eventPointerY;
        break;
      
      case MotionEvent.ACTION_MOVE:
        break;
      
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        if (eventPointerId == activePointerId) {
          // Send an up event for the event pointer
          sentEvent =
            MotionEvent.obtain(
              eventTime,
              eventTime,
              MotionEvent.ACTION_UP,
              eventPointerX,
              eventPointerY,
              eventMetaState
            );
          eventHandled = onTouchEventSinglePointer(sentEvent);
          // Unset the active pointer
          activePointerId = NONEXISTENT_POINTER_ID;
        }
        break;
    }
    
    return eventHandled;
  }
  
  private boolean onTouchEventSinglePointer(MotionEvent motionEvent) {
    
    int touchX = (int) motionEvent.getX() - getPaddingLeft();
    int touchY = (int) motionEvent.getY() - getPaddingTop();
    int keyIndex = getKeyIndexAtPoint(touchX, touchY);
    Keyboard.Key key = keyArray[keyIndex];
    String valueText = key.valueText;
    
    int eventAction = motionEvent.getAction();
    
    switch (eventAction) {
      
      case MotionEvent.ACTION_DOWN:
        setKeyPressedState(key, true);
        break;
      
      case MotionEvent.ACTION_UP:
        setKeyPressedState(key, false);
        inputListener.onKey(valueText);
        break;
      
      case MotionEvent.ACTION_MOVE:
        break;
    }
    
    return true;
  }
  
  public int getKeyIndexAtPoint(int x, int y) {
    
    int keyCount = keyArray.length;
    Keyboard.Key key;
    
    for (int keyIndex = 0; keyIndex < keyCount; keyIndex++) {
      key = keyArray[keyIndex];
      if (key.containsPoint(x, y)) {
        return keyIndex;
      }
    }
    
    return NONEXISTENT_KEY_INDEX;
  }
  
  public void setKeyPressedState(Keyboard.Key key, boolean state) {
    key.setPressedState(state);
    invalidate();
  }
}
