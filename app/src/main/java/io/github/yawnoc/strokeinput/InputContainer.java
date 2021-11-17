/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class InputContainer
  extends LinearLayout
{
  
  // Container properties
  private KeyboardListener keyboardListener;
  private Keyboard keyboard;
  private Key[] keyArray;
  private int touchableTopY;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  /*
    A listener for keyboard events.
  */
  public interface KeyboardListener {
    Keyboard loadSavedKeyboard();
    void saveKeyboard(Keyboard keyboard);
  }
  
  public void setKeyboardListener(final KeyboardListener keyboardListener) {
    this.keyboardListener = keyboardListener;
  }
  
}
