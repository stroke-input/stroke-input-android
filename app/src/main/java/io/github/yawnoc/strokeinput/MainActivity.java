/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

/*
  The main activity of the application.
*/
public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  private AlertDialog.Builder htmlAlertDialogBuilder;
  private WebView htmlWebView;
  
  public static final String SOURCE_CODE_URL =
    "https://github.com/stroke-input/stroke-input-android";
  public static final String ABOUT_URI =
    "file:///android_asset/about.html";
  
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    findViewById(R.id.source_code_button).setOnClickListener(this);
    findViewById(R.id.about_button).setOnClickListener(this);
    findViewById(R.id.input_settings_button).setOnClickListener(this);
    findViewById(R.id.switch_keyboard_button).setOnClickListener(this);
    
  }
  
  @Override
  public void onClick(final View view) {
    
    final int viewId = view.getId();
    
    if (viewId == R.id.source_code_button) {
      final Intent sourceCodeIntent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_CODE_URL));
      startActivity(sourceCodeIntent);
    }
    else if (viewId == R.id.about_button) {
      htmlWebView = new WebView(this);
      htmlWebView.loadUrl(ABOUT_URI);
      htmlAlertDialogBuilder = new AlertDialog.Builder(this);
      htmlAlertDialogBuilder
        .setView(htmlWebView)
        .setPositiveButton(R.string.activity_main_return_label, null)
        .show()
      ;
    }
    else if (viewId == R.id.input_settings_button) {
      final Intent inputSettingsIntent =
        new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
      startActivity(inputSettingsIntent);
    }
    else if (viewId == R.id.switch_keyboard_button) {
      final InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.showInputMethodPicker();
    }
    
  }
  
}
