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
  private final Rect keyRectangle;
  private final Paint keyFillPaint;
  private final Paint keyBorderPaint;
  private final Paint keyTextPaint;
  
  public KeyPreview(final Context context) {
    
    super(context);
    
    keyRectangle = new Rect();
    
    keyFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyFillPaint.setStyle(Paint.Style.FILL);
    
    keyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyBorderPaint.setStyle(Paint.Style.STROKE);
    
    keyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyTextPaint.setTypeface(
      Typeface.createFromAsset(
        context.getAssets(),
        InputContainer.KEYBOARD_FONT)
    );
    keyTextPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    if (activeKey == null) {
      return;
    }
  
    keyRectangle.set(0, 0, activeKey.width, activeKey.height);
    
    keyFillPaint.setColor(activeKey.fillColour);
    keyBorderPaint.setColor(activeKey.borderColour);
    keyBorderPaint.setStrokeWidth(activeKey.borderThickness);
  
    keyTextPaint.setColor(activeKey.textColour);
    keyTextPaint.setTextSize(activeKey.textSize);
  }
  
}
