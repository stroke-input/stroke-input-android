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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.yawnoc.utilities.Contexty;
import io.github.yawnoc.utilities.Mappy;
import io.github.yawnoc.utilities.Stringy;

/*
  An InputMethodService for the Stroke Input Method (筆畫輸入法).
  TODO:
    - Make the current rewrite (branch 'layout-rewrite') actually work
    - Actually complete the phrase data set
*/
public class StrokeInputService
  extends InputMethodService
  implements CandidatesViewAdapter.CandidateListener, KeyboardView.KeyboardListener
{
  
  public static final String SHIFT_KEY_VALUE_TEXT = "SHIFT";
  public static final String ENTER_KEY_VALUE_TEXT = "ENTER";
  
  private static final String STROKES_KEYBOARD_NAME = "STROKES";
  private static final String STROKES_SYMBOLS_1_KEYBOARD_NAME = "STROKES_SYMBOLS_1";
  private static final String STROKES_SYMBOLS_2_KEYBOARD_NAME = "STROKES_SYMBOLS_2";
  private static final String QWERTY_KEYBOARD_NAME = "QWERTY";
  private static final String QWERTY_SYMBOLS_KEYBOARD_NAME = "QWERTY_SYMBOLS";
  
  public static final String PREFERENCES_FILE_NAME = "preferences.txt";
  private static final String KEYBOARD_NAME_PREFERENCE_KEY = "keyboardName";
  
  private static final String SEQUENCE_CHARACTERS_FILE_NAME = "sequence-characters.txt";
  private static final String CHARACTERS_FILE_NAME_TRADITIONAL = "characters-traditional.txt";
  private static final String CHARACTERS_FILE_NAME_SIMPLIFIED = "characters-simplified.txt";
  private static final String RANKING_FILE_NAME_TRADITIONAL = "ranking-traditional.txt";
  private static final String RANKING_FILE_NAME_SIMPLIFIED = "ranking-simplified.txt";
  private static final String PHRASES_FILE_NAME_TRADITIONAL = "phrases-traditional.txt";
  private static final String PHRASES_FILE_NAME_SIMPLIFIED = "phrases-simplified.txt";
  
  private static final int LAG_PREVENTION_CODE_POINT_COUNT = 1400;
  
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
  private Set<Integer> codePointSetTraditional;
  private Set<Integer> codePointSetSimplified;
  private Map<Integer, Integer> sortingRankFromCodePointTraditional;
  private Map<Integer, Integer> sortingRankFromCodePointSimplified;
  private Set<Integer> commonCodePointSetTraditional;
  private Set<Integer> commonCodePointSetSimplified;
  private NavigableSet<String> phraseSetTraditional;
  private NavigableSet<String> phraseSetSimplified;
  
  private Set<Integer> unpreferredCodePointSet;
  private Map<Integer, Integer> sortingRankFromCodePoint;
  private Set<Integer> commonCodePointSet;
  private NavigableSet<String> phraseSet;
  
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
    inputContainer.initialiseCandidatesView(this);
    inputContainer.initialiseKeyboardView(this, loadSavedKeyboard());
    inputContainer.initialiseStrokeSequenceBar(this);
  }
  
  private Keyboard loadSavedKeyboard() {
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
  
  private void initialiseStrokeInput() {
    
    charactersFromStrokeDigitSequence = new TreeMap<>();
    loadSequenceCharactersDataIntoMap(SEQUENCE_CHARACTERS_FILE_NAME, charactersFromStrokeDigitSequence);
    
    codePointSetTraditional = new HashSet<>();
    codePointSetSimplified = new HashSet<>();
    loadCharactersIntoCodePointSet(CHARACTERS_FILE_NAME_TRADITIONAL, codePointSetTraditional);
    loadCharactersIntoCodePointSet(CHARACTERS_FILE_NAME_SIMPLIFIED, codePointSetSimplified);
    
    sortingRankFromCodePointTraditional = new HashMap<>();
    sortingRankFromCodePointSimplified = new HashMap<>();
    commonCodePointSetTraditional = new HashSet<>();
    commonCodePointSetSimplified = new HashSet<>();
    loadRankingData(RANKING_FILE_NAME_TRADITIONAL, sortingRankFromCodePointTraditional, commonCodePointSetTraditional);
    loadRankingData(RANKING_FILE_NAME_SIMPLIFIED, sortingRankFromCodePointSimplified, commonCodePointSetSimplified);
    
    phraseSetTraditional = new TreeSet<>();
    phraseSetSimplified = new TreeSet<>();
    loadPhrasesIntoSet(PHRASES_FILE_NAME_TRADITIONAL, phraseSetTraditional);
    loadPhrasesIntoSet(PHRASES_FILE_NAME_SIMPLIFIED, phraseSetSimplified);
    
    updateCandidateOrderPreference();
    
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private static boolean isCommentLine(final String line) {
    return line.startsWith("#") || line.length() == 0;
  }
  
  @SuppressWarnings("SameParameterValue")
  private void loadSequenceCharactersDataIntoMap(
    final String sequenceCharactersFileName,
    final Map<String, String> charactersFromStrokeDigitSequence
  )
  {
    
    final long startMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(sequenceCharactersFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        
        if (!isCommentLine(line)) {
          
          final String[] sunderedLineArray = Stringy.sunder(line, "\t");
          final String strokeDigitSequence = sunderedLineArray[0];
          final String characters = sunderedLineArray[1];
          
          charactersFromStrokeDigitSequence.put(strokeDigitSequence, characters);
          
        }
        
      }
      
    }
    
    catch (IOException exception) {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(sequenceCharactersFileName, endMillis - startMillis);
    
  }
  
  private void loadCharactersIntoCodePointSet(final String charactersFileName, final Set<Integer> codePointSet) {
    
    final long startMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(charactersFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (!isCommentLine(line)) {
          codePointSet.add(Stringy.getFirstCodePoint(line));
        }
      }
      
    }
    
    catch (IOException exception) {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(charactersFileName, endMillis - startMillis);
    
  }
  
  private void loadRankingData(
    final String rankingFileName,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final Set<Integer> commonCodePointSet
  )
  {
    
    final long startMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(rankingFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      int currentRank = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (!isCommentLine(line)) {
          for (final int codePoint : Stringy.toCodePointList(line)) {
            currentRank++;
            sortingRankFromCodePoint.put(codePoint, currentRank);
            if (currentRank < LAG_PREVENTION_CODE_POINT_COUNT) {
              commonCodePointSet.add(codePoint);
            }
          }
        }
      }
      
    }
    
    catch (IOException exception) {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(rankingFileName, endMillis - startMillis);
    
  }
  
  private void loadPhrasesIntoSet(final String phrasesFileName, final Set<String> phraseSet) {
    
    final long startMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(phrasesFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (!isCommentLine(line)) {
          phraseSet.add(line);
        }
      }
      
    }
    
    catch (IOException exception) {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(phrasesFileName, endMillis - startMillis);
    
  }
  
  private void sendLoadingTimeLog(final String fileName, final long millis) {
    Log.i(
      "StrokeInputService",
      "Loaded '" + fileName + "' in " + millis + " milliseconds"
    );
  }
  
  @Override
  public void onStartInput(final EditorInfo editorInfo, final boolean isRestarting) {
    
    super.onStartInput(editorInfo, isRestarting);
    
    inputOptionsBits = editorInfo.imeOptions;
    enterKeyHasAction = (inputOptionsBits & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0;
    
    final int inputTypeBits = editorInfo.inputType;
    final int inputClassBits = inputTypeBits & InputType.TYPE_MASK_CLASS;
    final int inputVariationBits = inputTypeBits & InputType.TYPE_MASK_VARIATION;
    
    switch (inputClassBits) {
      
      case InputType.TYPE_CLASS_NUMBER:
        inputIsPassword = inputVariationBits == InputType.TYPE_NUMBER_VARIATION_PASSWORD;
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
  public void onStartInputView(final EditorInfo editorInfo, final boolean isRestarting) {
    super.onStartInputView(editorInfo, isRestarting);
    setEnterKeyDisplayText();
    inputContainer.showStrokeSequenceBar();
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
        if (key.valueText.equals(ENTER_KEY_VALUE_TEXT)) {
          key.displayText = enterKeyDisplayText;
        }
      }
    }
    
  }
  
  // TODO: onComputeInsets
  
  @Override
  public void onCandidate(final String candidate) {
  }
  
  @Override
  public void onKey(final String valueText) {
  }
  
  @Override
  public void onLongPress(final String valueText) {
  }
  
  @Override
  public void onSwipe(final String valueText) {
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
  
  private void updateCandidateOrderPreference() {
    
    if (shouldPreferTraditional()) {
      unpreferredCodePointSet = codePointSetSimplified;
      sortingRankFromCodePoint = sortingRankFromCodePointTraditional;
      commonCodePointSet = commonCodePointSetTraditional;
      phraseSet = phraseSetTraditional;
    }
    else {
      unpreferredCodePointSet = codePointSetTraditional;
      sortingRankFromCodePoint = sortingRankFromCodePointSimplified;
      commonCodePointSet = commonCodePointSetSimplified;
      phraseSet = phraseSetSimplified;
    }
    
  }
  
  private boolean shouldPreferTraditional() {
    
    final String savedCandidateOrderPreference =
      MainActivity.loadSavedCandidateOrderPreference(getApplicationContext());
    
    return MainActivity.isTraditionalPreferred(savedCandidateOrderPreference);
    
  }
  
}
