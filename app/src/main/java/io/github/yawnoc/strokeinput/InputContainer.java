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
import android.graphics.Color;
import android.graphics.Paint;
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
  int DEFAULT_KEY_TEXT_COLOUR = Color.WHITE;
  int DEFAULT_KEY_TEXT_SIZE_PX = 36;
  
  // Container meta-properties
  private Keyboard currentKeyboard;
  private Context currentContext;
  private Paint currentPaint;
  
  // Keyboard properties
  private int keyFillColour;
  private int keyTextColour;
  private int keyTextSize;
  
  public InputContainer(Context context, AttributeSet attributes) {
    this(context, attributes, R.attr.keyboardViewStyle);
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
    
    currentContext = context;
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
  
    currentPaint = new Paint();
    currentPaint.setAntiAlias(true);
    currentPaint.setTextSize(keyTextSize);
    currentPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  public void setKeyboard(Keyboard keyboard) {
    currentKeyboard = keyboard;
    List<Keyboard.Key> keyList = currentKeyboard.getKeyList();
    requestLayout();
  }
  
}
