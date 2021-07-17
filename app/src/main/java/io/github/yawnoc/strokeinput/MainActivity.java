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

/*
  The main activity of the application.
*/
public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  public static final String SOURCE_CODE_URI =
    "https://github.com/stroke-input/stroke-input-android";
  public static final String ABOUT_URI =
    "file:///android_asset/about.html";
  
  AlertDialog.Builder htmlWebViewContainer;
  WebView htmlWebView;
  
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    findViewById(R.id.source_code_button).setOnClickListener(this);
    findViewById(R.id.about_button).setOnClickListener(this);
    findViewById(R.id.input_method_settings_button).setOnClickListener(this);
    findViewById(R.id.switch_keyboard_button).setOnClickListener(this);
    
  }
  
  @Override
  public void onClick(final View view) {
    
    final int viewId = view.getId();
    
    if (viewId == R.id.source_code_button) {
      Utilities.openInBrowser(this, SOURCE_CODE_URI);
    }
    else if (viewId == R.id.about_button) {
      showHtmlWebView(ABOUT_URI);
    }
    else if (viewId == R.id.input_method_settings_button) {
      Utilities.showSystemInputMethodSettings(this);
    }
    else if (viewId == R.id.switch_keyboard_button) {
      Utilities.showSystemKeyboardSwitcher(this);
    }
    
  }
  
  private void showHtmlWebView(final String uri) {
    
    if (htmlWebViewContainer == null) {
      htmlWebViewContainer =
        new AlertDialog.Builder(this, R.style.StrokeInputAlert);
      htmlWebViewContainer
        .setPositiveButton(R.string.activity_main_return_label, null);
    }
    
    if (htmlWebView == null) {
      htmlWebView = new WebView(this);
      htmlWebView.setBackgroundColor(Color.TRANSPARENT);
    }
  
    htmlWebView.loadUrl(uri);
    htmlWebViewContainer
      .setView(htmlWebView)
      .setOnDismissListener(
        dialog -> ((ViewGroup) htmlWebView.getParent()).removeView(htmlWebView)
      )
      .show();
  }
  
}
