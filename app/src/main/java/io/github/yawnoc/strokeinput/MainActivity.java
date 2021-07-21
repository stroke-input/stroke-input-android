/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import static io.github.yawnoc.Utilities.openInBrowser;
import static io.github.yawnoc.Utilities.showSystemInputMethodSettings;
import static io.github.yawnoc.Utilities.showSystemKeyboardSwitcher;

/*
  The main activity of the application.
*/
public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  public static final String ASSETS_DIRECTORY = "file:///android_asset/";
  public static final String SOURCE_CODE_URI =
    "https://github.com/stroke-input/stroke-input-android";
  
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
    
  }
  
  @Override
  public void onClick(final View view) {
    
    final int viewId = view.getId();
    
    if (viewId == R.id.source_code_button) {
      openInBrowser(this, SOURCE_CODE_URI);
    }
    else if (viewId == R.id.help_button) {
      showHtmlWebView(R.string.help_html_file_name);
    }
    else if (viewId == R.id.about_button) {
      showHtmlWebView(R.string.about_html_file_name);
    }
    else if (viewId == R.id.input_method_settings_button) {
      showSystemInputMethodSettings(this);
    }
    else if (viewId == R.id.switch_keyboard_button) {
      showSystemKeyboardSwitcher(this);
    }
    
  }
  
  private void showHtmlWebView(final String uri) {
    
    if (htmlWebViewContainer == null) {
      htmlWebViewContainer =
        new AlertDialog.Builder(this, R.style.StrokeInputAlert);
      htmlWebViewContainer
        .setPositiveButton(R.string.main_activity__return_label, null);
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
      .setOnDismissListener(
        dialog -> ((ViewGroup) htmlWebView.getParent()).removeView(htmlWebView)
      )
      .show();
  }
  
  private void showHtmlWebView(final int fileNameResourceId) {
    showHtmlWebView(ASSETS_DIRECTORY + getString(fileNameResourceId));
  }
  
}
