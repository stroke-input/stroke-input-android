/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/*
  A bar showing the current stroke sequence.
*/
public class StrokeSequenceBar
  extends AppCompatTextView
{
  public StrokeSequenceBar(Context context, AttributeSet attributes) {
    super(context, attributes);
  }
  
  public void setStrokeDigitSequence(final String strokeDigitSequence) {
    
    if (strokeDigitSequence.length() > 0) {
      final String strokeSequence = (
        strokeDigitSequence
          .replace(StrokeInputService.STROKE_DIGIT_1, getResources().getString(R.string.stroke_1))
          .replace(StrokeInputService.STROKE_DIGIT_2, getResources().getString(R.string.stroke_2))
          .replace(StrokeInputService.STROKE_DIGIT_3, getResources().getString(R.string.stroke_3))
          .replace(StrokeInputService.STROKE_DIGIT_4, getResources().getString(R.string.stroke_4))
          .replace(StrokeInputService.STROKE_DIGIT_5, getResources().getString(R.string.stroke_5))
      );
      setText(strokeSequence);
      requestLayout();
      setVisibility(VISIBLE);
    }
    else {
      setVisibility(INVISIBLE);
    }
    
  }
}
