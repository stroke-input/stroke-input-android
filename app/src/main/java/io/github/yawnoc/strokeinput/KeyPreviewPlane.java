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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.yawnoc.utilities.Valuey;

/*
  A plane for key previews.
*/
public class KeyPreviewPlane
  extends View
{
  private static final int DISMISSAL_DELAY_MILLISECONDS = 20;
  
  // Properties
  private int width;
  private int height;
  private int keyboardHeight;
  private final List<Key> keyList = new ArrayList<>();
  private Key latestKey;
  private int shiftMode = KeyboardView.SHIFT_DISABLED;
  
  // Delayed dismissal
  private Handler dismissalHandler;
  
  // Key preview drawing
  private Rect keyPreviewRectangle;
  private Paint keyPreviewFillPaint;
  private Paint keyPreviewBorderPaint;
  private Paint keyPreviewTextPaint;
  
  public KeyPreviewPlane(final Context context)
  {
    super(context);
    
    initialiseDismissalHandler();
    initialiseDrawing(context);
  }
  
  public KeyPreviewPlane(final Context context, final AttributeSet attributes)
  {
    super(context, attributes);
    
    initialiseDismissalHandler();
    initialiseDrawing(context);
  }
  
  private void initialiseDismissalHandler()
  {
    dismissalHandler =
            new Handler(Looper.getMainLooper())
            {
              @Override
              public void handleMessage(Message message)
              {
                Key key = (Key) message.obj;
                keyList.remove(key);
                invalidate();
              }
            };
  }
  
  private void initialiseDrawing(final Context context)
  {
    keyPreviewRectangle = new Rect();
    
    keyPreviewFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyPreviewFillPaint.setStyle(Paint.Style.FILL);
    
    keyPreviewBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyPreviewBorderPaint.setStyle(Paint.Style.STROKE);
    
    keyPreviewTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    keyPreviewTextPaint.setTypeface(
      Typeface.createFromAsset(context.getAssets(), KeyboardView.KEYBOARD_FONT_FILE_NAME)
    );
    keyPreviewTextPaint.setTextAlign(Paint.Align.CENTER);
  }
  
  public void updateDimensions(
    final int width,
    final int height,
    final int keyboardHeight
  )
  {
    setLayoutParams(new FrameLayout.LayoutParams(width, height));
    this.width = width;
    this.height = height;
    this.keyboardHeight = keyboardHeight;
  }
  
  public void updateShiftMode(final int shiftMode)
  {
    this.shiftMode = shiftMode;
    invalidate();
  }
  
  public void showPreviewAt(final Key key)
  {
    if (key != null && !keyList.contains(key) && key.isPreviewable)
    {
      keyList.add(key);
    }
    latestKey = key;
    invalidate();
  }
  
  public void movePreviewTo(final Key key)
  {
    keyList.remove(latestKey);
    showPreviewAt(key);
  }
  
  public void dismissLatest()
  {
    Message dismissalMessage = new Message();
    dismissalMessage.obj = latestKey;
    dismissalHandler.sendMessageDelayed(dismissalMessage, DISMISSAL_DELAY_MILLISECONDS);
    latestKey = null;
  }
  
  public void dismissAllImmediately()
  {
    keyList.clear();
    latestKey = null;
    invalidate();
  }
  
  @Override
  public void onDraw(final Canvas canvas)
  {
    for (final Key key : keyList)
    {
      final int keyPreviewWidth = (int) (key.previewMagnification * key.width);
      final int keyPreviewHeight = (int) (key.previewMagnification * key.height);
      
      keyPreviewRectangle.set(0, 0, keyPreviewWidth, keyPreviewHeight);
      
      keyPreviewFillPaint.setColor(KeyboardView.toPressedColour(key.fillColour));
      keyPreviewBorderPaint.setColor(key.borderColour);
      keyPreviewBorderPaint.setStrokeWidth(key.borderThickness);
      
      final float keyPreviewTextSize = key.previewMagnification * key.textSize;
      
      keyPreviewTextPaint.setColor(key.textColour);
      keyPreviewTextPaint.setTextSize(keyPreviewTextSize);
      
      final String keyPreviewDisplayText = key.shiftAwareDisplayText(shiftMode);
      
      final int keyPreviewTextOffsetX = (int) (key.previewMagnification * key.textOffsetX);
      final int keyPreviewTextOffsetY = (int) (key.previewMagnification * key.textOffsetY);
      
      final float keyPreviewTextX = keyPreviewWidth / 2f + keyPreviewTextOffsetX;
      final float keyPreviewTextY =
              (keyPreviewHeight - keyPreviewTextPaint.ascent() - keyPreviewTextPaint.descent()) / 2f
                +
              keyPreviewTextOffsetY;
      
      final int previewX =
              (int) Valuey.clipValueToRange(
                key.x - (keyPreviewWidth - key.width) / 2f,
                key.borderThickness,
                this.width - keyPreviewWidth - key.borderThickness
              );
      final int previewY =
              key.y
                - keyPreviewHeight - key.previewMarginY
                + this.height - keyboardHeight;
      
      canvas.translate(previewX, previewY);
      canvas.drawRect(keyPreviewRectangle, keyPreviewFillPaint);
      canvas.drawRect(keyPreviewRectangle, keyPreviewBorderPaint);
      canvas.drawText(keyPreviewDisplayText, keyPreviewTextX, keyPreviewTextY, keyPreviewTextPaint);
      canvas.translate(-previewX, -previewY);
    }
  }
}
