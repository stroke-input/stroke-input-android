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

import java.util.ArrayList;
import java.util.List;

import io.github.yawnoc.utilities.Valuey;

/*
  A plane for key previews, to be displayed in a PopupWindow.
*/
public class KeyPreviewPlane extends View {
  
  // Properties
  private int width;
  private int height;
  private int keyboardHeight;
  private final List<Key> showingKeyList;
  
  // Key preview drawing
  private final Rect keyPreviewRectangle;
  private final Paint keyPreviewFillPaint;
  private final Paint keyPreviewBorderPaint;
  private final Paint keyPreviewTextPaint;
  
  public KeyPreviewPlane(final Context context) {
    
    super(context);
    
    showingKeyList = new ArrayList<>();
    
    keyPreviewRectangle = new Rect();
    
    keyPreviewFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyPreviewFillPaint.setStyle(Paint.Style.FILL);
    
    keyPreviewBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyPreviewBorderPaint.setStyle(Paint.Style.STROKE);
    
    keyPreviewTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyPreviewTextPaint.setTypeface(
      Typeface.createFromAsset(
        context.getAssets(),
        InputContainer.KEYBOARD_FONT
      )
    );
    keyPreviewTextPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  public void updateDimensions(
    final int width,
    final int height,
    final int keyboardHeight
  )
  {
    this.width = width;
    this.height = height;
    this.keyboardHeight = keyboardHeight;
  }
  
  public void showKey(final Key key) {
    showingKeyList.add(key);
    invalidate();
  }
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    for (final Key key : showingKeyList) {
      
      final int keyPreviewWidth =
        (int) (key.previewMagnification * key.width);
      final int keyPreviewHeight =
        (int) (key.previewMagnification * key.height);
      
      keyPreviewRectangle.set(0, 0, keyPreviewWidth, keyPreviewHeight);
      
      keyPreviewFillPaint.setColor(
        InputContainer.toPressedColour(key.fillColour)
      );
      keyPreviewBorderPaint.setColor(key.borderColour);
      keyPreviewBorderPaint.setStrokeWidth(key.borderThickness);
      
      final float keyPreviewTextSize =
        key.previewMagnification * key.textSize;
      
      keyPreviewTextPaint.setColor(key.textColour);
      keyPreviewTextPaint.setTextSize(keyPreviewTextSize);
      
      final String keyPreviewDisplayText =
        key.displayText; // TODO: make shift-aware
      
      final int keyPreviewTextOffsetX =
        (int) (key.previewMagnification * key.textOffsetX);
      final int keyPreviewTextOffsetY =
        (int) (key.previewMagnification * key.textOffsetY);
      
      final float keyPreviewTextX = (
        keyPreviewWidth / 2f
          + keyPreviewTextOffsetX
      );
      final float keyPreviewTextY = (
        (
          keyPreviewHeight
            - keyPreviewTextPaint.ascent()
            - keyPreviewTextPaint.descent()
        ) / 2f
          + keyPreviewTextOffsetY
      );
      
      final int keyPreviewMargin = key.previewMargin;
      final int previewX =
        (int) Valuey.clipValueToRange(
          key.x - (keyPreviewWidth - key.width) / 2f,
          0,
          this.width
        );
      final int previewY = (
        key.y
          - keyPreviewHeight - keyPreviewMargin
          + this.height - keyboardHeight
      );
      
      canvas.translate(previewX, previewY);
      
      canvas.drawRect(keyPreviewRectangle, keyPreviewFillPaint);
      canvas.drawRect(keyPreviewRectangle, keyPreviewBorderPaint);
      canvas.drawText(
        keyPreviewDisplayText,
        keyPreviewTextX,
        keyPreviewTextY,
        keyPreviewTextPaint
      );
      
      canvas.translate(-previewX, -previewY);
      
    }
    
  }
  
}
