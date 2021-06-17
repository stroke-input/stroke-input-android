package io.github.yawnoc.strokeinput;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    
    Button switchKeyboardButton =
      (Button) findViewById(R.id.switch_keyboard_button);
    switchKeyboardButton.setOnClickListener(this);
    
  }
  
  @Override
  public void onClick(View view) {
    
    int viewId = view.getId();
    
    if (viewId == R.id.switch_keyboard_button) {
      InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.showInputMethodPicker();
    }
    
  }
  
}
