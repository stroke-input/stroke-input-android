/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

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
  private KeyboardView keyboardView;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  public void initialiseCandidatesView(final CandidatesViewAdapter.CandidateListener candidateListener) {
    candidatesView = findViewById(R.id.candidates_view);
    candidatesView.setCandidateListener(candidateListener);
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
  
}
