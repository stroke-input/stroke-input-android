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

import java.util.List;

/*
  A container that holds:
    - Candidates bar
    - Keyboard
  TODO:
    - Change appearance of currently pressed key
    - Cancel key press if move outside keyboard
    - Long press space for switching keyboard
    - Backspace
    - Enter key
    - Switch to symbols keyboard
    - Candidates
*/
public class InputContainer
  extends View
  implements View.OnClickListener
{
  private static final int NONEXISTENT_KEY_INDEX = -1;
  
  // Container meta-properties
  private OnInputListener inputListener;
  private Keyboard keyboard;
  private Keyboard.Key[] keyArray;
  
  // Pointer properties
  private int pointerCount = 1;
  private float pointerX;
  private float pointerY;
  
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
    List<Keyboard.Key> keyList = this.keyboard.getKeyList();
    keyArray = keyList.toArray(new Keyboard.Key[0]);
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
      
      keyFillPaint.setColor(key.keyFillColour);
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
    
    int newPointerCount = motionEvent.getPointerCount();
    int eventAction = motionEvent.getAction();
    long eventTime = motionEvent.getEventTime();
    boolean eventHandled;
    
    if (newPointerCount == pointerCount) {
      
      // Still a single pointer as before (e.g. release)
      if (newPointerCount == 1) {
        eventHandled = onTouchEventSinglePointer(motionEvent);
        pointerX = motionEvent.getX();
        pointerY = motionEvent.getY();
      }
      
      // Still multiple pointers as before (e.g. moving pointers)
      else {
        eventHandled = true; // do nothing
      }
    }
    
    else {
      
      // Changed to a single pointer (e.g. press)
      if (newPointerCount == 1) {
        // Send a down event for the new pointer
        MotionEvent downEvent =
          MotionEvent.obtain(
            eventTime,
            eventTime,
            MotionEvent.ACTION_DOWN,
            motionEvent.getX(),
            motionEvent.getY(),
            motionEvent.getMetaState()
          );
        eventHandled = onTouchEventSinglePointer(downEvent);
        downEvent.recycle();
        // Send an up event too if appropriate
        if (eventAction == MotionEvent.ACTION_UP) {
          eventHandled = onTouchEventSinglePointer(motionEvent);
        }
      }
      
      // Changed to multiple pointers (e.g. second-thumb press)
      else {
        // Send an up event for the existing pointer
        MotionEvent upEvent =
          MotionEvent.obtain(
            eventTime,
            eventTime,
            MotionEvent.ACTION_UP,
            pointerX,
            pointerY,
            motionEvent.getMetaState()
          );
        eventHandled = onTouchEventSinglePointer(motionEvent);
        upEvent.recycle();
      }
      
    }
    
    pointerCount = newPointerCount;
    
    return eventHandled;
  }
  
  private boolean onTouchEventSinglePointer(MotionEvent motionEvent) {
    
    int touchX = (int) motionEvent.getX() - getPaddingLeft();
    int touchY = (int) motionEvent.getY() - getPaddingTop();
    int keyIndex = getKeyIndexAtPoint(touchX, touchY);
    String valueText = keyArray[keyIndex].valueText;
    
    int eventAction = motionEvent.getAction();
    
    switch (eventAction) {
      case MotionEvent.ACTION_DOWN:
        break;
      case MotionEvent.ACTION_UP:
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
  
}
