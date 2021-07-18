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

import java.util.HashMap;
import java.util.Map;

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
  
  private static final String PREFERENCES_FILE_NAME = "preferences.txt";
  
  InputContainer inputContainer;
  Keyboard strokesKeyboard;
  Keyboard strokesSymbolsKeyboard;
  Keyboard qwertyKeyboard;
  Keyboard qwertySymbolsKeyboard;
  
  Map<Keyboard, String> nameFromKeyboard;
  Map<String, Keyboard> keyboardFromName;
  
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
  
    nameFromKeyboard = new HashMap<>();
    nameFromKeyboard.put(strokesKeyboard, "STROKES");
    nameFromKeyboard.put(strokesSymbolsKeyboard, "STROKES_SYMBOLS");
    nameFromKeyboard.put(qwertyKeyboard, "QWERTY");
    nameFromKeyboard.put(qwertySymbolsKeyboard, "QWERTY_SYMBOLS");
    keyboardFromName = Utilities.invertMap(nameFromKeyboard);
    
    Keyboard keyboard = getSavedKeyboard();
    if (keyboard == null) {
      keyboard = strokesKeyboard;
    }
    switchKeyboardWithSave(keyboard);
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
        inputConnection.sendKeyEvent(
          new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
        );
        inputConnection.sendKeyEvent(
          new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
        );
        final String characterBeforeCursor =
          (String) inputConnection.getTextBeforeCursor(1, 0);
        final int nextBackspaceIntervalMilliseconds = (
          Utilities.isAscii(characterBeforeCursor)
            ? BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII
            : BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8
        );
        inputContainer.setKeyRepeatIntervalMilliseconds(
          nextBackspaceIntervalMilliseconds
        );
        break;
      
      case "SWITCH_TO_STROKES":
        switchKeyboardWithSave(strokesKeyboard);
        break;
      
      case "SWITCH_TO_STROKES_SYMBOLS":
        switchKeyboardWithSave(strokesSymbolsKeyboard);
        break;
      
      case "SWITCH_TO_QWERTY":
        switchKeyboardWithSave(qwertyKeyboard);
        break;
      
      case "SWITCH_TO_QWERTY_SYMBOLS":
        switchKeyboardWithSave(qwertySymbolsKeyboard);
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
      Utilities.showSystemKeyboardSwitcher(this);
    }
    else if (valueText.equals("ABOUT")) {
      inputContainer.toggleDebugMode();
    }
  }
  
  @Override
  public void onSwipe(final String valueText) {
    
    if (valueText.equals("SPACE")) {
      
      final Keyboard keyboard = inputContainer.getKeyboard();
      final Keyboard newKeyboard;
      
      if (keyboard == strokesKeyboard || keyboard == strokesSymbolsKeyboard) {
        newKeyboard = qwertyKeyboard;
      }
      else {
        newKeyboard = strokesKeyboard;
      }
      
      switchKeyboardWithSave(newKeyboard);
    }
  }
  
  private void switchKeyboardWithSave(final Keyboard keyboard) {
    
    inputContainer.setKeyboard(keyboard);
    setSavedKeyboard(keyboard);
  }
  
  private Keyboard getSavedKeyboard() {
    
    String keyboardName = Utilities.loadPreferenceString(
      getApplicationContext(),
      PREFERENCES_FILE_NAME,
      "keyboardName"
    );
    
    return keyboardFromName.get(keyboardName);
  }
  
  private void setSavedKeyboard(final Keyboard keyboard) {
    
    String keyboardName = nameFromKeyboard.get(keyboard);
    
    Utilities.savePreferenceString(
      getApplicationContext(),
      PREFERENCES_FILE_NAME,
      "keyboardName",
      keyboardName
    );
  }
}
