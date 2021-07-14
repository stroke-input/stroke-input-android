/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
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
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII = 50;
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8 = 100;
  
  private static final String SPACE = " ";
  private static final String NEWLINE = "\n";
  
  InputContainer inputContainer;
  Keyboard strokesKeyboard;
  Keyboard strokesSymbolsKeyboard;
  Keyboard qwertyKeyboard;
  Keyboard qwertySymbolsKeyboard;
  
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
    qwertySymbolsKeyboard =
      new Keyboard(this, R.xml.keyboard_qwerty_symbols);
    
    inputContainer.setKeyboard(strokesKeyboard);
    inputContainer.setOnInputListener(this);
    
    return inputContainer;
  }
  
  @Override
  public void onKey(final String valueText, final String valueTextShifted) {
    
    final InputConnection inputConnection = getCurrentInputConnection();
    if (inputConnection == null) {
      return;
    }
    
    final int shiftMode = inputContainer.getShiftMode();
    
    switch (valueText) {
      
      case "BACKSPACE":
        inputConnection.sendKeyEvent(
          new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
        );
        inputConnection.sendKeyEvent(
          new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
        );
        final String characterBeforeCursor =
          (String) inputConnection.getTextBeforeCursor(1, 0);
        final int nextBackspaceIntervalMilliseconds = (
          isAscii(characterBeforeCursor)
            ? BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII
            : BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8
        );
        inputContainer.setKeyRepeatIntervalMilliseconds(
          nextBackspaceIntervalMilliseconds
        );
        break;
      
      case "SWITCH_TO_STROKES":
        inputContainer.setKeyboard(strokesKeyboard);
        break;
      
      case "SWITCH_TO_STROKES_SYMBOLS":
        inputContainer.setKeyboard(strokesSymbolsKeyboard);
        break;
      
      case "SWITCH_TO_QWERTY":
        inputContainer.setKeyboard(qwertyKeyboard);
        break;
      
      case "SWITCH_TO_QWERTY_SYMBOLS":
        inputContainer.setKeyboard(qwertySymbolsKeyboard);
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
        final String committedText;
        if (shiftMode == InputContainer.SHIFT_DISABLED) {
          committedText = valueText;
        }
        else {
          committedText = valueTextShifted;
        }
        inputConnection.commitText(committedText, 1);
        if (shiftMode == InputContainer.SHIFT_SINGLE) {
          inputContainer.setShiftMode(InputContainer.SHIFT_DISABLED);
        }
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
  public void onShiftDown() {
    
    final int shiftMode = inputContainer.getShiftMode();
    if (shiftMode == InputContainer.SHIFT_DISABLED) {
      inputContainer.setShiftMode(InputContainer.SHIFT_INITIATED);
    }
  }
  
  @Override
  public void onShiftUp() {
    
    final int shiftMode = inputContainer.getShiftMode();
    switch (shiftMode) {
      
      case InputContainer.SHIFT_SINGLE:
        inputContainer.setShiftMode(InputContainer.SHIFT_PERSISTENT);
        break;
      
      case InputContainer.SHIFT_INITIATED:
        inputContainer.setShiftMode(InputContainer.SHIFT_SINGLE);
        break;
      
      case InputContainer.SHIFT_PERSISTENT:
      case InputContainer.SHIFT_PRESSED:
        inputContainer.setShiftMode(InputContainer.SHIFT_DISABLED);
        break;
    }
  }
  
  @Override
  public void onSwipe(final String valueText) {
    
    if (valueText.equals("SPACE")) {
      final Keyboard keyboard = inputContainer.getKeyboard();
      if (keyboard == strokesKeyboard) {
        inputContainer.setKeyboard(qwertyKeyboard);
      }
      else if (keyboard == strokesSymbolsKeyboard) {
        inputContainer.setKeyboard(qwertySymbolsKeyboard);
      }
      else if (keyboard == qwertyKeyboard) {
        inputContainer.setKeyboard(strokesKeyboard);
      }
      else if (keyboard == qwertySymbolsKeyboard) {
        inputContainer.setKeyboard(strokesSymbolsKeyboard);
      }
    }
    
  }
  
  private static boolean isAscii(final String string) {
    return string.matches("\\p{ASCII}*");
  }
  
}
