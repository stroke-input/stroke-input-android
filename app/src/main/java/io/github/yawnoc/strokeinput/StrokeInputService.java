/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    - Actually complete the stroke input data set
    - Actually complete the phrase data set
*/
public class StrokeInputService
  extends InputMethodService
  implements InputContainer.OnInputListener, CandidatesBarAdapter.OnCandidateListener
{
  
  public static final String SHIFT_KEY_VALUE_TEXT = "SHIFT";
  public static final String ENTER_KEY_VALUE_TEXT = "ENTER";
  private static final String BACKSPACE_VALUE_TEXT = "BACKSPACE";
  private static final String SPACE_BAR_VALUE_TEXT = "SPACE";
  
  public static final String STROKE_DIGIT_1 = "1";
  public static final String STROKE_DIGIT_2 = "2";
  public static final String STROKE_DIGIT_3 = "3";
  public static final String STROKE_DIGIT_4 = "4";
  public static final String STROKE_DIGIT_5 = "5";
  
  private static final String STROKE_KEY_VALUE_TEXT_PREFIX = "STROKE_";
  private static final String STROKE_1_VALUE_TEXT = STROKE_KEY_VALUE_TEXT_PREFIX + STROKE_DIGIT_1;
  private static final String STROKE_2_VALUE_TEXT = STROKE_KEY_VALUE_TEXT_PREFIX + STROKE_DIGIT_2;
  private static final String STROKE_3_VALUE_TEXT = STROKE_KEY_VALUE_TEXT_PREFIX + STROKE_DIGIT_3;
  private static final String STROKE_4_VALUE_TEXT = STROKE_KEY_VALUE_TEXT_PREFIX + STROKE_DIGIT_4;
  private static final String STROKE_5_VALUE_TEXT = STROKE_KEY_VALUE_TEXT_PREFIX + STROKE_DIGIT_5;
  
  private static final String STROKES_KEYBOARD_NAME = "STROKES";
  private static final String STROKES_SYMBOLS_1_KEYBOARD_NAME = "STROKES_SYMBOLS_1";
  private static final String STROKES_SYMBOLS_2_KEYBOARD_NAME = "STROKES_SYMBOLS_2";
  private static final String QWERTY_KEYBOARD_NAME = "QWERTY";
  private static final String QWERTY_SYMBOLS_KEYBOARD_NAME = "QWERTY_SYMBOLS";
  
  private static final String SWITCH_KEYBOARD_VALUE_TEXT_PREFIX = "SWITCH_TO_";
  private static final String
    SWITCH_TO_STROKES_VALUE_TEXT = SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_KEYBOARD_NAME;
  private static final String
    SWITCH_TO_STROKES_SYMBOLS_1_VALUE_TEXT = SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_SYMBOLS_1_KEYBOARD_NAME;
  private static final String
    SWITCH_TO_STROKES_SYMBOLS_2_VALUE_TEXT = SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_SYMBOLS_2_KEYBOARD_NAME;
  private static final String
    SWITCH_TO_QWERTY_VALUE_TEXT = SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + QWERTY_KEYBOARD_NAME;
  private static final String
    SWITCH_TO_QWERTY_SYMBOLS_VALUE_TEXT = SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + QWERTY_SYMBOLS_KEYBOARD_NAME;
  
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII = 50;
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8 = 100;
  
  public static final String PREFERENCES_FILE_NAME = "preferences.txt";
  private static final String SEQUENCE_CHARACTERS_FILE_NAME = "sequence-characters.txt";
  private static final String RANKING_FILE_NAME = "ranking.txt";
  private static final String PHRASES_FILE_NAME = "phrases.txt";
  
  public static final String CANDIDATE_ORDER_PREFERENCE_KEY = "candidateOrderPreference";
  private static final String KEYBOARD_NAME_PREFERENCE_KEY = "keyboardName";
  
  private static final int RANKING_PENALTY_PER_CHAR = 3000;
  private static final int MAX_PREFIX_MATCH_COUNT = 20;
  private static final int MAX_PHRASE_COMPLETION_COUNT = 25;
  private static final int MAX_PHRASE_LENGTH = 6;
  
  Keyboard strokesKeyboard;
  Keyboard strokesSymbols1Keyboard;
  Keyboard strokesSymbols2Keyboard;
  Keyboard qwertyKeyboard;
  Keyboard qwertySymbolsKeyboard;
  
  private Map<Keyboard, String> nameFromKeyboard;
  private Map<String, Keyboard> keyboardFromName;
  private Set<Keyboard> keyboardSet;
  
  private InputContainer inputContainer;
  
  private NavigableMap<String, CharactersData> charactersDataFromStrokeDigitSequence;
  private Map<String, Integer> sortingRankFromCharacter;
  private NavigableSet<String> phraseSet;
  
  private String strokeDigitSequence = "";
  private List<String> candidateList = new ArrayList<>();
  private final List<String> phraseCompletionFirstCharacterList = new ArrayList<>();
  
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
    inputContainer.setOnInputListener(this);
    inputContainer.setCandidateListener(this);
    inputContainer.setKeyboard(loadSavedKeyboard());
  }
  
  private void initialiseStrokeInput() {
    
    charactersDataFromStrokeDigitSequence = new TreeMap<>();
    
    final long charactersDataStartMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(SEQUENCE_CHARACTERS_FILE_NAME);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (!isCommentLine(line)) {
          putSequenceAndCharactersDataIntoMap(line, charactersDataFromStrokeDigitSequence);
        }
      }
      
    }
    catch (IOException exception) {
      exception.printStackTrace();
    }
    
    final long charactersDataEndMillis = System.currentTimeMillis();
    Log.i(
      "StrokeInputService",
      "Loading of characters data: "
        + (charactersDataEndMillis - charactersDataStartMillis)
        + " milliseconds"
    );
    
    sortingRankFromCharacter = new HashMap<>();
    
    final long sortingRankStartMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(RANKING_FILE_NAME);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      int currentRank = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (!isCommentLine(line)) {
          for (final String character : Stringy.toCharacterList(line)) {
            if (!sortingRankFromCharacter.containsKey(character)) {
              currentRank++;
              sortingRankFromCharacter.put(character, currentRank);
            }
          }
        }
      }
      
    }
    catch (IOException exception) {
      exception.printStackTrace();
    }
    
    final long sortingRankEndMillis = System.currentTimeMillis();
    Log.i(
      "StrokeInputService",
      "Loading of sorting rank: "
        + (sortingRankEndMillis - sortingRankStartMillis)
        + " milliseconds"
    );
    
    phraseSet = new TreeSet<>();
    
    final long phraseSetStartMillis = System.currentTimeMillis();
    
    try {
      
      final InputStream inputStream = getAssets().open(PHRASES_FILE_NAME);
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
    
    final long phraseSetEndMillis = System.currentTimeMillis();
    Log.i(
      "StrokeInputService",
      "Loading of phrase set: "
        + (phraseSetEndMillis - phraseSetStartMillis)
        + " milliseconds"
    );
    
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean isCommentLine(final String line) {
    return line.startsWith("#") || line.length() == 0;
  }
  
  private void putSequenceAndCharactersDataIntoMap(
    final String line,
    final Map<String, CharactersData> charactersDataFromStrokeDigitSequence
  )
  {
    final String[] sunderedLineArray = Stringy.sunder(line, "\t");
    final String strokeDigitSequence = sunderedLineArray[0];
    final String commaSeparatedCharacters = sunderedLineArray[1];
    
    charactersDataFromStrokeDigitSequence.put(
      strokeDigitSequence,
      new CharactersData(commaSeparatedCharacters)
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
        if (key.valueText.equals(ENTER_KEY_VALUE_TEXT)) {
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
      
      case STROKE_1_VALUE_TEXT:
      case STROKE_2_VALUE_TEXT:
      case STROKE_3_VALUE_TEXT:
      case STROKE_4_VALUE_TEXT:
      case STROKE_5_VALUE_TEXT:
        final String strokeDigit = Stringy.removePrefix(STROKE_KEY_VALUE_TEXT_PREFIX, valueText);
        effectStrokeAppend(strokeDigit);
        break;
      
      case BACKSPACE_VALUE_TEXT:
        effectBackspace(inputConnection);
        break;
      
      case SWITCH_TO_STROKES_VALUE_TEXT:
      case SWITCH_TO_STROKES_SYMBOLS_1_VALUE_TEXT:
      case SWITCH_TO_STROKES_SYMBOLS_2_VALUE_TEXT:
      case SWITCH_TO_QWERTY_VALUE_TEXT:
      case SWITCH_TO_QWERTY_SYMBOLS_VALUE_TEXT:
        final String keyboardName = Stringy.removePrefix(SWITCH_KEYBOARD_VALUE_TEXT_PREFIX, valueText);
        effectKeyboardSwitch(keyboardName);
        break;
      
      case SPACE_BAR_VALUE_TEXT:
        effectSpaceKey(inputConnection);
        break;
      
      case ENTER_KEY_VALUE_TEXT:
        effectEnterKey(inputConnection);
        break;
      
      default:
        effectOrdinaryKey(inputConnection, valueText);
      
    }
    
  }
  
  private void effectStrokeAppend(final String strokeDigit) {
    
    final String newStrokeDigitSequence = strokeDigitSequence + strokeDigit;
    final List<String> newCandidateList = computeCandidateList(newStrokeDigitSequence);
    if (newCandidateList.size() > 0) {
      setStrokeDigitSequence(newStrokeDigitSequence);
      setCandidateList(newCandidateList);
    }
    
  }
  
  private void effectBackspace(final InputConnection inputConnection) {
    
    if (strokeDigitSequence.length() > 0) {
      
      final String newStrokeDigitSequence = Stringy.removeSuffix("(?s).", strokeDigitSequence);
      final List<String> newCandidateList = computeCandidateList(newStrokeDigitSequence);
      
      setStrokeDigitSequence(newStrokeDigitSequence);
      setCandidateList(newCandidateList);
      
      inputContainer.setKeyRepeatIntervalMilliseconds(BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8);
      
    }
    
    else {
      
      final String upToOneCharacterBeforeCursor = getTextBeforeCursor(inputConnection, 1);
      
      if (upToOneCharacterBeforeCursor.length() > 0) {
        
        final CharSequence selection = inputConnection.getSelectedText(0);
        
        if (TextUtils.isEmpty(selection)) {
          if (Build.VERSION.SDK_INT >= 24) {
            inputConnection.deleteSurroundingTextInCodePoints(1, 0);
          }
          else {
            final int charCount = ( // damn you Java for using UTF-16
              Stringy.firstCharacterIsBasic(upToOneCharacterBeforeCursor)
                ? 1 // Basic Multilingual Plane (single character)
                : 2 // Supplementary Multilingual Plane (surrogate pair)
            );
            inputConnection.deleteSurroundingText(charCount, 0);
          }
        }
        else {
          inputConnection.commitText("", 1);
        }
        
        setCandidateListForPhraseCompletion(inputConnection);
      }
      else { // for apps like Termux
        
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
      }
      
      final int nextBackspaceIntervalMilliseconds = (
        Stringy.isAscii(upToOneCharacterBeforeCursor)
          ? BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII
          : BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8
      );
      inputContainer.setKeyRepeatIntervalMilliseconds(nextBackspaceIntervalMilliseconds);
      
    }
    
  }
  
  private void effectKeyboardSwitch(final String keyboardName) {
    final Keyboard keyboard = keyboardFromName.get(keyboardName);
    inputContainer.setKeyboard(keyboard);
  }
  
  private void effectSpaceKey(final InputConnection inputConnection) {
    if (strokeDigitSequence.length() > 0) {
      onCandidate(getFirstCandidate());
    }
    inputConnection.commitText(" ", 1);
  }
  
  private void effectEnterKey(final InputConnection inputConnection) {
    if (strokeDigitSequence.length() > 0) {
      onCandidate(getFirstCandidate());
    }
    else if (enterKeyHasAction) {
      inputConnection.performEditorAction(inputOptionsBits);
    }
    else {
      inputConnection.commitText("\n", 1);
    }
  }
  
  private void effectOrdinaryKey(final InputConnection inputConnection, final String valueText) {
    if (strokeDigitSequence.length() > 0) {
      onCandidate(getFirstCandidate());
    }
    inputConnection.commitText(valueText, 1);
  }
  
  @Override
  public void onLongPress(final String valueText) {
    
    if (valueText.equals(SPACE_BAR_VALUE_TEXT)) {
      Contexty.showSystemKeyboardSwitcher(this);
    }
    
  }
  
  @Override
  public void onSwipe(final String valueText) {
    
    if (valueText.equals(SPACE_BAR_VALUE_TEXT)) {
      
      final Keyboard keyboard = inputContainer.getKeyboard();
      final String keyboardName = nameFromKeyboard.get(keyboard);
      
      if (keyboardName == null) {
        return;
      }
      switch (keyboardName) {
        case STROKES_KEYBOARD_NAME:
        case STROKES_SYMBOLS_1_KEYBOARD_NAME:
        case STROKES_SYMBOLS_2_KEYBOARD_NAME:
          inputContainer.setKeyboard(qwertyKeyboard);
          break;
        case QWERTY_KEYBOARD_NAME:
        case QWERTY_SYMBOLS_KEYBOARD_NAME:
          inputContainer.setKeyboard(strokesKeyboard);
          break;
      }
      
    }
    
  }
  
  @Override
  public Keyboard loadSavedKeyboard() {
    final String savedKeyboardName =
      Contexty.loadPreferenceString(getApplicationContext(), PREFERENCES_FILE_NAME, KEYBOARD_NAME_PREFERENCE_KEY);
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
      KEYBOARD_NAME_PREFERENCE_KEY,
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
    setStrokeDigitSequence("");
    setCandidateListForPhraseCompletion(inputConnection);
    
  }
  
  private void setStrokeDigitSequence(final String strokeDigitSequence) {
    this.strokeDigitSequence = strokeDigitSequence;
    inputContainer.setStrokeDigitSequence(strokeDigitSequence);
  }
  
  private void setCandidateList(final List<String> candidateList) {
    this.candidateList = candidateList;
    inputContainer.setCandidateList(candidateList);
  }
  
  private void setCandidateListForPhraseCompletion(final InputConnection inputConnection) {
    
    List<String> phraseCompletionCandidateList =
      computePhraseCompletionCandidateList(inputConnection, MAX_PHRASE_COMPLETION_COUNT);
    
    phraseCompletionFirstCharacterList.clear();
    for (final String phraseCompletionCandidate : phraseCompletionCandidateList) {
      phraseCompletionFirstCharacterList.add(Stringy.getFirstCharacter(phraseCompletionCandidate));
    }
    
    setCandidateList(phraseCompletionCandidateList);
    
  }
  
  private Comparator<String> candidateComparator() {
    return candidateComparator(Collections.emptyList());
  }
  
  @SuppressWarnings("ComparatorCombinators")
  private Comparator<String> candidateComparator(final List<String> phraseCompletionFirstCharacterList) {
    return
      (string1, string2) ->
        Integer.compare(
          computeSortingRank(string1, phraseCompletionFirstCharacterList),
          computeSortingRank(string2, phraseCompletionFirstCharacterList)
        );
  }
  
  /*
    Compute the sorting rank for a string, based on its first character.
    The overall rank consists of a base rank plus a length penalty.
    The base rank is thus:
      If the first character matches that of a phrase completion candidate:
        {negative infinity} + {phrase completion index};
      Else, if the first character is a common character (in "ranking.txt"):
        {ranking in "ranking.txt"};
      Else:
        {positive infinity}.
  */
  private int computeSortingRank(final String string, final List<String> phraseCompletionFirstCharacterList) {
    
    final int lengthPenalty = (string.length() - 1) * RANKING_PENALTY_PER_CHAR;
    final String firstCharacter = Stringy.getFirstCharacter(string);
    
    final int phraseCompletionIndex = phraseCompletionFirstCharacterList.indexOf(firstCharacter);
    if (phraseCompletionIndex > 0) {
      return Integer.MIN_VALUE + phraseCompletionIndex + lengthPenalty;
    }
    
    final Integer baseRank = sortingRankFromCharacter.get(firstCharacter);
    if (baseRank != null) {
      return baseRank + lengthPenalty;
    }
    
    return Integer.MAX_VALUE;
    
  }
  
  private List<String> computeCandidateList(final String strokeDigitSequence) {
    
    if (strokeDigitSequence.length() == 0) {
      return Collections.emptyList();
    }
    
    final CharactersData exactMatchCharactersData = charactersDataFromStrokeDigitSequence.get(strokeDigitSequence);
    final List<String> exactMatchCandidateList = (
      exactMatchCharactersData == null
        ? Collections.emptyList()
        : exactMatchCharactersData.toCandidateList(candidateComparator(phraseCompletionFirstCharacterList))
    );
    
    final CharactersData prefixMatchCharactersData = new CharactersData("");
    final Collection<CharactersData> prefixMatchCharactersDataCollection = (
      charactersDataFromStrokeDigitSequence
        .subMap(
          strokeDigitSequence, false,
          strokeDigitSequence + Character.MAX_VALUE, false
        )
        .values()
    );
    for (final CharactersData charactersData : prefixMatchCharactersDataCollection) {
      prefixMatchCharactersData.addData(charactersData);
    }
    final List<String> prefixMatchCandidateList =
      prefixMatchCharactersData
        .toCandidateList(candidateComparator(phraseCompletionFirstCharacterList), MAX_PREFIX_MATCH_COUNT);
    
    final List<String> candidateList = new ArrayList<>();
    candidateList.addAll(exactMatchCandidateList);
    candidateList.addAll(prefixMatchCandidateList);
    
    return candidateList;
    
  }
  
  private String getFirstCandidate() {
    try {
      return candidateList.get(0);
    }
    catch (IndexOutOfBoundsException exception) {
      return "";
    }
  }
  
  /*
    Compute the phrase completion candidate list.
    Longer matches with the text before the cursor are ranked earlier.
  */
  @SuppressWarnings("SameParameterValue")
  private List<String> computePhraseCompletionCandidateList(
    final InputConnection inputConnection,
    final int maxCandidateCount
  )
  {
    
    final List<String> phraseCompletionCandidateList = new ArrayList<>();
    
    for (
      String phrasePrefix = getTextBeforeCursor(inputConnection, MAX_PHRASE_LENGTH - 1);
      phrasePrefix.length() > 0;
      phrasePrefix = Stringy.removePrefix("(?s).", phrasePrefix)
    )
    {
      final Set<String> prefixMatchPhraseCandidateSet =
        phraseSet.subSet(
          phrasePrefix, false,
          phrasePrefix + Character.MAX_VALUE, false
        );
      final List<String> prefixMatchPhraseCompletionList = new ArrayList<>();
      
      for (final String phraseCandidate : prefixMatchPhraseCandidateSet) {
        final String phraseCompletion = Stringy.removePrefix(phrasePrefix, phraseCandidate);
        if (!phraseCompletionCandidateList.contains(phraseCompletion)) {
          prefixMatchPhraseCompletionList.add(phraseCompletion);
        }
      }
      Collections.sort(prefixMatchPhraseCompletionList, candidateComparator());
      phraseCompletionCandidateList.addAll(prefixMatchPhraseCompletionList);
    }
    
    final int candidateCount = Math.min(phraseCompletionCandidateList.size(), maxCandidateCount);
    
    return new ArrayList<>(phraseCompletionCandidateList.subList(0, candidateCount));
    
  }
  
  private String getTextBeforeCursor(final InputConnection inputConnection, final int characterCount) {
    
    if (inputIsPassword) {
      return ""; // don't read passwords
    }
    
    return (String) inputConnection.getTextBeforeCursor(characterCount, 0);
    
  }
  
}
