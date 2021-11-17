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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.yawnoc.utilities.Contexty;
import io.github.yawnoc.utilities.Mappy;

/*
  An InputMethodService for the Stroke Input Method (筆畫輸入法).
  TODO:
    - Make the current rewrite (branch 'layout-rewrite') actually work
    - Actually complete the phrase data set
*/
public class StrokeInputService
  extends InputMethodService
  implements InputContainer.CandidateListener, InputContainer.KeyboardListener
{
  
  private static final String STROKES_KEYBOARD_NAME = "STROKES";
  private static final String STROKES_SYMBOLS_1_KEYBOARD_NAME = "STROKES_SYMBOLS_1";
  private static final String STROKES_SYMBOLS_2_KEYBOARD_NAME = "STROKES_SYMBOLS_2";
  private static final String QWERTY_KEYBOARD_NAME = "QWERTY";
  private static final String QWERTY_SYMBOLS_KEYBOARD_NAME = "QWERTY_SYMBOLS";
  
  public static final String PREFERENCES_FILE_NAME = "preferences.txt";
  private static final String KEYBOARD_NAME_PREFERENCE_KEY = "keyboardName";
  
  Keyboard strokesKeyboard;
  Keyboard strokesSymbols1Keyboard;
  Keyboard strokesSymbols2Keyboard;
  Keyboard qwertyKeyboard;
  Keyboard qwertySymbolsKeyboard;
  
  private Map<Keyboard, String> nameFromKeyboard;
  private Map<String, Keyboard> keyboardFromName;
  private Set<Keyboard> keyboardSet;
  
  private InputContainer inputContainer;
  
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
    nameFromKeyboard.put(strokesKeyboard, STROKES_KEYBOARD_NAME);
    nameFromKeyboard.put(strokesSymbols1Keyboard, STROKES_SYMBOLS_1_KEYBOARD_NAME);
    nameFromKeyboard.put(strokesSymbols2Keyboard, STROKES_SYMBOLS_2_KEYBOARD_NAME);
    nameFromKeyboard.put(qwertyKeyboard, QWERTY_KEYBOARD_NAME);
    nameFromKeyboard.put(qwertySymbolsKeyboard, QWERTY_SYMBOLS_KEYBOARD_NAME);
    keyboardFromName = Mappy.invertMap(nameFromKeyboard);
    keyboardSet = nameFromKeyboard.keySet();
    
  }
  
  private Keyboard newKeyboard(final int layoutResourceId) {
    return new Keyboard(this, layoutResourceId, isFullscreenMode());
  }
  
  @SuppressLint("InflateParams")
  private void initialiseInputContainer() {
    inputContainer = (InputContainer) getLayoutInflater().inflate(R.layout.input_container, null);
    inputContainer.setCandidateListener(this);
    inputContainer.setKeyboardListener(this);
    inputContainer.initialiseCandidatesView();
    inputContainer.initialiseKeyboardView();
  }
  
  @Override
  public Keyboard loadSavedKeyboard() {
    final String savedKeyboardName =
      Contexty.loadPreferenceString(getApplicationContext(), PREFERENCES_FILE_NAME, KEYBOARD_NAME_PREFERENCE_KEY);
    final Keyboard savedKeyboard = keyboardFromName.get(savedKeyboardName);
    if (savedKeyboard != null) {
      return savedKeyboard;
    }
    else {
      return strokesKeyboard;
    }
  }
  
  @Override
  public void saveKeyboard(final Keyboard keyboard) {
    final String keyboardName = nameFromKeyboard.get(keyboard);
    Contexty.savePreferenceString(
      getApplicationContext(),
      PREFERENCES_FILE_NAME,
      KEYBOARD_NAME_PREFERENCE_KEY,
      keyboardName
    );
  }
  
}
