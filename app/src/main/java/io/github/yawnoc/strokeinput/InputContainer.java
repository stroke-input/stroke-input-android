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
import android.graphics.drawable.Drawable;
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
  int DEFAULT_KEY_TEXT_COLOUR = Color.BLUE; // TODO: revert to Color.WHITE
  
  // Container meta-properties
  private Keyboard inputKeyboard;
  private Keyboard.Key[] inputKeyArray;
  
  // Keyboard drawing
  Paint keyFillPaint;
  Rect keyRectangle;
  
  public InputContainer(Context context, AttributeSet attributes) {
    
    super(context, attributes);
    
    keyFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyRectangle = new Rect();
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
    
    int VOID_COLOUR = Color.BLACK;
    
    super.onDraw(canvas);
    
    if (inputKeyboard == null) {
      return;
    }
    
    canvas.drawColor(VOID_COLOUR);
    
    final Keyboard.Key[] keyArray = inputKeyArray;
    
    for (final Keyboard.Key currentKey : keyArray) {
      
      String valueText = currentKey.valueText;
      String displayText = currentKey.displayText;
      Drawable displayIcon = currentKey.displayIcon;
      
      if (displayText == null) {
        displayText = valueText;
      }
      
      keyFillPaint.setColor(currentKey.keyFillColour);
      keyRectangle.set(
        0,
        0,
        px_from_dp(currentKey.width),
        px_from_dp(currentKey.height)
      );
      
      canvas.translate(px_from_dp(currentKey.x), px_from_dp(currentKey.y));
      canvas.drawRect(keyRectangle, keyFillPaint);
      canvas.translate(-px_from_dp(currentKey.x), -px_from_dp(currentKey.y));
    }
    
  }
  
  int px_from_dp(int dp) {
    return (int) (getResources().getDisplayMetrics().density * dp);
  }
  
}
