/*
  Copyright 2021--2023 Conway
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import java.util.List;

/*
  A view that holds a keyboard.
  Touch logic is implemented here.
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

  public static final String KEYBOARD_FONT_FILE_NAME = "StrokeInputFont.ttf";
  private static final float COLOUR_LIGHTNESS_CUTOFF = 0.7f;

  // View properties
  private KeyboardListener keyboardListener;
  private LinearLayout mainInputPlane;
  private KeyPreviewPlane keyPreviewPlane;
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
  private Rect keyboardRectangle;
  private Paint keyboardFillPaint;

  // Key drawing
  private Rect keyRectangle;
  private Paint keyFillPaint;
  private Paint keyBorderPaint;
  private Paint keyTextPaint;

  public KeyboardView(final Context context, final AttributeSet attributes)
  {
    super(context, attributes);

    initialiseExtendedPressHandler();
    initialiseDrawing(context);
  }

  private void initialiseExtendedPressHandler()
  {
    resetKeyRepeatIntervalMilliseconds();

    extendedPressHandler =
            new Handler(Looper.getMainLooper())
            {
              @Override
              public void handleMessage(@NonNull Message message)
              {
                if (activeKey != null)
                {
                  switch (message.what)
                  {
                    case MESSAGE_KEY_REPEAT:
                      keyboardListener.onKey(activeKey.valueText);
                      sendExtendedPressHandlerMessage(MESSAGE_KEY_REPEAT, keyRepeatIntervalMilliseconds);
                      break;

                    case MESSAGE_LONG_PRESS:
                      keyboardListener.onLongPress(activeKey.valueText);
                      activeKey = null;
                      activePointerId = NONEXISTENT_POINTER_ID;
                      keyPreviewPlane.dismissAllImmediately();
                      invalidate();
                      break;
                  }
                }
              }
            };

  }

  private void initialiseDrawing(final Context context)
  {
    this.setBackgroundColor(Color.TRANSPARENT);

    keyboardRectangle = new Rect();
    keyboardFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    keyRectangle = new Rect();

    keyFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyFillPaint.setStyle(Paint.Style.FILL);

    keyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyBorderPaint.setStyle(Paint.Style.STROKE);

    keyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), KEYBOARD_FONT_FILE_NAME));
    keyTextPaint.setTextAlign(Paint.Align.CENTER);
  }

  /*
    A listener for keyboard events.
  */
  public interface KeyboardListener
  {
    void onKey(String valueText);
    void onLongPress(String valueText);
    void onSwipe(String valueText);
    void saveKeyboard(Keyboard keyboard);
  }

  public void setKeyboardListener(final KeyboardListener keyboardListener)
  {
    this.keyboardListener = keyboardListener;
  }

  public void setMainInputPlane(final LinearLayout mainInputPlane)
  {
    this.mainInputPlane = mainInputPlane;
  }

  public void setKeyPreviewPlane(final KeyPreviewPlane keyPreviewPlane)
  {
    this.keyPreviewPlane = keyPreviewPlane;
  }

  public Keyboard getKeyboard()
  {
    return keyboard;
  }

  public void setKeyboard(final Keyboard keyboard)
  {
    keyboardListener.saveKeyboard(keyboard);
    this.keyboard = keyboard;
    keyList = keyboard.getKeyList();
    keyboardFillPaint.setColor(keyboard.fillColour);
    if (shiftMode != SHIFT_PERSISTENT)
    {
      shiftMode = SHIFT_DISABLED;
      keyPreviewPlane.updateShiftMode(shiftMode);
    }
    requestLayout();
  }

  public void resetKeyRepeatIntervalMilliseconds()
  {
    keyRepeatIntervalMilliseconds = DEFAULT_KEY_REPEAT_INTERVAL_MILLISECONDS;
  }

  public void setKeyRepeatIntervalMilliseconds(final int milliseconds)
  {
    keyRepeatIntervalMilliseconds = milliseconds;
  }

  @Override
  public void onClick(final View view)
  {
    // Touch logic implemented in onTouchEvent
  }

  @Override
  public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
  {
    final int keyboardWidth;
    final int keyboardHeight;
    if (keyboard != null)
    {
      keyboardWidth = keyboard.getWidth();
      keyboardHeight = keyboard.getHeight();
    }
    else
    {
      keyboardWidth = 0;
      keyboardHeight = 0;
    }
    keyboardRectangle.set(0, 0, keyboardWidth, keyboardHeight);

    final int mainInputPlaneWidth = mainInputPlane.getMeasuredWidth();
    final int mainInputPlaneHeight = mainInputPlane.getMeasuredHeight();
    keyPreviewPlane.updateDimensions(mainInputPlaneWidth, mainInputPlaneHeight, keyboardHeight);

    setMeasuredDimension(keyboardWidth, keyboardHeight);
  }

  @Override
  public void onDraw(@NonNull final Canvas canvas)
  {
    if (keyboard == null)
    {
      return;
    }

    canvas.drawRect(keyboardRectangle, keyboardFillPaint);

    for (final Key key : keyList)
    {
      keyRectangle.set(0, 0, key.width, key.height);

      int keyFillColour = key.fillColour;
      if (
        key == activeKey
          ||
        key.valueText.equals(StrokeInputService.SHIFT_KEY_VALUE_TEXT) && (
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
      if (key == activeKey && swipeModeIsActivated)
      {
        keyTextColour = key.textSwipeColour;
      }
      else
      {
        keyTextColour = key.textColour;
      }
      keyTextPaint.setColor(keyTextColour);
      keyTextPaint.setTextSize(key.textSize);

      final String keyDisplayText =
              (key.valueText.equals(StrokeInputService.ENTER_KEY_VALUE_TEXT))
                ? key.displayText
                : key.shiftAwareDisplayText(shiftMode);

      final float keyTextX = key.width / 2f + key.textOffsetX;
      final float keyTextY = (key.height - keyTextPaint.ascent() - keyTextPaint.descent()) / 2f + key.textOffsetY;

      canvas.translate(key.x, key.y);
      canvas.drawRect(keyRectangle, keyFillPaint);
      canvas.drawRect(keyRectangle, keyBorderPaint);
      canvas.drawText(keyDisplayText, keyTextX, keyTextY, keyTextPaint);
      canvas.translate(-key.x, -key.y);
    }
  }

  /*
    Lighten a dark colour and darken a light colour.
    Used for key press colour changes.
  */
  public static int toPressedColour(final int colour)
  {
    final float[] colourHSLArray = new float[3];
    ColorUtils.colorToHSL(colour, colourHSLArray);

    float colourLightness = colourHSLArray[2];

    if (colourLightness < COLOUR_LIGHTNESS_CUTOFF)
    {
      colourLightness = (2 * colourLightness + 1) / 3;
    }
    else
    {
      colourLightness = (2 * colourLightness) / 3;
    }

    colourHSLArray[2] = colourLightness;

    return ColorUtils.HSLToColor(colourHSLArray);
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
  public boolean onTouchEvent(final MotionEvent event)
  {
    final int eventPointerCount = event.getPointerCount();

    if (eventPointerCount > 2)
    {
      sendCancelEvent();
      return true;
    }

    touchLogic:
    switch (event.getActionMasked())
    {
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_POINTER_DOWN:

        final int downPointerIndex = event.getActionIndex();
        final int downPointerId = event.getPointerId(downPointerIndex);
        final int downPointerX = (int) event.getX(downPointerIndex);
        final int downPointerY = (int) event.getY(downPointerIndex);
        final Key downKey = getKeyAtPoint(downPointerX, downPointerY);

        if (isShiftKey(downKey))
        {
          sendShiftDownEvent(downPointerId);
          break;
        }

        if (activePointerId != NONEXISTENT_POINTER_ID)
        {
          sendUpEvent(activeKey, false);
          keyPreviewPlane.dismissLatest();
        }
        sendDownEvent(downKey, downPointerId, downPointerX);
        break;

      case MotionEvent.ACTION_MOVE:

        for (int index = 0; index < eventPointerCount; index++)
        {
          final int movePointerId = event.getPointerId(index);
          final int movePointerX = (int) event.getX(index);
          final int movePointerY = (int) event.getY(index);
          final Key moveKey = getKeyAtPoint(movePointerX, movePointerY);

          if (movePointerId == activePointerId)
          {
            if (isShiftKey(moveKey) && !isSwipeableKey(activeKey))
            {
              sendShiftMoveToEvent(movePointerId);
              break touchLogic;
            }

            if (moveKey != activeKey || isSwipeableKey(activeKey))
            {
              sendMoveEvent(moveKey, movePointerId, movePointerX);
              break touchLogic;
            }

            break touchLogic;
          }

          if (movePointerId == shiftPointerId)
          {
            if (!isShiftKey(moveKey))
            {
              sendShiftMoveFromEvent(moveKey, movePointerId);
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

        if ((upPointerId == shiftPointerId || isShiftKey(upKey)) && !isSwipeableKey(activeKey))
        {
          sendShiftUpEvent(true);
          break;
        }

        if (upPointerId == activePointerId)
        {
          sendUpEvent(upKey, true);
          break;
        }
        break;

      case MotionEvent.ACTION_CANCEL:
        sendCancelEvent();
        break;
    }

    return true;
  }

  private void sendCancelEvent()
  {
    shiftPointerId = NONEXISTENT_POINTER_ID;
    activeKey = null;
    activePointerId = NONEXISTENT_POINTER_ID;
    keyPreviewPlane.dismissAllImmediately();
    invalidate();
  }

  private void sendDownEvent(final Key key, final int pointerId, final int x)
  {
    if (isSwipeableKey(key))
    {
      pointerDownX = x;
    }
    swipeModeIsActivated = false;

    if (shiftPointerId != NONEXISTENT_POINTER_ID)
    {
      shiftMode = SHIFT_HELD;
      keyPreviewPlane.updateShiftMode(shiftMode);
    }

    activeKey = key;
    activePointerId = pointerId;

    sendAppropriateExtendedPressHandlerMessage(key);
    keyPreviewPlane.showPreviewAt(key);
    invalidate();
  }

  private void sendMoveEvent(final Key key, final int pointerId, final int x)
  {
    boolean shouldRedrawKeyboard = false;

    if (swipeModeIsActivated)
    {
      if (Math.abs(x - pointerDownX) < SWIPE_ACTIVATION_DISTANCE)
      {
        swipeModeIsActivated = false;
        shouldRedrawKeyboard = true;
      }
    }
    else if (key == activeKey && isSwipeableKey(key))
    {
      if (Math.abs(x - pointerDownX) > SWIPE_ACTIVATION_DISTANCE)
      {
        swipeModeIsActivated = true;
        removeAllExtendedPressHandlerMessages();
        shouldRedrawKeyboard = true;
      }
    }
    else // move is a key change
    {
      activeKey = key;
      removeAllExtendedPressHandlerMessages();
      sendAppropriateExtendedPressHandlerMessage(key);
      resetKeyRepeatIntervalMilliseconds();
      keyPreviewPlane.movePreviewTo(key);
      shouldRedrawKeyboard = true;
    }

    activePointerId = pointerId;

    if (shouldRedrawKeyboard)
    {
      invalidate();
    }
  }

  private void sendUpEvent(final Key key, final boolean shouldRedrawKeyboard)
  {
    if (swipeModeIsActivated)
    {
      keyboardListener.onSwipe(activeKey.valueText);
    }
    else if (key != null)
    {
      if (shiftMode != SHIFT_DISABLED && key.isShiftable)
      {
        keyboardListener.onKey(key.valueTextShifted);
      }
      else
      {
        keyboardListener.onKey(key.valueText);
      }

      if (shiftMode == SHIFT_SINGLE)
      {
        shiftMode = SHIFT_DISABLED;
        keyPreviewPlane.updateShiftMode(shiftMode);
      }
    }

    activeKey = null;
    activePointerId = NONEXISTENT_POINTER_ID;

    removeAllExtendedPressHandlerMessages();
    resetKeyRepeatIntervalMilliseconds();
    if (shouldRedrawKeyboard)
    {
      keyPreviewPlane.dismissLatest();
      invalidate();
    }
  }

  private void sendShiftDownEvent(final int pointerId)
  {
    if (shiftMode == SHIFT_DISABLED)
    {
      shiftMode =
              (activeKey == null)
                ? SHIFT_INITIATED
                : SHIFT_HELD;
      keyPreviewPlane.updateShiftMode(shiftMode);
    }
    shiftPointerId = pointerId;
    invalidate();
  }

  private void sendShiftMoveToEvent(final int pointerId)
  {
    shiftMode = SHIFT_HELD;
    shiftPointerId = pointerId;
    keyPreviewPlane.updateShiftMode(shiftMode);

    activeKey = null;
    activePointerId = NONEXISTENT_POINTER_ID;

    removeAllExtendedPressHandlerMessages();
    keyPreviewPlane.dismissLatest();
    invalidate();
  }

  private void sendShiftMoveFromEvent(final Key key, final int pointerId)
  {
    sendShiftUpEvent(false);

    activeKey = key;
    activePointerId = pointerId;

    removeAllExtendedPressHandlerMessages();
    sendAppropriateExtendedPressHandlerMessage(key);
    resetKeyRepeatIntervalMilliseconds();
    keyPreviewPlane.movePreviewTo(key);
    invalidate();
  }

  private void sendShiftUpEvent(boolean shouldRedrawKeyboard)
  {
    switch (shiftMode)
    {
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
    keyPreviewPlane.updateShiftMode(shiftMode);

    if (shouldRedrawKeyboard)
    {
      invalidate();
    }
  }

  private Key getKeyAtPoint(final int x, final int y)
  {
    for (final Key key : keyList)
    {
      if (key.containsPoint(x, y))
      {
        return key;
      }
    }

    return null;
  }

  private boolean isShiftKey(final Key key)
  {
    return key != null && key.valueText.equals(StrokeInputService.SHIFT_KEY_VALUE_TEXT);
  }

  private boolean isSwipeableKey(final Key key)
  {
    return key != null && key.isSwipeable;
  }

  private void sendAppropriateExtendedPressHandlerMessage(final Key key)
  {
    if (key == null)
    {
      return;
    }
    if (key.isRepeatable)
    {
      sendExtendedPressHandlerMessage(MESSAGE_KEY_REPEAT, KEY_REPEAT_START_MILLISECONDS);
    }
    else if (key.isLongPressable)
    {
      sendExtendedPressHandlerMessage(MESSAGE_LONG_PRESS, KEY_LONG_PRESS_MILLISECONDS);
    }
  }

  private void sendExtendedPressHandlerMessage(final int messageWhat, final long delayMilliseconds)
  {
    final Message message = extendedPressHandler.obtainMessage(messageWhat);
    extendedPressHandler.sendMessageDelayed(message, delayMilliseconds);
  }

  private void removeAllExtendedPressHandlerMessages()
  {
    extendedPressHandler.removeCallbacksAndMessages(null);
  }
}
