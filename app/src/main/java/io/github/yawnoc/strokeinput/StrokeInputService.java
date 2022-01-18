/*
  Copyright 2021--2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
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
import java.util.HashSet;
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
*/
public class StrokeInputService
  extends InputMethodService
  implements CandidatesViewAdapter.CandidateListener, KeyboardView.KeyboardListener
{
  private static final String LOG_TAG = "StrokeInputService";
  
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
  private static final String STROKES_SYMBOLS_3_KEYBOARD_NAME = "STROKES_SYMBOLS_3";
  private static final String QWERTY_KEYBOARD_NAME = "QWERTY";
  private static final String QWERTY_SYMBOLS_KEYBOARD_NAME = "QWERTY_SYMBOLS";
  
  private static final String SWITCH_KEYBOARD_VALUE_TEXT_PREFIX = "SWITCH_TO_";
  
  private static final String SWITCH_TO_STROKES_VALUE_TEXT =
          SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_KEYBOARD_NAME;
  
  private static final String SWITCH_TO_STROKES_SYMBOLS_1_VALUE_TEXT =
          SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_SYMBOLS_1_KEYBOARD_NAME;
  
  private static final String SWITCH_TO_STROKES_SYMBOLS_2_VALUE_TEXT =
          SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_SYMBOLS_2_KEYBOARD_NAME;
  
  private static final String SWITCH_TO_STROKES_SYMBOLS_3_VALUE_TEXT =
          SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + STROKES_SYMBOLS_3_KEYBOARD_NAME;
  
  private static final String SWITCH_TO_QWERTY_VALUE_TEXT =
          SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + QWERTY_KEYBOARD_NAME;
  
  private static final String SWITCH_TO_QWERTY_SYMBOLS_VALUE_TEXT =
          SWITCH_KEYBOARD_VALUE_TEXT_PREFIX + QWERTY_SYMBOLS_KEYBOARD_NAME;
  
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII = 50;
  private static final int BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8 = 100;
  
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
  private static final int LARGISH_SORTING_RANK = 3000;
  private static final int RANKING_PENALTY_PER_CHAR = 2 * LARGISH_SORTING_RANK;
  private static final int RANKING_PENALTY_UNPREFERRED = 10 * LARGISH_SORTING_RANK;
  private static final int MAX_PREFIX_MATCH_COUNT = 30;
  private static final int MAX_PHRASE_LENGTH = 6;
  
  Keyboard strokesKeyboard;
  Keyboard strokesSymbols1Keyboard;
  Keyboard strokesSymbols2Keyboard;
  Keyboard strokesSymbols3Keyboard;
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
  
  private String strokeDigitSequence = "";
  private List<String> candidateList = new ArrayList<>();
  private final List<Integer> phraseCompletionFirstCodePointList = new ArrayList<>();
  
  private int inputOptionsBits;
  private boolean enterKeyHasAction;
  private boolean inputIsPassword;
  
  @Override
  public void onCreate()
  {
    super.onCreate();
    
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
  
  @SuppressLint("InflateParams")
  @Override
  public View onCreateInputView()
  {
    strokesKeyboard = new Keyboard(this, R.xml.keyboard_strokes);
    strokesSymbols1Keyboard = new Keyboard(this, R.xml.keyboard_strokes_symbols_1);
    strokesSymbols2Keyboard = new Keyboard(this, R.xml.keyboard_strokes_symbols_2);
    strokesSymbols3Keyboard = new Keyboard(this, R.xml.keyboard_strokes_symbols_3);
    qwertyKeyboard = new Keyboard(this, R.xml.keyboard_qwerty);
    qwertySymbolsKeyboard = new Keyboard(this, R.xml.keyboard_qwerty_symbols);
    
    nameFromKeyboard = new HashMap<>();
    nameFromKeyboard.put(strokesKeyboard, STROKES_KEYBOARD_NAME);
    nameFromKeyboard.put(strokesSymbols1Keyboard, STROKES_SYMBOLS_1_KEYBOARD_NAME);
    nameFromKeyboard.put(strokesSymbols2Keyboard, STROKES_SYMBOLS_2_KEYBOARD_NAME);
    nameFromKeyboard.put(strokesSymbols3Keyboard, STROKES_SYMBOLS_3_KEYBOARD_NAME);
    nameFromKeyboard.put(qwertyKeyboard, QWERTY_KEYBOARD_NAME);
    nameFromKeyboard.put(qwertySymbolsKeyboard, QWERTY_SYMBOLS_KEYBOARD_NAME);
    keyboardFromName = Mappy.invertMap(nameFromKeyboard);
    keyboardSet = nameFromKeyboard.keySet();
    
    inputContainer = (InputContainer) getLayoutInflater().inflate(R.layout.input_container, null);
    inputContainer.initialisePopupRecess();
    inputContainer.initialiseStrokeSequenceBar(this);
    inputContainer.initialiseCandidatesView(this);
    inputContainer.initialiseKeyboardView(this, loadSavedKeyboard());
    
    return inputContainer;
  }
  
  private Keyboard loadSavedKeyboard()
  {
    final String savedKeyboardName =
            Contexty.loadPreferenceString(getApplicationContext(), PREFERENCES_FILE_NAME, KEYBOARD_NAME_PREFERENCE_KEY);
    final Keyboard savedKeyboard = keyboardFromName.get(savedKeyboardName);
    if (savedKeyboard != null)
    {
      return savedKeyboard;
    }
    else
    {
      return strokesKeyboard;
    }
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private static boolean isCommentLine(final String line)
  {
    return line.startsWith("#") || line.length() == 0;
  }
  
  @SuppressWarnings("SameParameterValue")
  private void loadSequenceCharactersDataIntoMap(
    final String sequenceCharactersFileName,
    final Map<String, String> charactersFromStrokeDigitSequence
  )
  {
    final long startMillis = System.currentTimeMillis();
    
    try
    {
      final InputStream inputStream = getAssets().open(sequenceCharactersFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        if (!isCommentLine(line))
        {
          final String[] sunderedLineArray = Stringy.sunder(line, "\t");
          final String strokeDigitSequence = sunderedLineArray[0];
          final String characters = sunderedLineArray[1];
          charactersFromStrokeDigitSequence.put(strokeDigitSequence, characters);
        }
      }
    }
    catch (IOException exception)
    {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(sequenceCharactersFileName, endMillis - startMillis);
  }
  
  private void loadCharactersIntoCodePointSet(final String charactersFileName, final Set<Integer> codePointSet)
  {
    final long startMillis = System.currentTimeMillis();
    
    try
    {
      final InputStream inputStream = getAssets().open(charactersFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        if (!isCommentLine(line))
        {
          codePointSet.add(Stringy.getFirstCodePoint(line));
        }
      }
    }
    catch (IOException exception)
    {
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
    
    try
    {
      final InputStream inputStream = getAssets().open(rankingFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      int currentRank = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        if (!isCommentLine(line))
        {
          for (final int codePoint : Stringy.toCodePointList(line))
          {
            currentRank++;
            if (!sortingRankFromCodePoint.containsKey(codePoint))
            {
              sortingRankFromCodePoint.put(codePoint, currentRank);
            }
            if (currentRank < LAG_PREVENTION_CODE_POINT_COUNT)
            {
              commonCodePointSet.add(codePoint);
            }
          }
        }
      }
    }
    catch (IOException exception)
    {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(rankingFileName, endMillis - startMillis);
  }
  
  private void loadPhrasesIntoSet(final String phrasesFileName, final Set<String> phraseSet)
  {
    final long startMillis = System.currentTimeMillis();
    
    try
    {
      final InputStream inputStream = getAssets().open(phrasesFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      
      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        if (!isCommentLine(line))
        {
          phraseSet.add(line);
        }
      }
    }
    catch (IOException exception)
    {
      exception.printStackTrace();
    }
    
    final long endMillis = System.currentTimeMillis();
    sendLoadingTimeLog(phrasesFileName, endMillis - startMillis);
  }
  
  private void sendLoadingTimeLog(final String fileName, final long millis)
  {
    if (BuildConfig.DEBUG)
    {
      Log.d(LOG_TAG, String.format("Loaded %s in %d ms", fileName, millis));
    }
  }
  
  @Override
  public void onStartInput(final EditorInfo editorInfo, final boolean isRestarting)
  {
    super.onStartInput(editorInfo, isRestarting);
    
    inputOptionsBits = editorInfo.imeOptions;
    enterKeyHasAction = (inputOptionsBits & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0;
    
    final int inputTypeBits = editorInfo.inputType;
    final int inputClassBits = inputTypeBits & InputType.TYPE_MASK_CLASS;
    final int inputVariationBits = inputTypeBits & InputType.TYPE_MASK_VARIATION;
    
    switch (inputClassBits)
    {
      case InputType.TYPE_CLASS_NUMBER:
        inputIsPassword = inputVariationBits == InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        break;
      
      case InputType.TYPE_CLASS_TEXT:
        switch (inputVariationBits)
        {
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
  public void onStartInputView(final EditorInfo editorInfo, final boolean isRestarting)
  {
    super.onStartInputView(editorInfo, isRestarting);
    
    updateFullscreenMode(); // needed in API level 31+ so that fullscreen works after rotate whilst keyboard showing
    final boolean isFullscreen = isFullscreenMode();
    inputContainer.setPopupRecessLayout(isFullscreen);
    inputContainer.setBackground(isFullscreen);
    
    inputContainer.setStrokeDigitSequence(strokeDigitSequence);
    inputContainer.setCandidateList(candidateList);
    inputContainer.showKeyPreviewPlane(); // for phones that dismiss PopupWindow on switch app
    
    setEnterKeyDisplayText();
  }
  
  private void setEnterKeyDisplayText()
  {
    String enterKeyDisplayText = null;
    switch (inputOptionsBits & EditorInfo.IME_MASK_ACTION)
    {
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
    if (!enterKeyHasAction || enterKeyDisplayText == null)
    {
      enterKeyDisplayText = getString(R.string.display_text__return);
    }
    
    for (final Keyboard keyboard : keyboardSet)
    {
      for (final Key key : keyboard.getKeyList())
      {
        if (key.valueText.equals(ENTER_KEY_VALUE_TEXT))
        {
          key.displayText = enterKeyDisplayText;
        }
      }
    }
    inputContainer.redrawKeyboard();
  }
  
  @Override
  public void onComputeInsets(final Insets insets)
  {
    super.onComputeInsets(insets);
    if (inputContainer != null) // check needed in API level 30
    {
      final int candidatesViewTop = inputContainer.getCandidatesViewTop();
      insets.visibleTopInsets = candidatesViewTop;
      insets.contentTopInsets = candidatesViewTop;
    }
  }
  
  @Override
  public void onCandidate(final String candidate)
  {
    final InputConnection inputConnection = getCurrentInputConnection();
    if (inputConnection == null)
    {
      return;
    }
    
    inputConnection.commitText(candidate, 1);
    setStrokeDigitSequence("");
    setPhraseCompletionCandidateList(inputConnection);
  }
  
  @Override
  public void onKey(final String valueText)
  {
    final InputConnection inputConnection = getCurrentInputConnection();
    if (inputConnection == null)
    {
      return;
    }
    
    switch (valueText)
    {
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
      case SWITCH_TO_STROKES_SYMBOLS_3_VALUE_TEXT:
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
  
  private void effectStrokeAppend(final String strokeDigit)
  {
    final String newStrokeDigitSequence = strokeDigitSequence + strokeDigit;
    final List<String> newCandidateList = computeCandidateList(newStrokeDigitSequence);
    if (newCandidateList.size() > 0)
    {
      setStrokeDigitSequence(newStrokeDigitSequence);
      setCandidateList(newCandidateList);
    }
  }
  
  private void effectBackspace(final InputConnection inputConnection)
  {
    if (strokeDigitSequence.length() > 0)
    {
      final String newStrokeDigitSequence = Stringy.removeSuffixRegex("(?s).", strokeDigitSequence);
      final List<String> newCandidateList = computeCandidateList(newStrokeDigitSequence);
      
      setStrokeDigitSequence(newStrokeDigitSequence);
      setCandidateList(newCandidateList);
      
      if (newStrokeDigitSequence.length() == 0)
      {
        setPhraseCompletionCandidateList(inputConnection);
      }
      
      inputContainer.setKeyRepeatIntervalMilliseconds(BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8);
    }
    else
    {
      final String upToOneCharacterBeforeCursor = getTextBeforeCursor(inputConnection, 1);
      
      if (upToOneCharacterBeforeCursor.length() > 0)
      {
        final CharSequence selection = inputConnection.getSelectedText(0);
        if (TextUtils.isEmpty(selection))
        {
          inputConnection.deleteSurroundingTextInCodePoints(1, 0);
        }
        else
        {
          inputConnection.commitText("", 1);
        }
      }
      else // for apps like Termux
      {
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
      }
      
      setPhraseCompletionCandidateList(inputConnection);
      
      final int nextBackspaceIntervalMilliseconds =
              (Stringy.isAscii(upToOneCharacterBeforeCursor))
                ? BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_ASCII
                : BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8;
      inputContainer.setKeyRepeatIntervalMilliseconds(nextBackspaceIntervalMilliseconds);
    }
  }
  
  private void effectKeyboardSwitch(final String keyboardName)
  {
    final Keyboard keyboard = keyboardFromName.get(keyboardName);
    inputContainer.setKeyboard(keyboard);
  }
  
  private void effectSpaceKey(final InputConnection inputConnection)
  {
    if (strokeDigitSequence.length() > 0)
    {
      onCandidate(getFirstCandidate());
    }
    inputConnection.commitText(" ", 1);
  }
  
  private void effectEnterKey(final InputConnection inputConnection)
  {
    if (strokeDigitSequence.length() > 0)
    {
      onCandidate(getFirstCandidate());
    }
    else if (enterKeyHasAction)
    {
      inputConnection.performEditorAction(inputOptionsBits);
    }
    else
    {
      inputConnection.commitText("\n", 1);
    }
  }
  
  private void effectOrdinaryKey(final InputConnection inputConnection, final String valueText)
  {
    if (strokeDigitSequence.length() > 0)
    {
      onCandidate(getFirstCandidate());
    }
    inputConnection.commitText(valueText, 1);
  }
  
  @Override
  public void onLongPress(final String valueText)
  {
    if (valueText.equals(SPACE_BAR_VALUE_TEXT))
    {
      Contexty.showSystemKeyboardChanger(this);
    }
  }
  
  @Override
  public void onSwipe(final String valueText)
  {
    if (valueText.equals(SPACE_BAR_VALUE_TEXT))
    {
      final Keyboard keyboard = inputContainer.getKeyboard();
      final String keyboardName = nameFromKeyboard.get(keyboard);
      
      if (keyboardName == null)
      {
        return;
      }
      switch (keyboardName)
      {
        case STROKES_KEYBOARD_NAME:
        case STROKES_SYMBOLS_1_KEYBOARD_NAME:
        case STROKES_SYMBOLS_2_KEYBOARD_NAME:
        case STROKES_SYMBOLS_3_KEYBOARD_NAME:
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
  public void saveKeyboard(final Keyboard keyboard)
  {
    final String keyboardName = nameFromKeyboard.get(keyboard);
    Contexty.savePreferenceString(
      getApplicationContext(),
      PREFERENCES_FILE_NAME,
      KEYBOARD_NAME_PREFERENCE_KEY,
      keyboardName
    );
  }
  
  private void setStrokeDigitSequence(final String strokeDigitSequence)
  {
    this.strokeDigitSequence = strokeDigitSequence;
    inputContainer.setStrokeDigitSequence(strokeDigitSequence);
  }
  
  private void setCandidateList(final List<String> candidateList)
  {
    this.candidateList = candidateList;
    inputContainer.setCandidateList(candidateList);
  }
  
  private void setPhraseCompletionCandidateList(final InputConnection inputConnection)
  {
    List<String> phraseCompletionCandidateList = computePhraseCompletionCandidateList(inputConnection);
    
    phraseCompletionFirstCodePointList.clear();
    for (final String phraseCompletionCandidate : phraseCompletionCandidateList)
    {
      phraseCompletionFirstCodePointList.add(Stringy.getFirstCodePoint(phraseCompletionCandidate));
    }
    
    setCandidateList(phraseCompletionCandidateList);
  }
  
  /*
    Candidate comparator for a string.
  */
  private Comparator<String> candidateComparator(
    final Set<Integer> unpreferredCodePointSet,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePointList
  )
  {
    return
      Comparator.comparingInt(
        string ->
          computeCandidateRank(
            string,
            unpreferredCodePointSet,
            sortingRankFromCodePoint,
            phraseCompletionFirstCodePointList
          )
      );
  }
  
  /*
    Candidate comparator for a code point.
  */
  private Comparator<Integer> candidateCodePointComparator(
    final Set<Integer> unpreferredCodePointSet,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePointList
  )
  {
    return
      Comparator.comparingInt(
        codePoint ->
          computeCandidateRank(
            codePoint,
            1,
            unpreferredCodePointSet,
            sortingRankFromCodePoint,
            phraseCompletionFirstCodePointList
          )
      );
  }
  
  /*
    Compute the candidate rank for a string.
  */
  private int computeCandidateRank(
    final String string,
    final Set<Integer> unpreferredCodePointSet,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePointList
  )
  {
    final int firstCodePoint = Stringy.getFirstCodePoint(string);
    final int stringLength = string.length();
    
    return
      computeCandidateRank(
        firstCodePoint,
        stringLength,
        unpreferredCodePointSet,
        sortingRankFromCodePoint,
        phraseCompletionFirstCodePointList
      );
  }
  
  /*
    Compute the candidate rank for a string with a given first code point and length.
  */
  private int computeCandidateRank(
    final int firstCodePoint,
    final int stringLength,
    final Set<Integer> unpreferredCodePointSet,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePointList
  )
  {
    final int coarseRank;
    final int fineRank;
    final int penalty;
    
    final boolean phraseCompletionListIsEmpty = phraseCompletionFirstCodePointList.size() == 0;
    final int phraseCompletionIndex = phraseCompletionFirstCodePointList.indexOf(firstCodePoint);
    final boolean firstCodePointMatchesPhraseCompletionCandidate = phraseCompletionIndex > 0;
    
    final Integer sortingRank = sortingRankFromCodePoint.get(firstCodePoint);
    final int sortingRankNonNull =
            (sortingRank != null)
              ? sortingRank
              : LARGISH_SORTING_RANK;
    
    final int lengthPenalty = (stringLength - 1) * RANKING_PENALTY_PER_CHAR;
    final int unpreferredPenalty =
            (unpreferredCodePointSet.contains(firstCodePoint))
              ? RANKING_PENALTY_UNPREFERRED
              : 0;
    
    if (phraseCompletionListIsEmpty)
    {
      coarseRank = Integer.MIN_VALUE;
      fineRank = sortingRankNonNull;
      penalty = lengthPenalty + unpreferredPenalty;
    }
    else if (firstCodePointMatchesPhraseCompletionCandidate)
    {
      coarseRank = Integer.MIN_VALUE;
      fineRank = phraseCompletionIndex;
      penalty = lengthPenalty;
    }
    else
    {
      coarseRank = 0;
      fineRank = sortingRankNonNull;
      penalty = lengthPenalty + unpreferredPenalty;
    }
    
    return coarseRank + fineRank + penalty;
  }
  
  private List<String> computeCandidateList(final String strokeDigitSequence)
  {
    if (strokeDigitSequence.length() == 0)
    {
      return Collections.emptyList();
    }
    
    updateCandidateOrderPreference();
    
    final List<String> exactMatchCandidateList;
    final String exactMatchCharacters = charactersFromStrokeDigitSequence.get(strokeDigitSequence);
    if (exactMatchCharacters != null)
    {
      exactMatchCandidateList = Stringy.toCharacterList(exactMatchCharacters);
      exactMatchCandidateList.sort(
        candidateComparator(unpreferredCodePointSet, sortingRankFromCodePoint, phraseCompletionFirstCodePointList)
      );
    }
    else
    {
      exactMatchCandidateList = Collections.emptyList();
    }
    
    final Set<Integer> prefixMatchCodePointSet = new HashSet<>();
    final Collection<String> prefixMatchCharactersCollection =
            charactersFromStrokeDigitSequence
              .subMap(
                strokeDigitSequence, false,
                strokeDigitSequence + Character.MAX_VALUE, false
              )
              .values();
    
    final long addStartMillis = System.currentTimeMillis();
    for (final String characters : prefixMatchCharactersCollection)
    {
      Stringy.addCodePointsToSet(characters, prefixMatchCodePointSet);
    }
    final long addEndMillis = System.currentTimeMillis();
    if (BuildConfig.DEBUG)
    {
      Log.d(LOG_TAG, String.format("Added code points to set in %d ms", addEndMillis - addStartMillis));
    }
    
    if (prefixMatchCodePointSet.size() > LAG_PREVENTION_CODE_POINT_COUNT)
    {
      prefixMatchCodePointSet.retainAll(commonCodePointSet);
    }
    
    final List<Integer> prefixMatchCandidateCodePointList = new ArrayList<>(prefixMatchCodePointSet);
    final long sortStartMillis = System.currentTimeMillis();
    prefixMatchCandidateCodePointList.sort(
      candidateCodePointComparator(
        unpreferredCodePointSet,
        sortingRankFromCodePoint,
        phraseCompletionFirstCodePointList
      )
    );
    final long sortEndMillis = System.currentTimeMillis();
    if (BuildConfig.DEBUG)
    {
      Log.d(LOG_TAG, String.format("Sorted prefix matches in %d ms", sortEndMillis - sortStartMillis));
    }
    
    final int prefixMatchCount = Math.min(prefixMatchCandidateCodePointList.size(), MAX_PREFIX_MATCH_COUNT);
    final List<String> prefixMatchCandidateList = new ArrayList<>();
    for (final int prefixMatchCodePoint : prefixMatchCandidateCodePointList.subList(0, prefixMatchCount))
    {
      prefixMatchCandidateList.add(Stringy.toString(prefixMatchCodePoint));
    }
    
    final List<String> candidateList = new ArrayList<>();
    candidateList.addAll(exactMatchCandidateList);
    candidateList.addAll(prefixMatchCandidateList);
    
    return candidateList;
  }
  
  private String getFirstCandidate()
  {
    try
    {
      return candidateList.get(0);
    }
    catch (IndexOutOfBoundsException exception)
    {
      return "";
    }
  }
  
  /*
    Compute the phrase completion candidate list.
    Longer matches with the text before the cursor are ranked earlier.
  */
  private List<String> computePhraseCompletionCandidateList(final InputConnection inputConnection)
  {
    updateCandidateOrderPreference();
    
    final List<String> phraseCompletionCandidateList = new ArrayList<>();
    
    for (
      String phrasePrefix = getTextBeforeCursor(inputConnection, MAX_PHRASE_LENGTH - 1);
      phrasePrefix.length() > 0;
      phrasePrefix = Stringy.removePrefixRegex("(?s).", phrasePrefix)
    )
    {
      final Set<String> prefixMatchPhraseCandidateSet =
              phraseSet.subSet(
                phrasePrefix, false,
                phrasePrefix + Character.MAX_VALUE, false
              );
      final List<String> prefixMatchPhraseCompletionList = new ArrayList<>();
      
      for (final String phraseCandidate : prefixMatchPhraseCandidateSet)
      {
        final String phraseCompletion = Stringy.removePrefix(phrasePrefix, phraseCandidate);
        if (!phraseCompletionCandidateList.contains(phraseCompletion))
        {
          prefixMatchPhraseCompletionList.add(phraseCompletion);
        }
      }
      prefixMatchPhraseCompletionList.sort(
        candidateComparator(unpreferredCodePointSet, sortingRankFromCodePoint, Collections.emptyList())
      );
      phraseCompletionCandidateList.addAll(prefixMatchPhraseCompletionList);
    }
    
    return phraseCompletionCandidateList;
  }
  
  private String getTextBeforeCursor(final InputConnection inputConnection, final int characterCount)
  {
    if (inputIsPassword)
    {
      return ""; // don't read passwords
    }
    
    final String textBeforeCursor = (String) inputConnection.getTextBeforeCursor(characterCount, 0);
    
    if (textBeforeCursor != null)
    {
      return textBeforeCursor;
    }
    else
    {
      return "";
    }
  }
  
  private void updateCandidateOrderPreference()
  {
    if (shouldPreferTraditional())
    {
      unpreferredCodePointSet = codePointSetSimplified;
      sortingRankFromCodePoint = sortingRankFromCodePointTraditional;
      commonCodePointSet = commonCodePointSetTraditional;
      phraseSet = phraseSetTraditional;
    }
    else
    {
      unpreferredCodePointSet = codePointSetTraditional;
      sortingRankFromCodePoint = sortingRankFromCodePointSimplified;
      commonCodePointSet = commonCodePointSetSimplified;
      phraseSet = phraseSetSimplified;
    }
  }
  
  private boolean shouldPreferTraditional()
  {
    final String savedCandidateOrderPreference =
            MainActivity.loadSavedCandidateOrderPreference(getApplicationContext());
    return MainActivity.isTraditionalPreferred(savedCandidateOrderPreference);
  }
}
