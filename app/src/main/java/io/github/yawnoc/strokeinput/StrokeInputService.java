/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.view.View;

public class StrokeInputService
  extends InputMethodService
  implements InputContainer.OnInputListener
{
  
  public static final String PREFERENCES_FILE_NAME = "preferences.txt";
  
  private InputContainer inputContainer;
  
  @Override
  public View onCreateInputView() {
    initialiseInputContainer();
    return inputContainer;
  }
  
  @SuppressLint("InflateParams")
  private void initialiseInputContainer() {
    inputContainer = (InputContainer) getLayoutInflater().inflate(R.layout.input_container, null);
  }
  
  @Override
  public Keyboard loadSavedKeyboard() {
    return null;
  }
  
  @Override
  public void saveKeyboard(final Keyboard keyboard) {
  }
  
}
