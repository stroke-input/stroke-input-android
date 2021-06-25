/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This is a re-implementation of the deprecated `KeyboardView` class,
  which is licensed under the Apache License 2.0,
  see <https://www.apache.org/licenses/LICENSE-2.0.html>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;
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
  
  public InputContainer(Context context, AttributeSet attributes) {
    this(context, attributes, R.attr.keyboardViewStyle);
  }
  
  public InputContainer(
    Context context,
    AttributeSet attributes,
    int defaultStyleAttribute
  )
  {
    this(context, attributes, defaultStyleAttribute, 0);
  }
  
  public InputContainer(
    Context context,
    AttributeSet attributes,
    int defaultStyleAttribute,
    int defaultStyleResourceId
  )
  {
    super(context, attributes, defaultStyleAttribute, defaultStyleResourceId);
  }
  
  public void setKeyboard(Keyboard keyboard) {
    currentKeyboard = keyboard;
    List<Keyboard.Key> keyList = currentKeyboard.getKeyList();
    requestLayout();
  }
  
}
