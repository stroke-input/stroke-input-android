/*
  Copyright 2021--2025 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import androidx.core.view.WindowInsetsCompat;

import java.util.List;

/*
  A container that holds:
    1. Main input block:
       (a) Main input plane:
           - Popup recess
           - Stroke sequence bar
           - Candidates view
           - Keyboard view
       (b) Key preview plane (overlaid)
    2. Bottom spacer (needed in API level 35+ for edge-to-edge bottom padding)
*/
public class InputContainer
  extends LinearLayout
{
  // Container properties
  private View popupRecess;
  private StrokeSequenceBar strokeSequenceBar;
  private CandidatesView candidatesView;
  private CandidatesViewAdapter candidatesViewAdapter;
  private KeyboardView keyboardView;
  private View bottomSpacer;

  @Override
  public WindowInsets onApplyWindowInsets(WindowInsets insets)
  {
    super.onApplyWindowInsets(insets);

    if (Build.VERSION.SDK_INT >= 35) // needed in API level 35+ for edge-to-edge bottom padding
    {
      final Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      ViewGroup.LayoutParams layoutParameters = bottomSpacer.getLayoutParams();
      layoutParameters.height = systemBars.bottom;
      bottomSpacer.setLayoutParams(layoutParameters);
    }

    return insets;
  }

  public InputContainer(final Context context, final AttributeSet attributes)
  {
    super(context, attributes);
  }

  public void initialisePopupRecess()
  {
    popupRecess = findViewById(R.id.popup_recess);
  }

  public void initialiseStrokeSequenceBar(final Context context)
  {
    strokeSequenceBar = findViewById(R.id.stroke_sequence_bar);
    strokeSequenceBar.setTypeface(Typeface.createFromAsset(context.getAssets(), KeyboardView.KEYBOARD_FONT_FILE_NAME));
  }

  public void initialiseCandidatesView(final CandidatesViewAdapter.CandidateListener candidateListener)
  {
    candidatesView = findViewById(R.id.candidates_view);
    candidatesView.setCandidateListener(candidateListener);
    candidatesViewAdapter = candidatesView.getCandidatesViewAdapter();
  }

  public void initialiseKeyboardView(
    final KeyboardView.KeyboardListener keyboardListener,
    final Keyboard keyboard
  )
  {
    keyboardView = findViewById(R.id.keyboard_view);
    keyboardView.setKeyboardListener(keyboardListener);
    keyboardView.setMainInputPlane(findViewById(R.id.main_input_plane));
    keyboardView.setKeyPreviewPlane(findViewById(R.id.key_preview_plane));
    keyboardView.setKeyboard(keyboard);
  }

  public void initialiseBottomSpacer()
  {
    bottomSpacer = findViewById(R.id.bottom_spacer);
  }

  public void setPopupRecessLayout(final boolean isFullscreen)
  {
    if (isFullscreen)
    {
      popupRecess.setVisibility(GONE);
    }
    else
    {
      popupRecess.setVisibility(INVISIBLE);
    }
  }

  public void setBackground(final boolean isFullscreen)
  {
    final int backgroundResourceId =
            (isFullscreen)
              ? R.color.stroke_sequence_bar_fill_fullscreen
              : 0; // none
    setBackgroundResource(backgroundResourceId);
  }

  public void updateHeight()
  {
    keyboardView.requestLayout();
    getViewTreeObserver().addOnPreDrawListener( // otherwise downsized keyboard won't fall to the bottom
      new ViewTreeObserver.OnPreDrawListener()
      {
        @Override
        public boolean onPreDraw()
        {
          getViewTreeObserver().removeOnPreDrawListener(this);
          requestLayout();
          return false;
        }
      }
    );
  }

  public void setStrokeDigitSequence(final String strokeDigitSequence)
  {
    strokeSequenceBar.setStrokeDigitSequence(strokeDigitSequence);
  }

  public void setCandidates(final List<String> candidates)
  {
    candidatesViewAdapter.updateCandidates(candidates);
    candidatesView.scrollToPosition(0);
  }

  public int getCandidatesViewTop()
  {
    return candidatesView.getTop();
  }

  public Keyboard getKeyboard()
  {
    return keyboardView.getKeyboard();
  }

  public void setKeyboard(final Keyboard keyboard)
  {
    keyboardView.setKeyboard(keyboard);
  }

  public void setKeyRepeatIntervalMilliseconds(final int milliseconds)
  {
    keyboardView.setKeyRepeatIntervalMilliseconds(milliseconds);
  }

  public void redrawKeyboard()
  {
    keyboardView.requestLayout();
    keyboardView.invalidate();
  }
}
