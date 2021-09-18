/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.github.yawnoc.utilities.Contexty;

/*
  The main activity of the application.
*/
public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  
  private static final String ASSETS_DIRECTORY = "file:///android_asset/";
  private static final String SOURCE_CODE_URI = "https://github.com/stroke-input/stroke-input-android";
  
  AlertDialog.Builder candidateOrderDialogBuilder;
  Dialog candidateOrderDialog;
  AlertDialog.Builder htmlWebViewContainer;
  WebView htmlWebView;
  
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    
    findViewById(R.id.source_code_button).setOnClickListener(this);
    findViewById(R.id.help_button).setOnClickListener(this);
    findViewById(R.id.about_button).setOnClickListener(this);
    findViewById(R.id.input_method_settings_button).setOnClickListener(this);
    findViewById(R.id.switch_keyboard_button).setOnClickListener(this);
    findViewById(R.id.candidate_order_button).setOnClickListener(this);
    
    setCandidateOrderButtonText(loadSavedCandidateOrderPreference());
    
  }
  
  private boolean isTraditionalPreferred(final String candidateOrderPreference) {
    
    if (candidateOrderPreference == null) {
      return true;
    }
    
    return !candidateOrderPreference.equals("PREFER_SIMPLIFIED");
    
  }
  
  private String loadSavedCandidateOrderPreference() {
    return
      Contexty.loadPreferenceString(
        getApplicationContext(),
        StrokeInputService.PREFERENCES_FILE_NAME,
        "candidateOrderPreference"
      );
  }
  
  private void saveCandidateOrderPreference(final String candidateOrderPreference) {
    Contexty.savePreferenceString(
      getApplicationContext(),
      StrokeInputService.PREFERENCES_FILE_NAME,
      "candidateOrderPreference",
      candidateOrderPreference
    );
  }
  
  private void setCandidateOrderButtonText(final String candidateOrderPreference) {
    
    final TextView candidateOrderButton = findViewById(R.id.candidate_order_button);
    final String candidateOrderButtonText = (
      isTraditionalPreferred(candidateOrderPreference)
        ? getString(R.string.label__main_activity__traditional_characters_first)
        : getString(R.string.label__main_activity__simplified_characters_first)
    );
    
    candidateOrderButton.setText(candidateOrderButtonText);
    
  }
  
  @Override
  public void onClick(final View view) {
    
    final int viewId = view.getId();
    
    if (viewId == R.id.source_code_button) {
      Contexty.openInBrowser(this, SOURCE_CODE_URI);
    }
    else if (viewId == R.id.help_button) {
      showHtmlWebView(R.string.file_name__help_html);
    }
    else if (viewId == R.id.about_button) {
      showHtmlWebView(R.string.file_name__about_html);
    }
    else if (viewId == R.id.input_method_settings_button) {
      Contexty.showSystemInputMethodSettings(this);
    }
    else if (viewId == R.id.switch_keyboard_button) {
      Contexty.showSystemKeyboardSwitcher(this);
    }
    else if (viewId == R.id.candidate_order_button) {
      showCandidateOrderDialog();
    }
    else if (viewId == R.id.prefer_traditional_button) {
      saveCandidateOrderPreference("PREFER_TRADITIONAL");
      setCandidateOrderButtonText("PREFER_TRADITIONAL");
    }
    else if (viewId == R.id.prefer_simplified_button) {
      saveCandidateOrderPreference("PREFER_SIMPLIFIED");
      setCandidateOrderButtonText("PREFER_SIMPLIFIED");
    }
    
  }
  
  private void showHtmlWebView(final String uri) {
    
    if (htmlWebViewContainer == null) {
      htmlWebViewContainer = new AlertDialog.Builder(this, R.style.StrokeInputAlert);
      htmlWebViewContainer.setPositiveButton(R.string.label__main_activity__return, null);
    }
    
    if (htmlWebView == null) {
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
  
  private void showHtmlWebView(final int fileNameResourceId) {
    showHtmlWebView(ASSETS_DIRECTORY + getString(fileNameResourceId));
  }
  
  private void showCandidateOrderDialog() {
    
    candidateOrderDialogBuilder = new AlertDialog.Builder(this, R.style.StrokeInputDialog);
    candidateOrderDialogBuilder
      .setTitle(R.string.text__main_activity__candidate_order)
      .setView(R.layout.candidate_order_dialog)
      .setCancelable(true);
    
    candidateOrderDialog = candidateOrderDialogBuilder.create();
    candidateOrderDialog.show();
    
    final RadioGroup candidateOrderRadioGroup = candidateOrderDialog.findViewById(R.id.candidate_order_radio_group);
    final Button preferTraditionalButton = candidateOrderDialog.findViewById(R.id.prefer_traditional_button);
    final Button preferSimplifiedButton = candidateOrderDialog.findViewById(R.id.prefer_simplified_button);
    
    final boolean traditionalIsPreferred = isTraditionalPreferred(loadSavedCandidateOrderPreference());
    final int savedCandidateOrderButtonId = (
      traditionalIsPreferred
        ? R.id.prefer_traditional_button
        : R.id.prefer_simplified_button
    );
    candidateOrderRadioGroup.check(savedCandidateOrderButtonId);
    
    preferTraditionalButton.setOnClickListener(this);
    preferSimplifiedButton.setOnClickListener(this);
    
  }
  
}
