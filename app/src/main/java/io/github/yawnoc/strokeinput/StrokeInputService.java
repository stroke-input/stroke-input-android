/*
  Copyright 2021--2025 Conway
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
  private static final int CJK_MAIN_CODE_POINT_START = 0x4E00;
  private static final int CJK_MAIN_CODE_POINT_END = 0x9FFF;
  private static final int CJK_EXTENSION_CODE_POINT_MIN = 0x3400;
  private static final int CJK_EXTENSION_CODE_POINT_MAX = 0x2CEAF;
  private static final int RANKING_PENALTY_CJK_EXTENSION = CJK_MAIN_CODE_POINT_END - CJK_EXTENSION_CODE_POINT_MIN + 1;
  private static final int RANKING_PENALTY_PER_CHAR = 2 * CJK_EXTENSION_CODE_POINT_MAX;
  private static final int RANKING_PENALTY_UNPREFERRED = 10 * CJK_EXTENSION_CODE_POINT_MAX;
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
  private Set<Keyboard> keyboards;

  private InputContainer inputContainer;

  private final NavigableMap<String, String> charactersFromStrokeDigitSequence = new TreeMap<>();
  private final Set<Integer> codePointsTraditional = new HashSet<>();
  private final Set<Integer> codePointsSimplified = new HashSet<>();
  private final Map<Integer, Integer> sortingRankFromCodePointTraditional = new HashMap<>();
  private final Map<Integer, Integer> sortingRankFromCodePointSimplified = new HashMap<>();
  private final Set<Integer> commonCodePointsTraditional = new HashSet<>();
  private final Set<Integer> commonCodePointsSimplified = new HashSet<>();
  private final NavigableSet<String> phrasesTraditional = new TreeSet<>();
  private final NavigableSet<String> phrasesSimplified = new TreeSet<>();

  private Set<Integer> unpreferredCodePoints;
  private Map<Integer, Integer> sortingRankFromCodePoint;
  private Set<Integer> commonCodePoints;
  private NavigableSet<String> phrases;

  private String strokeDigitSequence = "";
  private List<String> candidates = new ArrayList<>();
  private final List<Integer> phraseCompletionFirstCodePoints = new ArrayList<>();

  private int inputActionsBits;
  private boolean enterKeyHasAction;
  private boolean inputIsPassword;

  @Override
  public void onCreate()
  {
    super.onCreate();

    loadSequenceCharactersData(SEQUENCE_CHARACTERS_FILE_NAME, charactersFromStrokeDigitSequence);
    loadCharactersData(CHARACTERS_FILE_NAME_TRADITIONAL, codePointsTraditional);
    loadCharactersData(CHARACTERS_FILE_NAME_SIMPLIFIED, codePointsSimplified);
    loadRankingData(RANKING_FILE_NAME_TRADITIONAL, sortingRankFromCodePointTraditional, commonCodePointsTraditional);
    loadRankingData(RANKING_FILE_NAME_SIMPLIFIED, sortingRankFromCodePointSimplified, commonCodePointsSimplified);
    loadPhrasesData(PHRASES_FILE_NAME_TRADITIONAL, phrasesTraditional);
    loadPhrasesData(PHRASES_FILE_NAME_SIMPLIFIED, phrasesSimplified);

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
    keyboards = nameFromKeyboard.keySet();

    inputContainer = (InputContainer) getLayoutInflater().inflate(R.layout.input_container, null);
    inputContainer.initialisePopupRecess();
    inputContainer.initialiseStrokeSequenceBar(this);
    inputContainer.initialiseCandidatesView(this);
    inputContainer.initialiseKeyboardView(this, loadSavedKeyboard());
    inputContainer.initialiseBottomSpacer();

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
    return line.startsWith("#") || line.isEmpty();
  }

  @SuppressWarnings("SameParameterValue")
  private void loadSequenceCharactersData(
    final String sequenceCharactersFileName,
    final Map<String, String> charactersFromStrokeDigitSequence
  )
  {
    final long startMilliseconds = System.currentTimeMillis();

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
      Log.e(LOG_TAG, "loadSequenceCharactersData failed", exception);
    }

    final long endMilliseconds = System.currentTimeMillis();
    sendLoadingTimeLog(sequenceCharactersFileName, startMilliseconds, endMilliseconds);
  }

  private void loadCharactersData(final String charactersFileName, final Set<Integer> codePoints)
  {
    final long startMilliseconds = System.currentTimeMillis();

    try
    {
      final InputStream inputStream = getAssets().open(charactersFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        if (!isCommentLine(line))
        {
          codePoints.add(Stringy.getFirstCodePoint(line));
        }
      }
    }
    catch (IOException exception)
    {
      Log.e(LOG_TAG, "loadCharactersData failed", exception);
    }

    final long endMilliseconds = System.currentTimeMillis();
    sendLoadingTimeLog(charactersFileName, startMilliseconds, endMilliseconds);
  }

  private void loadRankingData(
    final String rankingFileName,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final Set<Integer> commonCodePoints
  )
  {
    final long startMilliseconds = System.currentTimeMillis();

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
              commonCodePoints.add(codePoint);
            }
          }
        }
      }
    }
    catch (IOException exception)
    {
      Log.e(LOG_TAG, "loadRankingData failed", exception);
    }

    final long endMilliseconds = System.currentTimeMillis();
    sendLoadingTimeLog(rankingFileName, startMilliseconds, endMilliseconds);
  }

  private void loadPhrasesData(final String phrasesFileName, final Set<String> phrases)
  {
    final long startMilliseconds = System.currentTimeMillis();

    try
    {
      final InputStream inputStream = getAssets().open(phrasesFileName);
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        if (!isCommentLine(line))
        {
          phrases.add(line);
        }
      }
    }
    catch (IOException exception)
    {
      Log.e(LOG_TAG, "loadPhrasesData failed", exception);
    }

    final long endMilliseconds = System.currentTimeMillis();
    sendLoadingTimeLog(phrasesFileName, startMilliseconds, endMilliseconds);
  }

  private void sendLoadingTimeLog(final String fileName, final long startMilliseconds, final long endMilliseconds)
  {
    if (BuildConfig.DEBUG)
    {
      final long durationMilliseconds = endMilliseconds - startMilliseconds;
      Log.d(LOG_TAG, String.format("Loaded %s in %d ms", fileName, durationMilliseconds));
    }
  }

  @Override
  public void onStartInput(final EditorInfo editorInfo, final boolean isRestarting)
  {
    super.onStartInput(editorInfo, isRestarting);

    final int inputOptionsBits = editorInfo.imeOptions;
    inputActionsBits = inputOptionsBits & EditorInfo.IME_MASK_ACTION;
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

    inputContainer.post( // await layout so that width is available
      () ->
      {
        final int inputContainerWidth = inputContainer.getWidth();
        for (final Keyboard keyboard : keyboards)
        {
          keyboard.correctKeyboardWidth(inputContainerWidth); // needed in API level 35+ due to edge-to-edge breakage
        }
        inputContainer.redrawKeyboard(); // key preview plane initialisation is buggy in landscape mode
      }
    );

    for (final Keyboard keyboard : keyboards)
    {
      keyboard.adjustKeyboardHeight();
    }
    inputContainer.updateHeight();

    updateFullscreenMode(); // needed in API level 31+ so that fullscreen works after rotate whilst keyboard showing
    final boolean isFullscreen = isFullscreenMode();
    inputContainer.setPopupRecessLayout(isFullscreen);
    inputContainer.setBackground(isFullscreen);

    inputContainer.setStrokeDigitSequence(strokeDigitSequence);
    inputContainer.setCandidates(candidates);

    setEnterKeyDisplayText();
  }

  private void setEnterKeyDisplayText()
  {
    String enterKeyDisplayText = null;
    switch (inputActionsBits)
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

    for (final Keyboard keyboard : keyboards)
    {
      for (final Key key : keyboard.getKeys())
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
  public boolean onEvaluateInputViewShown()
  {
    super.onEvaluateInputViewShown();
    return true; // override needed in API level 36
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
    setPhraseCompletionCandidates(inputConnection);
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
    final List<String> newCandidates = computeCandidates(newStrokeDigitSequence);
    if (!newCandidates.isEmpty())
    {
      setStrokeDigitSequence(newStrokeDigitSequence);
      setCandidates(newCandidates);
    }
  }

  private void effectBackspace(final InputConnection inputConnection)
  {
    if (!strokeDigitSequence.isEmpty())
    {
      final String newStrokeDigitSequence = Stringy.removeSuffixRegex("(?s).", strokeDigitSequence);
      final List<String> newCandidates = computeCandidates(newStrokeDigitSequence);

      setStrokeDigitSequence(newStrokeDigitSequence);
      setCandidates(newCandidates);

      if (newStrokeDigitSequence.isEmpty())
      {
        setPhraseCompletionCandidates(inputConnection);
      }

      inputContainer.setKeyRepeatIntervalMilliseconds(BACKSPACE_REPEAT_INTERVAL_MILLISECONDS_UTF_8);
    }
    else
    {
      final String upToOneCharacterBeforeCursor = getTextBeforeCursor(inputConnection, 1);

      if (!upToOneCharacterBeforeCursor.isEmpty())
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

      setPhraseCompletionCandidates(inputConnection);

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
    if (!strokeDigitSequence.isEmpty())
    {
      onCandidate(getFirstCandidate());
    }
    else
    {
      inputConnection.commitText(" ", 1);
    }
  }

  private void effectEnterKey(final InputConnection inputConnection)
  {
    if (!strokeDigitSequence.isEmpty())
    {
      onCandidate(getFirstCandidate());
    }
    else if (enterKeyHasAction)
    {
      inputConnection.performEditorAction(inputActionsBits);
    }
    else
    {
      inputConnection.commitText("\n", 1);
    }
  }

  private void effectOrdinaryKey(final InputConnection inputConnection, final String valueText)
  {
    if (!strokeDigitSequence.isEmpty())
    {
      return;
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

  private void setCandidates(final List<String> candidates)
  {
    this.candidates = candidates;
    inputContainer.setCandidates(candidates);
  }

  private void setPhraseCompletionCandidates(final InputConnection inputConnection)
  {
    List<String> phraseCompletionCandidates = computePhraseCompletionCandidates(inputConnection);

    phraseCompletionFirstCodePoints.clear();
    for (final String phraseCompletionCandidate : phraseCompletionCandidates)
    {
      phraseCompletionFirstCodePoints.add(Stringy.getFirstCodePoint(phraseCompletionCandidate));
    }

    setCandidates(phraseCompletionCandidates);
  }

  /*
    Candidate comparator for a string.
  */
  private Comparator<String> candidateComparator(
    final Set<Integer> unpreferredCodePoints,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePoints
  )
  {
    return
      Comparator.comparingInt(
        string ->
          computeCandidateRank(
            string,
            unpreferredCodePoints,
            sortingRankFromCodePoint,
            phraseCompletionFirstCodePoints
          )
      );
  }

  /*
    Candidate comparator for a code point.
  */
  private Comparator<Integer> candidateCodePointComparator(
    final Set<Integer> unpreferredCodePoints,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePoints
  )
  {
    return
      Comparator.comparingInt(
        codePoint ->
          computeCandidateRank(
            codePoint,
            1,
            unpreferredCodePoints,
            sortingRankFromCodePoint,
            phraseCompletionFirstCodePoints
          )
      );
  }

  /*
    Compute the candidate rank for a string.
  */
  private int computeCandidateRank(
    final String string,
    final Set<Integer> unpreferredCodePoints,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePoints
  )
  {
    final int firstCodePoint = Stringy.getFirstCodePoint(string);
    final int stringLength = string.length();

    return
      computeCandidateRank(
        firstCodePoint,
        stringLength,
        unpreferredCodePoints,
        sortingRankFromCodePoint,
        phraseCompletionFirstCodePoints
      );
  }

  /*
    Compute the candidate rank for a string with a given first code point and length.
  */
  private int computeCandidateRank(
    final int firstCodePoint,
    final int stringLength,
    final Set<Integer> unpreferredCodePoints,
    final Map<Integer, Integer> sortingRankFromCodePoint,
    final List<Integer> phraseCompletionFirstCodePoints
  )
  {
    final int coarseRank;
    final int fineRank;
    final int penalty;

    final boolean phraseCompletionsIsEmpty = phraseCompletionFirstCodePoints.isEmpty();
    final int phraseCompletionIndex = phraseCompletionFirstCodePoints.indexOf(firstCodePoint);
    final boolean firstCodePointMatchesPhraseCompletionCandidate = phraseCompletionIndex > 0;

    final Integer sortingRank = sortingRankFromCodePoint.get(firstCodePoint);
    final int cjkBlockPenalty =
            (firstCodePoint < CJK_MAIN_CODE_POINT_START || firstCodePoint > CJK_MAIN_CODE_POINT_END)
              ? RANKING_PENALTY_CJK_EXTENSION
              : 0;
    final int sortingRankNonNull =
            (sortingRank != null)
              ? sortingRank
              : firstCodePoint + cjkBlockPenalty;

    final int lengthPenalty = (stringLength - 1) * RANKING_PENALTY_PER_CHAR;
    final int unpreferredPenalty =
            (unpreferredCodePoints.contains(firstCodePoint))
              ? RANKING_PENALTY_UNPREFERRED
              : 0;

    if (phraseCompletionsIsEmpty)
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

  private List<String> computeCandidates(final String strokeDigitSequence)
  {
    if (strokeDigitSequence.isEmpty())
    {
      return Collections.emptyList();
    }

    updateCandidateOrderPreference();

    final Set<Integer> exactMatchCodePoints;
    final List<String> exactMatchCandidates;
    final String exactMatchCharacters = charactersFromStrokeDigitSequence.get(strokeDigitSequence);
    if (exactMatchCharacters != null)
    {
      exactMatchCodePoints = Stringy.toCodePointSet(exactMatchCharacters);
      exactMatchCandidates = Stringy.toCharacterList(exactMatchCharacters);
      exactMatchCandidates.sort(
        candidateComparator(unpreferredCodePoints, sortingRankFromCodePoint, phraseCompletionFirstCodePoints)
      );
    }
    else
    {
      exactMatchCodePoints = Collections.emptySet();
      exactMatchCandidates = Collections.emptyList();
    }

    final Collection<String> prefixMatchCharactersCollection =
            charactersFromStrokeDigitSequence
              .subMap(
                strokeDigitSequence, false,
                strokeDigitSequence + Character.MAX_VALUE, false
              )
              .values();

    final Set<Integer> prefixMatchCodePoints = Stringy.toCodePointSet(prefixMatchCharactersCollection);

    prefixMatchCodePoints.removeAll(exactMatchCodePoints);
    if (prefixMatchCodePoints.size() > LAG_PREVENTION_CODE_POINT_COUNT)
    {
      prefixMatchCodePoints.retainAll(commonCodePoints);
    }

    final List<Integer> prefixMatchCandidateCodePoints = new ArrayList<>(prefixMatchCodePoints);
    prefixMatchCandidateCodePoints.sort(
      candidateCodePointComparator(unpreferredCodePoints, sortingRankFromCodePoint, phraseCompletionFirstCodePoints)
    );

    final int prefixMatchCount = Math.min(prefixMatchCandidateCodePoints.size(), MAX_PREFIX_MATCH_COUNT);
    final List<String> prefixMatchCandidates = new ArrayList<>();
    for (final int prefixMatchCodePoint : prefixMatchCandidateCodePoints.subList(0, prefixMatchCount))
    {
      prefixMatchCandidates.add(Stringy.toString(prefixMatchCodePoint));
    }

    final List<String> candidates = new ArrayList<>();
    candidates.addAll(exactMatchCandidates);
    candidates.addAll(prefixMatchCandidates);

    return candidates;
  }

  private String getFirstCandidate()
  {
    try
    {
      return candidates.get(0);
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
  private List<String> computePhraseCompletionCandidates(final InputConnection inputConnection)
  {
    updateCandidateOrderPreference();

    final List<String> phraseCompletionCandidates = new ArrayList<>();

    for (
      String phrasePrefix = getTextBeforeCursor(inputConnection, MAX_PHRASE_LENGTH - 1);
      !phrasePrefix.isEmpty();
      phrasePrefix = Stringy.removePrefixRegex("(?s).", phrasePrefix)
    )
    {
      final Set<String> prefixMatchPhraseCandidates =
              phrases.subSet(
                phrasePrefix, false,
                phrasePrefix + Character.MAX_VALUE, false
              );
      final List<String> prefixMatchPhraseCompletions = new ArrayList<>();

      for (final String phraseCandidate : prefixMatchPhraseCandidates)
      {
        final String phraseCompletion = Stringy.removePrefix(phrasePrefix, phraseCandidate);
        if (!phraseCompletionCandidates.contains(phraseCompletion))
        {
          prefixMatchPhraseCompletions.add(phraseCompletion);
        }
      }
      prefixMatchPhraseCompletions.sort(
        candidateComparator(unpreferredCodePoints, sortingRankFromCodePoint, Collections.emptyList())
      );
      phraseCompletionCandidates.addAll(prefixMatchPhraseCompletions);
    }

    return phraseCompletionCandidates;
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
      unpreferredCodePoints = codePointsSimplified;
      sortingRankFromCodePoint = sortingRankFromCodePointTraditional;
      commonCodePoints = commonCodePointsTraditional;
      phrases = phrasesTraditional;
    }
    else
    {
      unpreferredCodePoints = codePointsTraditional;
      sortingRankFromCodePoint = sortingRankFromCodePointSimplified;
      commonCodePoints = commonCodePointsSimplified;
      phrases = phrasesSimplified;
    }
  }

  private boolean shouldPreferTraditional()
  {
    final String savedCandidateOrderPreference =
            MainActivity.loadSavedCandidateOrderPreference(getApplicationContext());
    return MainActivity.isTraditionalPreferred(savedCandidateOrderPreference);
  }
}
