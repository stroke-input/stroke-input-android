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
import java.util.Set;

import io.github.yawnoc.utilities.Contexty;
import io.github.yawnoc.utilities.Mappy;
import io.github.yawnoc.utilities.Stringy;

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
  
  Keyboard strokesKeyboard;
  Keyboard strokesSymbols1Keyboard;
  Keyboard strokesSymbols2Keyboard;
  Keyboard qwertyKeyboard;
  Keyboard qwertySymbolsKeyboard;
  
  private Map<Keyboard, String> nameFromKeyboard;
  private Map<String, Keyboard> keyboardFromName;
  private Set<Keyboard> keyboardSet;
  
  private InputContainer inputContainer;
  
  private int inputOptionsBits;
  private boolean enterKeyHasAction;
  
  @Override
  public View onCreateInputView() {
    
    initialiseKeyboards();
    initialiseInputContainer();
    
    return inputContainer;
  }
  
  private void initialiseKeyboards() {
    
    strokesKeyboard = newKeyboard(R.xml.keyboard_strokes);
    strokesSymbols1Keyboard = newKeyboard(R.xml.keyboard_strokes_symbols_1);
    strokesSymbols2Keyboard = newKeyboard(R.xml.keyboard_strokes_symbols_2);
    qwertyKeyboard = newKeyboard(R.xml.keyboard_qwerty);
    qwertySymbolsKeyboard = newKeyboard(R.xml.keyboard_qwerty_symbols);
    
    nameFromKeyboard = new HashMap<>();
    nameFromKeyboard.put(strokesKeyboard, "STROKES");
    nameFromKeyboard.put(strokesSymbols1Keyboard, "STROKES_SYMBOLS_1");
    nameFromKeyboard.put(strokesSymbols2Keyboard, "STROKES_SYMBOLS_2");
    nameFromKeyboard.put(qwertyKeyboard, "QWERTY");
    nameFromKeyboard.put(qwertySymbolsKeyboard, "QWERTY_SYMBOLS");
    keyboardFromName = Mappy.invertMap(nameFromKeyboard);
    keyboardSet = nameFromKeyboard.keySet();
  }
  
  private Keyboard newKeyboard(final int layoutResourceId) {
    return new Keyboard(this, layoutResourceId, isFullscreenMode());
  }
  
  @SuppressLint("InflateParams")
  private void initialiseInputContainer() {
    inputContainer =
      (InputContainer)
        getLayoutInflater().inflate(R.layout.input_container, null);
    inputContainer.setOnInputListener(this);
    inputContainer.setKeyboard(loadSavedKeyboard());
  }
  
  @Override
  public void onStartInput(
    final EditorInfo editorInfo,
    final boolean isRestarting
  )
  {
    super.onStartInput(editorInfo, isRestarting);
    
    inputOptionsBits = editorInfo.imeOptions;
    enterKeyHasAction =
      (inputOptionsBits & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0;
  }
  
  @Override
  public void onStartInputView(
    final EditorInfo editorInfo,
    final boolean isRestarting
  )
  {
    super.onStartInputView(editorInfo, isRestarting);
    setEnterKeyDisplayText();
    inputContainer.showStrokeSequenceBar();
    inputContainer.showKeyPreviewPlane();
  }
  
  private void setEnterKeyDisplayText() {
    
    String enterKeyDisplayText = null;
    switch (inputOptionsBits & EditorInfo.IME_MASK_ACTION) {
      case EditorInfo.IME_ACTION_DONE:
        enterKeyDisplayText = getString(R.string.display_text__done);
        break;
      case EditorInfo.IME_ACTION_GO:
        enterKeyDisplayText = getString(R.string.display_text__go);
        break;
      case EditorInfo.IME_ACTION_NEXT:
        enterKeyDisplayText = getString(R.string.display_text__next);
        break;
      case EditorInfo.IME_ACTION_PREVIOUS:
        enterKeyDisplayText = getString(R.string.display_text__previous);
        break;
      case EditorInfo.IME_ACTION_SEARCH:
        enterKeyDisplayText = getString(R.string.display_text__search);
        break;
      case EditorInfo.IME_ACTION_SEND:
        enterKeyDisplayText = getString(R.string.display_text__send);
        break;
    }
    if (!enterKeyHasAction || enterKeyDisplayText == null) {
      enterKeyDisplayText = getString(R.string.display_text__return);
    }
    
    for (final Keyboard keyboard : keyboardSet) {
      for (final Key key : keyboard.getKeyList()) {
        if (key.valueText.equals("ENTER")) {
          key.displayText = enterKeyDisplayText;
        }
      }
    }
  }
  
  @Override
  public void onComputeInsets(final InputMethodService.Insets insets) {
    super.onComputeInsets(insets);
    if (inputContainer != null) { // check needed in API level 30
      final int touchableTopY = inputContainer.getTouchableTopY();
      // API level 28 is dumb, see <https://stackoverflow.com/a/53326786>
      if (touchableTopY > 0) {
        insets.visibleTopInsets = touchableTopY;
        insets.contentTopInsets = touchableTopY;
      }
    }
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
          Stringy.isAscii(characterBeforeCursor)
            ? BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII
            : BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8
        );
        inputContainer.setKeyRepeatIntervalMilliseconds(
          nextBackspaceIntervalMilliseconds
        );
        break;
      
      case "SWITCH_TO_STROKES":
      case "SWITCH_TO_STROKES_SYMBOLS_1":
      case "SWITCH_TO_STROKES_SYMBOLS_2":
      case "SWITCH_TO_QWERTY":
      case "SWITCH_TO_QWERTY_SYMBOLS":
        final String keyboardName =
          Stringy.removePrefix("SWITCH_TO_", valueText);
        final Keyboard keyboard = keyboardFromName.get(keyboardName);
        inputContainer.setKeyboard(keyboard);
        break;
      
      case "SPACE":
        inputConnection.commitText(SPACE, 1);
        break;
      
      case "ENTER":
        if (enterKeyHasAction) {
          inputConnection.performEditorAction(inputOptionsBits);
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
      Contexty.showSystemKeyboardSwitcher(this);
    }
    else if (valueText.equals("ENTER")) {
      inputContainer.toggleDebugMode();
    }
  }
  
  @Override
  public void onSwipe(final String valueText) {
    
    if (valueText.equals("SPACE")) {
      
      final Keyboard keyboard = inputContainer.getKeyboard();
      final String keyboardName = nameFromKeyboard.get(keyboard);
      
      if (keyboardName == null) {
        return;
      }
      switch (keyboardName) {
        case "STROKES":
        case "STROKES_SYMBOLS_1":
        case "STROKES_SYMBOLS_2":
          inputContainer.setKeyboard(qwertyKeyboard);
          break;
        case "QWERTY":
        case "QWERTY_SYMBOLS":
          inputContainer.setKeyboard(strokesKeyboard);
          break;
      }
    }
  }
  
  @Override
  public Keyboard loadSavedKeyboard() {
    final String savedKeyboardName =
      Contexty.loadPreferenceString(
        getApplicationContext(),
        PREFERENCES_FILE_NAME,
        "keyboardName"
      );
    final Keyboard savedKeyboard = keyboardFromName.get(savedKeyboardName);
    if (savedKeyboard == null) {
      return strokesKeyboard;
    }
    else {
      return savedKeyboard;
    }
  }
  
  @Override
  public void saveKeyboard(final Keyboard keyboard) {
    final String keyboardName = nameFromKeyboard.get(keyboard);
    Contexty.savePreferenceString(
      getApplicationContext(),
      PREFERENCES_FILE_NAME,
      "keyboardName",
      keyboardName
    );
  }
  
}
