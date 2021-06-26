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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/*
  A container that holds:
  - TODO: Candidates bar
  - TODO: Keyboard
*/
public class InputContainer
  extends View
{
  // Container meta-properties
  private Keyboard inputKeyboard;
  private Keyboard.Key[] inputKeyArray;
  
  // Keyboard drawing
  Rect keyRectangle;
  Paint keyFillPaint;
  Paint keyBorderPaint;
  Paint keyTextPaint;
  
  public InputContainer(Context context, AttributeSet attributes) {
    
    super(context, attributes);
    
    keyRectangle = new Rect();
    
    keyFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyFillPaint.setStyle(Paint.Style.FILL);
    
    keyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyBorderPaint.setStyle(Paint.Style.STROKE);
    
    keyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyTextPaint.setTypeface(Typeface.DEFAULT);
    keyTextPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  public void setKeyboard(Keyboard keyboard) {
    inputKeyboard = keyboard;
    List<Keyboard.Key> keyList = inputKeyboard.getKeyList();
    inputKeyArray = keyList.toArray(new Keyboard.Key[0]);
    requestLayout();
  }
  
  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    
    int paddingHorizontal = getPaddingLeft() + getPaddingRight();
    int paddingVertical = getPaddingTop() + getPaddingBottom();
    
    int keyboardWidth;
    int keyboardHeight;
    if (inputKeyboard == null) {
      keyboardWidth = 0;
      keyboardHeight = 0;
    }
    else {
      keyboardWidth = inputKeyboard.getWidth();
      keyboardHeight = inputKeyboard.getHeight();
    }
    
    setMeasuredDimension(
      px_from_dp(keyboardWidth + paddingHorizontal),
      px_from_dp(keyboardHeight + paddingVertical)
    );
  }
  
  @Override
  public void onDraw(Canvas canvas) {
    
    super.onDraw(canvas);
    
    if (inputKeyboard == null) {
      return;
    }
    
    int VOID_COLOUR = Color.BLACK;
    canvas.drawColor(VOID_COLOUR);
    
    final Keyboard.Key[] keyArray = inputKeyArray;
    
    for (final Keyboard.Key key : keyArray) {
      
      int key_x_px = px_from_dp(key.x);
      int key_y_px = px_from_dp(key.y);
      int key_width_px = px_from_dp(key.width);
      int key_height_px = px_from_dp(key.height);
      int key_border_thickness_px = px_from_dp(key.keyBorderThickness);
      
      String valueText = key.valueText;
      String displayText = key.displayText;
      if (displayText == null) {
        displayText = valueText;
      }
      
      keyRectangle.set(0, 0, key_width_px, key_height_px);
      
      keyFillPaint.setColor(key.keyFillColour);
      keyBorderPaint.setColor(key.keyBorderColour);
      keyBorderPaint.setStrokeWidth(key_border_thickness_px);
      
      keyTextPaint.setColor(key.keyTextColour);
      keyTextPaint.setTextSize(key.keyTextSize);
      
      canvas.translate(key_x_px, key_y_px);
      
      canvas.drawRect(keyRectangle, keyFillPaint);
      canvas.drawRect(keyRectangle, keyBorderPaint);
      canvas.drawText(
        displayText,
        (float) key_width_px / 2,
        (key_height_px - keyTextPaint.ascent() - keyTextPaint.descent()) / 2,
        keyTextPaint
      );
      
      canvas.translate(-key_x_px, -key_y_px);
    }
    
  }
  
  int px_from_dp(int dp) {
    return (int) (getResources().getDisplayMetrics().density * dp);
  }
  
}
