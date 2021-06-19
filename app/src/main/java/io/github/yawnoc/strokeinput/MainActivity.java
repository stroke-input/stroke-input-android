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
import android.widget.Button;

public class MainActivity
  extends AppCompatActivity
  implements View.OnClickListener
{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    Button inputSettingsButton = findViewById(R.id.input_settings_button);
    inputSettingsButton.setOnClickListener(this);
    
    Button switchKeyboardButton = findViewById(R.id.switch_keyboard_button);
    switchKeyboardButton.setOnClickListener(this);
    
  }
  
  @Override
  public void onClick(View view) {
    
    int viewId = view.getId();
    
    if (viewId == R.id.input_settings_button) {
      Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
      startActivity(intent);
    }
    else if (viewId == R.id.switch_keyboard_button) {
      InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.showInputMethodPicker();
    }
    
  }
  
}
