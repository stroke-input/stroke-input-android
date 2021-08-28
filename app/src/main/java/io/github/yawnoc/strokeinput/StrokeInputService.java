/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.yawnoc.utilities.Contexty;
import io.github.yawnoc.utilities.Mappy;
import io.github.yawnoc.utilities.Stringy;

/*
  An InputMethodService for the Stroke Input Method (筆畫輸入法).
  TODO:
    - Character candidates
    - Candidate sorting (with preference for simplified)
    - Phrase candidates
    - Actually complete the stroke input data set
*/
public class StrokeInputService
  extends InputMethodService
  implements
    InputContainer.OnInputListener, CandidatesBarAdapter.OnCandidateListener
{
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII = 50;
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8 = 100;
  
  private static final String PREFERENCES_FILE_NAME = "preferences.txt";
  private static final String SEQUENCE_CHARACTERS_FILE_NAME =
    "sequence-exact-characters.txt";
  
  Keyboard strokesKeyboard;
  Keyboard strokesSymbols1Keyboard;
  Keyboard strokesSymbols2Keyboard;
  Keyboard qwertyKeyboard;
  Keyboard qwertySymbolsKeyboard;
  
  private Map<Keyboard, String> nameFromKeyboard;
  private Map<String, Keyboard> keyboardFromName;
  private Set<Keyboard> keyboardSet;
  
  private InputContainer inputContainer;
  
  private NavigableMap<String, String> charactersFromStrokeDigitSequence;
  
  private String strokeDigitsSequence = "";
  private List<String> candidateList = new ArrayList<>();
  
  private int inputOptionsBits;
  private boolean enterKeyHasAction;
  private boolean inputIsPassword;
  
  @Override
  public View onCreateInputView() {
    
    initialiseKeyboards();
    initialiseInputContainer();
    initialiseStrokeInput();
    
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
    inputContainer.setCandidateListener(this);
    inputContainer.setKeyboard(loadSavedKeyboard());
  }
  
  private void initialiseStrokeInput() {
    
    charactersFromStrokeDigitSequence = new TreeMap<>();
    
    try {
      
      final InputStream inputStream =
        getAssets().open(SEQUENCE_CHARACTERS_FILE_NAME);
      final BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream));
      
      final Pattern sequenceCharactersPattern =
        Pattern.compile("([1-5]+)\\t(.+)");
      String line;
      Matcher lineMatcher;
      while ((line = bufferedReader.readLine()) != null) {
        lineMatcher = sequenceCharactersPattern.matcher(line);
        if (lineMatcher.matches()) {
          charactersFromStrokeDigitSequence.put(
            lineMatcher.group(1), // stroke digits sequence
            lineMatcher.group(2)  // characters
          );
        }
      }
    }
    catch (IOException exception) {
      exception.printStackTrace();
    }
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
    
    final int inputTypeBits = editorInfo.inputType;
    final int inputClassBits =
      inputTypeBits & InputType.TYPE_MASK_CLASS;
    final int inputVariationBits =
      inputTypeBits & InputType.TYPE_MASK_VARIATION;
    
    switch (inputClassBits) {
      
      case InputType.TYPE_CLASS_NUMBER:
        inputIsPassword =
          inputVariationBits == InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        break;
      
      case InputType.TYPE_CLASS_TEXT:
        switch (inputVariationBits) {
          case InputType.TYPE_TEXT_VARIATION_PASSWORD:
          case InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
          case InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD:
            inputIsPassword = true;
            break;
          default:
            inputIsPassword = false;
        }
        break;
      
      default:
        inputIsPassword = false;
    }
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
    inputContainer.showCandidatesBar();
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
      
      case "STROKE_1":
      case "STROKE_2":
      case "STROKE_3":
      case "STROKE_4":
      case "STROKE_5":
        final String strokeDigit = Stringy.removePrefix("STROKE_", valueText);
        final String newStrokeDigitSequence =
          strokeDigitsSequence + strokeDigit;
        final List<String> newCandidateList =
          toCandidateList(newStrokeDigitSequence);
        if (newCandidateList.size() > 0) {
          setStrokeDigitsSequence(newStrokeDigitSequence);
          setCandidateList(newCandidateList);
        }
        break;
      
      case "BACKSPACE":
        if (strokeDigitsSequence.length() > 0) {
          setStrokeDigitsSequence(
            Stringy.removeSuffix(".", strokeDigitsSequence)
          );
          inputContainer.setKeyRepeatIntervalMilliseconds(
            BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8
          );
        }
        else {
          inputConnection.sendKeyEvent(
            new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
          );
          inputConnection.sendKeyEvent(
            new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
          );
          final int nextBackspaceIntervalMilliseconds = (
            Stringy.isAscii(getTextBeforeCursor(inputConnection, 1))
              ? BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII
              : BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8
          );
          inputContainer.setKeyRepeatIntervalMilliseconds(
            nextBackspaceIntervalMilliseconds
          );
        }
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
        if (strokeDigitsSequence.length() > 0) {
          onCandidate(getFirstCandidate());
        }
        inputConnection.commitText(" ", 1);
        break;
      
      case "ENTER":
        if (strokeDigitsSequence.length() > 0) {
          onCandidate(getFirstCandidate());
        }
        else if (enterKeyHasAction) {
          inputConnection.performEditorAction(inputOptionsBits);
        }
        else {
          inputConnection.commitText("\n", 1);
        }
        break;
      
      default:
        if (strokeDigitsSequence.length() > 0) {
          onCandidate(getFirstCandidate());
        }
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
  
  @Override
  public void onCandidate(final String candidate) {
    
    final InputConnection inputConnection = getCurrentInputConnection();
    if (inputConnection == null) {
      return;
    }
    
    inputConnection.commitText(candidate, 1);
    setStrokeDigitsSequence("");
  }
  
  private void setStrokeDigitsSequence(final String strokeDigitsSequence) {
    this.strokeDigitsSequence = strokeDigitsSequence;
    inputContainer.setStrokeDigitsSequence(strokeDigitsSequence);
  }
  
  private void setCandidateList(final List<String> candidateList) {
    this.candidateList = candidateList;
    inputContainer.setCandidateList(candidateList);
  }
  
  private List<String> toCandidateList(final String strokeDigitsSequence) {
    
    String exactMatchCandidates =
      charactersFromStrokeDigitSequence.get(strokeDigitsSequence);
    if (exactMatchCandidates == null) {
      exactMatchCandidates = "";
    }
    
    final Collection<String> prefixMatchCandidatesCollection = (
      charactersFromStrokeDigitSequence
        .subMap(
          strokeDigitsSequence,
          false,
          strokeDigitsSequence + Character.MAX_VALUE,
          false
        )
        .values()
    );
    String prefixMatchCandidates =
      TextUtils.join("", prefixMatchCandidatesCollection);
    
    if (exactMatchCandidates.equals("") && prefixMatchCandidates.equals("")) {
      return Collections.emptyList();
    }
    
    // TODO: candidate sorting & delete duplicates
    // TODO: limit number of prefix match candidates
    final String candidates = exactMatchCandidates + prefixMatchCandidates;
    
    return Stringy.toCharacterList(candidates);
  }
  
  private String getFirstCandidate() {
    try {
      return candidateList.get(0);
    }
    catch (IndexOutOfBoundsException exception) {
      return "";
    }
  }
  
  private String getTextBeforeCursor(
    final InputConnection inputConnection,
    final int characterCount
  )
  {
    if (inputIsPassword) {
      return ""; // don't read passwords
    }
    
    return (String) inputConnection.getTextBeforeCursor(characterCount, 0);
  }
  
}
