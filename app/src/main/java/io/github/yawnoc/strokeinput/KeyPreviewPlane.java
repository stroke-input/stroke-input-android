/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;

/*
  A plane for key previews, to be displayed in a PopupWindow.
*/
public class KeyPreviewPlane extends View {
  
  // Key preview drawing
  private final Rect keyPreviewRectangle;
  private final Paint keyPreviewFillPaint;
  private final Paint keyPreviewBorderPaint;
  private final Paint keyPreviewTextPaint;
  
  public KeyPreviewPlane(final Context context) {
    
    super(context);
    
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
  
  @Override
  public void onDraw(final Canvas canvas) {
    
    // TODO: implement properly
    canvas.drawColor(Color.GREEN);
    
  }
  
}
