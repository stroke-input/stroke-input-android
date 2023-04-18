/*
  Copyright 2021--2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

import io.github.yawnoc.utilities.Contexty;

/*
  The main activity of the application.
*/
public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  public static final String CANDIDATE_ORDER_PREFERENCE_KEY = "candidateOrderPreference";
  public static final String CANDIDATE_ORDER_PREFER_TRADITIONAL_FIRST = "TRADITIONAL_FIRST";
  public static final String CANDIDATE_ORDER_PREFER_SIMPLIFIED_FIRST = "SIMPLIFIED_FIRST";
  
  public static final String KEYBOARD_HEIGHT_ADJUSTMENT_PROGRESS_KEY = "keyboardHeightAdjustmentProgress";
  public static final int KEYBOARD_HEIGHT_ADJUSTMENT_DEFAULT_PROGRESS = 10;
  public static final int KEYBOARD_HEIGHT_ADJUSTMENT_MAX_PROGRESS = 20;
  public static final float KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_MIN = 0.5f;
  public static final float KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_MAX = 1.5f;
  public static final float KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_RANGE =
          KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_MAX - KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_MIN;
  
  private static final String ASSETS_DIRECTORY = "file:///android_asset/";
  private static final String SOURCE_CODE_URI = "https://github.com/stroke-input/stroke-input-android";
  private static final String PRIVACY_POLICY_URI =
          "https://github.com/stroke-input/stroke-input-android/blob/master/PRIVACY.md#privacy-policy";
  
  AlertDialog.Builder candidateOrderDialogBuilder;
  Dialog candidateOrderDialog;
  AlertDialog.Builder htmlWebViewContainer;
  WebView htmlWebView;
  
  @Override
  protected void onCreate(final Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setTitle(R.string.label__main_activity__welcome);
    setContentView(R.layout.main_activity);
    
    findViewById(R.id.source_code_button).setOnClickListener(this);
    findViewById(R.id.help_button).setOnClickListener(this);
    findViewById(R.id.about_button).setOnClickListener(this);
    findViewById(R.id.privacy_button).setOnClickListener(this);
    findViewById(R.id.input_method_settings_button).setOnClickListener(this);
    findViewById(R.id.change_keyboard_button).setOnClickListener(this);
    findViewById(R.id.candidate_order_button).setOnClickListener(this);
    
    final SeekBar keyboardHeightAdjustmentSeekBar = findViewById(R.id.keyboard_height_adjustment_seek_bar);
    keyboardHeightAdjustmentSeekBar.setOnSeekBarChangeListener(
      new SeekBar.OnSeekBarChangeListener()
      {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean isUserChange)
        {
          saveKeyboardHeightAdjustmentProgress(progress);
          final float adjustmentFactor = keyboardHeightAdjustmentProgressToFactor(progress);
          setKeyboardHeightAdjustmentDisplayText(adjustmentFactor);
        }
        
        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
          final View focusView = getCurrentFocus();
          if (focusView != null)
          {
            final InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            inputMethodManager.showSoftInput(findViewById(R.id.test_input), InputMethodManager.SHOW_IMPLICIT);
          }
        }
      }
    );
    
    setCandidateOrderButtonText(loadSavedCandidateOrderPreference());
    
    final int adjustmentProgress = loadSavedKeyboardHeightAdjustmentProgress(getApplicationContext());
    final float adjustmentFactor = keyboardHeightAdjustmentProgressToFactor(adjustmentProgress);
    keyboardHeightAdjustmentSeekBar.setMax(KEYBOARD_HEIGHT_ADJUSTMENT_MAX_PROGRESS);
    keyboardHeightAdjustmentSeekBar.setProgress(adjustmentProgress);
    setKeyboardHeightAdjustmentDisplayText(adjustmentFactor);
    
    findViewById(R.id.test_input).requestFocus();
  }
  
  public static boolean isTraditionalPreferred(final String candidateOrderPreference)
  {
    if (candidateOrderPreference == null)
    {
      return true;
    }
    
    return !candidateOrderPreference.equals(CANDIDATE_ORDER_PREFER_SIMPLIFIED_FIRST);
  }
  
  private String loadSavedCandidateOrderPreference()
  {
    return loadSavedCandidateOrderPreference(getApplicationContext());
  }
  
  public static String loadSavedCandidateOrderPreference(final Context context)
  {
    return
      Contexty.loadPreferenceString(context, StrokeInputService.PREFERENCES_FILE_NAME, CANDIDATE_ORDER_PREFERENCE_KEY);
  }
  
  private void saveCandidateOrderPreference(final String candidateOrderPreference)
  {
    Contexty.savePreferenceString(
      getApplicationContext(),
      StrokeInputService.PREFERENCES_FILE_NAME,
      CANDIDATE_ORDER_PREFERENCE_KEY,
      candidateOrderPreference
    );
  }
  
  private void setCandidateOrderButtonText(final String candidateOrderPreference)
  {
    final TextView candidateOrderButton = findViewById(R.id.candidate_order_button);
    final String candidateOrderButtonText =
            (isTraditionalPreferred(candidateOrderPreference))
              ? getString(R.string.label__main_activity__traditional_first)
              : getString(R.string.label__main_activity__simplified_first);
    candidateOrderButton.setText(candidateOrderButtonText);
  }
  
  public static int loadSavedKeyboardHeightAdjustmentProgress(final Context context)
  {
    return
      Contexty.loadPreferenceInt(
        context,
        StrokeInputService.PREFERENCES_FILE_NAME,
        KEYBOARD_HEIGHT_ADJUSTMENT_PROGRESS_KEY,
        KEYBOARD_HEIGHT_ADJUSTMENT_DEFAULT_PROGRESS
      );
  }
  
  private void saveKeyboardHeightAdjustmentProgress(final int keyboardHeightAdjustmentProgress)
  {
    Contexty.savePreferenceInt(
      getApplicationContext(),
        StrokeInputService.PREFERENCES_FILE_NAME,
        KEYBOARD_HEIGHT_ADJUSTMENT_PROGRESS_KEY,
        keyboardHeightAdjustmentProgress
    );
  }
  
  public static float keyboardHeightAdjustmentProgressToFactor(final int progress)
  {
    final float progressFraction = ((float) progress) / KEYBOARD_HEIGHT_ADJUSTMENT_MAX_PROGRESS;
    return KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_MIN + progressFraction * KEYBOARD_HEIGHT_ADJUSTMENT_FACTOR_RANGE;
  }
  
  private void setKeyboardHeightAdjustmentDisplayText(final float adjustmentFactor)
  {
    final TextView keyboardHeightAdjustmentDisplay = findViewById(R.id.keyboard_height_adjustment_display);
    final String adjustmentDisplayText = "×\u00A0" + NumberFormat.getInstance().format(adjustmentFactor);
    keyboardHeightAdjustmentDisplay.setText(adjustmentDisplayText);
  }
  
  @Override
  public void onClick(final View view)
  {
    final int viewId = view.getId();
    if (viewId == R.id.source_code_button)
    {
      Contexty.openInBrowser(this, SOURCE_CODE_URI);
    }
    else if (viewId == R.id.help_button)
    {
      showHtmlWebView(R.string.file_name__help_html);
    }
    else if (viewId == R.id.about_button)
    {
      showHtmlWebView(R.string.file_name__about_html);
    }
    else if (viewId == R.id.privacy_button)
    {
      Contexty.openInBrowser(this, PRIVACY_POLICY_URI);
    }
    else if (viewId == R.id.input_method_settings_button)
    {
      Contexty.showSystemInputMethodSettings(this);
    }
    else if (viewId == R.id.change_keyboard_button)
    {
      Contexty.showSystemKeyboardChanger(this);
    }
    else if (viewId == R.id.candidate_order_button)
    {
      showCandidateOrderDialog();
    }
    else if (viewId == R.id.traditional_first_button)
    {
      saveCandidateOrderPreference(CANDIDATE_ORDER_PREFER_TRADITIONAL_FIRST);
      setCandidateOrderButtonText(CANDIDATE_ORDER_PREFER_TRADITIONAL_FIRST);
      candidateOrderDialog.dismiss();
    }
    else if (viewId == R.id.simplified_first_button)
    {
      saveCandidateOrderPreference(CANDIDATE_ORDER_PREFER_SIMPLIFIED_FIRST);
      setCandidateOrderButtonText(CANDIDATE_ORDER_PREFER_SIMPLIFIED_FIRST);
      candidateOrderDialog.dismiss();
    }
  }
  
  private void showHtmlWebView(final String uri)
  {
    if (htmlWebViewContainer == null)
    {
      htmlWebViewContainer = new AlertDialog.Builder(this, R.style.StrokeInputAlert);
      htmlWebViewContainer.setPositiveButton(R.string.label__main_activity__return, null);
    }
    
    try
    {
      if (htmlWebView == null)
      {
        htmlWebView = new WebView(this);
        htmlWebView.setBackgroundColor(Color.TRANSPARENT);
        htmlWebView.getSettings().setBuiltInZoomControls(true);
        htmlWebView.getSettings().setDisplayZoomControls(false);
      }
      
      htmlWebView.loadUrl(uri);
      htmlWebViewContainer
        .setView(htmlWebView)
        .setOnDismissListener(dialog -> ((ViewGroup) htmlWebView.getParent()).removeView(htmlWebView))
        .show();
    }
    catch (Exception exception)
    {
      Contexty.showErrorMessage(this, getString(R.string.message__error__webview));
    }
  }
  
  private void showHtmlWebView(final int fileNameResourceId)
  {
    showHtmlWebView(ASSETS_DIRECTORY + getString(fileNameResourceId));
  }
  
  private void showCandidateOrderDialog()
  {
    candidateOrderDialogBuilder = new AlertDialog.Builder(this, R.style.StrokeInputAlert);
    candidateOrderDialogBuilder
      .setTitle(R.string.label__main_activity__candidate_order)
      .setView(R.layout.candidate_order_dialog)
      .setCancelable(true);
    
    candidateOrderDialog = candidateOrderDialogBuilder.create();
    final int dialog_size = ViewGroup.LayoutParams.WRAP_CONTENT;
    candidateOrderDialog.show();
    candidateOrderDialog.getWindow().setLayout(dialog_size, dialog_size);
    
    final RadioGroup candidateOrderRadioGroup = candidateOrderDialog.findViewById(R.id.candidate_order_radio_group);
    final Button traditionalFirstButton = candidateOrderDialog.findViewById(R.id.traditional_first_button);
    final Button simplifiedFirstButton = candidateOrderDialog.findViewById(R.id.simplified_first_button);
    
    final boolean traditionalIsPreferred = isTraditionalPreferred(loadSavedCandidateOrderPreference());
    final int savedCandidateOrderButtonId =
            (traditionalIsPreferred)
              ? R.id.traditional_first_button
              : R.id.simplified_first_button;
    candidateOrderRadioGroup.check(savedCandidateOrderButtonId);
    
    traditionalFirstButton.setOnClickListener(this);
    simplifiedFirstButton.setOnClickListener(this);
  }
}
