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
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
  
}
