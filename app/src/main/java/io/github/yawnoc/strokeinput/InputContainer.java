/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/*
  A container that holds:
    - Stroke sequence bar
    - Candidates view
    - Keyboard view
*/
public class InputContainer
  extends LinearLayout
{
  
  // Container properties
  private TextView strokeSequenceBar;
  private CandidatesView candidatesView;
  private CandidatesViewAdapter candidatesViewAdapter;
  private KeyboardView keyboardView;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  public void initialiseStrokeSequenceBar(final Context context) {
    strokeSequenceBar = findViewById(R.id.stroke_sequence_bar);
    strokeSequenceBar.setTypeface(Typeface.createFromAsset(context.getAssets(), KeyboardView.KEYBOARD_FONT_FILE_NAME));
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
  
  public int getTouchableTopY() {
    return keyboardView.getTouchableTopY();
  }
  
  public void setKeyboard(final Keyboard keyboard) {
    keyboardView.setKeyboard(keyboard);
  }
  
  public void setKeyRepeatIntervalMilliseconds(final int millis) {
    keyboardView.setKeyRepeatIntervalMilliseconds(millis);
  }
  
  public void setCandidateList(final List<String> candidateList) {
    candidatesViewAdapter.updateCandidateList(candidateList);
    candidatesView.scrollToPosition(0);
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
