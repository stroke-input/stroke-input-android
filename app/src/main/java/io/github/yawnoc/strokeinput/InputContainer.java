/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/*
  A container that holds:
    - Stroke sequence bar (popup)
    - Candidates view
    - Keyboard view
*/
public class InputContainer
  extends LinearLayout
{
  
  // Container properties
  private CandidatesView candidatesView;
  private CandidatesViewAdapter candidatesViewAdapter;
  private KeyboardView keyboardView;
  
  // Stroke sequence bar
  private TextView strokeSequenceBar;
  private PopupWindow strokeSequenceBarPopup;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  public void initialiseCandidatesView(final CandidatesViewAdapter.CandidateListener candidateListener) {
    candidatesView = findViewById(R.id.candidates_view);
    candidatesView.setCandidateListener(candidateListener);
    candidatesViewAdapter = candidatesView.getCandidatesViewAdapter();
  }
  
  public void initialiseKeyboardView(
    final KeyboardView.KeyboardListener keyboardListener,
    final Keyboard keyboard
  )
  {
    keyboardView = findViewById(R.id.keyboard_view);
    keyboardView.setKeyboardListener(keyboardListener);
    keyboardView.setKeyboard(keyboard);
  }
  
  @SuppressLint("InflateParams")
  public void initialiseStrokeSequenceBar(final Context context) {
    
    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    strokeSequenceBar = (TextView) layoutInflater.inflate(R.layout.stroke_sequence_bar, null);
    strokeSequenceBar.setTypeface(Typeface.createFromAsset(context.getAssets(), KeyboardView.KEYBOARD_FONT_FILE_NAME));
    
    final int popup_size = LinearLayout.LayoutParams.WRAP_CONTENT;
    strokeSequenceBarPopup = new PopupWindow(strokeSequenceBar, popup_size, popup_size);
    strokeSequenceBarPopup.setTouchable(false);
    strokeSequenceBarPopup.setClippingEnabled(false);
    
  }
  
  @SuppressLint("RtlHardcoded")
  public void showStrokeSequenceBar() {
    
    final int softButtonsHeight = getSoftButtonsHeight();
    final int keyboardViewHeight = keyboardView.getHeight();
    
    strokeSequenceBarPopup.dismiss();
    
    if (getWindowToken() != null) { // check needed in API level 29
      strokeSequenceBarPopup.showAtLocation(
        this,
        Gravity.BOTTOM | Gravity.LEFT,
        0,
        softButtonsHeight + keyboardViewHeight
      );
    }
    
  }
  
  private int getSoftButtonsHeight() {
    
    final int softButtonsHeight;
    final WindowInsets rootWindowInsets = this.getRootWindowInsets();
    if (rootWindowInsets == null) {
      softButtonsHeight = 0;
    }
    else {
      if (Build.VERSION.SDK_INT < 30) {
        softButtonsHeight = rootWindowInsets.getSystemWindowInsetBottom(); // deprecated in API level 30
      }
      else {
        softButtonsHeight = rootWindowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom;
      }
    }
    
    return softButtonsHeight;
    
  }
  
  public void setKeyboard(final Keyboard keyboard) {
    keyboardView.setKeyboard(keyboard);
  }
  
  public void setKeyRepeatIntervalMilliseconds(final int millis) {
    keyboardView.setKeyRepeatIntervalMilliseconds(millis);
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
      strokeSequenceBar.setText(strokeSequence);
      strokeSequenceBar.requestLayout();
      strokeSequenceBar.setVisibility(VISIBLE);
    }
    else {
      strokeSequenceBar.setVisibility(INVISIBLE);
    }
    
  }
  
}
