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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
  int DEFAULT_KEY_FILL_COLOUR = Color.BLACK;
  int DEFAULT_KEY_TEXT_COLOUR = Color.BLUE; // TODO: revert to Color.WHITE
  int DEFAULT_KEY_TEXT_SIZE_PX = 36;
  
  // Container meta-properties
  private Keyboard inputKeyboard;
  private Keyboard.Key[] inputKeyArray;
  private Context inputContext;
  private Paint inputPaint;
  
  // Keyboard styles
  private int keyFillColour;
  private int keyTextColour;
  private int keyTextSize;
  
  public InputContainer(Context context, AttributeSet attributes) {
    this(context, attributes, R.attr.inputContainerStyle);
  }
  
  public InputContainer(
    Context context,
    AttributeSet attributes,
    int defaultStyleAttribute
  )
  {
    this(context, attributes, defaultStyleAttribute, 0);
  }
  
  public InputContainer(
    Context context,
    AttributeSet attributes,
    int defaultStyleAttribute,
    int defaultStyleResourceId
  )
  {
    super(context, attributes, defaultStyleAttribute, defaultStyleResourceId);
    
    inputContext = context;
    TypedArray attributesArray =
      context.obtainStyledAttributes(
        attributes,
        R.styleable.InputContainer,
        defaultStyleAttribute,
        defaultStyleResourceId
      );
    
    int indexCount = attributesArray.getIndexCount();
    
    for (int index = 0; index < indexCount; index++) {
      
      int populatedIndex = attributesArray.getIndex(index);
      
      if (populatedIndex == R.styleable.InputContainer_keyFillColour) {
        keyFillColour =
          attributesArray.getColor(populatedIndex, DEFAULT_KEY_FILL_COLOUR);
      }
      else if (populatedIndex == R.styleable.InputContainer_keyTextColour) {
        keyTextColour =
          attributesArray.getColor(populatedIndex, DEFAULT_KEY_TEXT_COLOUR);
      }
      else if (populatedIndex == R.styleable.InputContainer_keyTextSize) {
        keyTextSize =
          attributesArray.getDimensionPixelSize(
            populatedIndex,
            DEFAULT_KEY_TEXT_SIZE_PX
          );
      }
    }
    
    attributesArray.recycle();
    
    inputPaint = new Paint();
    inputPaint.setAntiAlias(true);
    inputPaint.setTextSize(keyTextSize);
    inputPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  public void setKeyboard(Keyboard keyboard) {
    inputKeyboard = keyboard;
    List<Keyboard.Key> keyList = inputKeyboard.getKeyList();
    inputKeyArray = keyList.toArray(new Keyboard.Key[0]);
    requestLayout();
  }
  
  @Override
  public void onDraw(Canvas canvas) {
    
    super.onDraw(canvas);
    
    if (inputKeyboard == null) {
      return;
    }
    
    final Paint paint = inputPaint;
    final Keyboard.Key[] keyArray = inputKeyArray;
    
    canvas.drawColor(Color.YELLOW); // TODO: remove me
    
    for (final Keyboard.Key currentKey : keyArray) {
      
      String valueText = currentKey.valueText;
      String displayText = currentKey.displayText;
      Drawable displayIcon = currentKey.displayIcon;
      
      if (displayText == null) {
        displayText = valueText;
      }
      
      if (displayIcon == null) {
        paint.setColor(keyTextColour);
        paint.setTextSize(keyTextSize);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText(
          displayText,
          currentKey.width / 2,
          currentKey.height / 2,
          paint
        );
      }
    }
    
  }
  
}
