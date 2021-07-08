/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

/*
  An InputMethodService for the Stroke Input Method (筆畫輸入法).
*/
public class StrokeInputService
  extends InputMethodService
  implements InputContainer.OnInputListener
{
  private static final String EMPTY_STRING = "";
  private static final String SPACE = " ";
  private static final String NEWLINE = "\n";
  
  @Override
  public View onCreateInputView() {
    
    @SuppressLint("InflateParams")
    final InputContainer inputContainer =
      (InputContainer)
        getLayoutInflater().inflate(R.layout.input_container, null);
    
    final Keyboard keyboard = new Keyboard(this, R.xml.keyboard_strokes);
    
    inputContainer.setKeyboard(keyboard);
    inputContainer.setOnInputListener(this);
    
    return inputContainer;
  }
  
  @Override
  public void onKey(final String valueText) {
  
    final InputConnection inputConnection = getCurrentInputConnection();
    if (inputConnection == null) {
      return;
    }
    
    switch (valueText) {
      
      case "BACKSPACE":
        final CharSequence selectedText = inputConnection.getSelectedText(0);
        if (TextUtils.isEmpty(selectedText)) {
          inputConnection.deleteSurroundingTextInCodePoints(1, 0);
        }
        else {
          inputConnection.commitText(EMPTY_STRING, 1);
        }
        break;
      
      case "SPACE":
        inputConnection.commitText(SPACE, 1);
        break;
      
      case "ENTER":
        final EditorInfo editorInfo = getCurrentInputEditorInfo();
        final int editorActionBits = editorInfo.imeOptions;
        final boolean enterKeyHasAction =
          (editorActionBits & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0;
        if (enterKeyHasAction) {
          inputConnection.performEditorAction(editorActionBits);
        }
        else {
          inputConnection.commitText(NEWLINE, 1);
        }
        break;
      
      default:
        inputConnection.commitText(valueText, 1);
    }
  }
  
  @Override
  public void onLongPress(final String valueText) {
    
    if (valueText.equals("SPACE")) {
      final InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.showInputMethodPicker();
    }
  }
  
}
