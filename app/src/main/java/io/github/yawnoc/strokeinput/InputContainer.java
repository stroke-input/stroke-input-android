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
  private StrokeSequenceBar strokeSequenceBar;
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
  
  public void setStrokeDigitSequence(final String strokeDigitSequence) {
    strokeSequenceBar.setStrokeDigitSequence(strokeDigitSequence);
  }
  
  public void setCandidateList(final List<String> candidateList) {
    candidatesViewAdapter.updateCandidateList(candidateList);
    candidatesView.scrollToPosition(0);
  }
  
  public Keyboard getKeyboard() {
    return keyboardView.getKeyboard();
  }
  
  public void setKeyboard(final Keyboard keyboard) {
    keyboardView.setKeyboard(keyboard);
  }
  
  public void setKeyRepeatIntervalMilliseconds(final int millis) {
    keyboardView.setKeyRepeatIntervalMilliseconds(millis);
  }
  
}
