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
    
    setCandidateOrderButtonText(loadSavedTraditionalIsPreferred());
  }
  
  private boolean loadSavedTraditionalIsPreferred() {
    
    final String savedCandidateOrderPreference =
      Contexty.loadPreferenceString(
        getApplicationContext(),
        StrokeInputService.PREFERENCES_FILE_NAME,
        "candidateOrderPreference"
      );
    
    if (savedCandidateOrderPreference == null) {
      return true;
    }
    
    return !savedCandidateOrderPreference.equals("PREFER_SIMPLIFIED");
  }
  
  private void setCandidateOrderButtonText(final boolean traditionalIsPreferred) {
    
    final TextView candidateOrderButton = findViewById(R.id.candidate_order_button);
    final String candidateOrderButtonText = (
      traditionalIsPreferred
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
    
  }
  
  private void showCandidateOrderDialog() {
    
    candidateOrderDialogBuilder = new AlertDialog.Builder(this, R.style.StrokeInputDialog);
    candidateOrderDialogBuilder
      .setTitle(R.string.text__main_activity__candidate_order)
      .setView(R.layout.candidate_order_dialog)
      .setCancelable(true);
    
    candidateOrderDialog = candidateOrderDialogBuilder.create();
    candidateOrderDialog.show();
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
  
}
