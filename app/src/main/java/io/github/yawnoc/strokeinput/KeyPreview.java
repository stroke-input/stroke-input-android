/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;

public class KeyPreview extends View {
  
  // Properties
  private Key activeKey;
  
  // Key preview drawing
  private final Rect rectangle;
  private final Paint fillPaint;
  private final Paint borderPaint;
  private final Paint textPaint;
  
  public KeyPreview(final Context context) {
    
    super(context);
    
    rectangle = new Rect();
    
    fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    fillPaint.setStyle(Paint.Style.FILL);
    
    borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    borderPaint.setStyle(Paint.Style.STROKE);
    
    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setTypeface(
      Typeface.createFromAsset(
        context.getAssets(),
        InputContainer.KEYBOARD_FONT)
    );
    textPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    if (activeKey == null) {
      return;
    }
  
    rectangle.set(0, 0, activeKey.width, activeKey.height);
    
    fillPaint.setColor(activeKey.fillColour);
    borderPaint.setColor(activeKey.borderColour);
    borderPaint.setStrokeWidth(activeKey.borderThickness);
  
    textPaint.setColor(activeKey.textColour);
    textPaint.setTextSize(activeKey.textSize);
  }
  
}
