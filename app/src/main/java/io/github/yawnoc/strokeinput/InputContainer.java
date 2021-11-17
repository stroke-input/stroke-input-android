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
  private InputListener inputListener;
  private Keyboard keyboard;
  private Key[] keyArray;
  private int touchableTopY;
  
  public InputContainer(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  /*
    A listener for input events.
  */
  public interface InputListener {
    Keyboard loadSavedKeyboard();
    void saveKeyboard(Keyboard keyboard);
  }
  
  public void setInputListener(final InputListener inputListener) {
    this.inputListener = inputListener;
  }
  
}
