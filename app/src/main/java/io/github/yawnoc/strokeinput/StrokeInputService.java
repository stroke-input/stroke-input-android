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
  
  InputContainer inputContainer;
  Keyboard strokesKeyboard;
  Keyboard strokesSymbolsKeyboard;
  Keyboard qwertyKeyboard;
  
  @SuppressLint("InflateParams")
  @Override
  public View onCreateInputView() {
    
    inputContainer =
      (InputContainer)
        getLayoutInflater().inflate(R.layout.input_container, null);
    
    strokesKeyboard = new Keyboard(this, R.xml.keyboard_strokes);
    strokesSymbolsKeyboard =
      new Keyboard(this, R.xml.keyboard_strokes_symbols);
    qwertyKeyboard = new Keyboard(this, R.xml.keyboard_qwerty);
    
    inputContainer.setKeyboard(strokesKeyboard);
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
      
      case "SWITCH_TO_STROKES":
        inputContainer.setKeyboard(strokesKeyboard);
        break;
      
      case "SWITCH_TO_STROKES_SYMBOLS":
        inputContainer.setKeyboard(strokesSymbolsKeyboard);
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
  
  @Override
  public void onSwipe(final String valueText) {
    
    if (valueText.equals("SPACE")) {
      final Keyboard keyboard = inputContainer.getKeyboard();
      if (keyboard == strokesKeyboard || keyboard == strokesSymbolsKeyboard) {
        inputContainer.setKeyboard(qwertyKeyboard);
      }
      else {
        inputContainer.setKeyboard(strokesKeyboard);
      }
    }
    
  }
  
}
