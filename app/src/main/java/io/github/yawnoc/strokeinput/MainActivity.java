/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/*
  The main activity of the application.
  Contains:
    - Welcome
    - Button for enabling the Stroke Input Method
    - Button for switching keyboards
    - Box for testing input
  TODO:
    - About
    - Link to <https://github.com/yawnoc/StrokeInput>
*/
public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    findViewById(R.id.input_settings_button).setOnClickListener(this);
    findViewById(R.id.switch_keyboard_button).setOnClickListener(this);
    
  }
  
  @Override
  public void onClick(final View view) {
    
    final int viewId = view.getId();
    
    if (viewId == R.id.input_settings_button) {
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
