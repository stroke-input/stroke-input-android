/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.view.View;

import java.util.List;

/*
  A container that holds:
  - TODO: Candidates bar
  - TODO: Keyboard
*/
public class InputContainer
  extends View
{
  private Keyboard currentKeyboard;
  
  public InputContainer(Context context) {
    super(context);
  }
  
  public void setKeyboard(Keyboard keyboard) {
    currentKeyboard = keyboard;
    List<Keyboard.Key> keyList = currentKeyboard.getKeyList();
    requestLayout();
  }
  
}
