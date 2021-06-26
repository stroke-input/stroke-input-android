/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.view.View;

/*
  An InputMethodService for the Stroke Input Method (筆畫輸入法).
  Currently does nothing.
*/
public class StrokeInputService extends InputMethodService {
  
  @Override
  public View onCreateInputView() {
    
    @SuppressLint("InflateParams")
    InputContainer inputContainer =
      (InputContainer)
        getLayoutInflater().inflate(R.layout.input_container, null);
    Keyboard keyboard = new Keyboard(this, R.xml.keyboard_strokes);
    
    inputContainer.setKeyboard(keyboard);
    
    return inputContainer;
  }
  
}
