/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This file contains bytes copied from the deprecated `KeyboardView` class,
  i.e. `core/java/android/inputmethodservice/KeyboardView.java`
  from <https://android.googlesource.com/platform/frameworks/base>,
  which is licensed under the Apache License 2.0,
  see <https://www.apache.org/licenses/LICENSE-2.0.html>.
  ---
  Take your pick from the following out-of-date notices:
  In `core/java/android/inputmethodservice/KeyboardView.java`:
    Copyright (C) 2008-2009 Google Inc.
  In `NOTICE`:
    Copyright 2005-2008 The Android Open Source Project
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/*
  A container that holds a keyboard.
*/
public class KeyboardView
  extends View
  implements View.OnClickListener
{
  
  // Container properties
  private KeyboardListener keyboardListener;
  private Keyboard keyboard;
  private List<Key> keyList;
  
  public static final int SHIFT_DISABLED = 0;
  
  public KeyboardView(final Context context, final AttributeSet attributes) {
    super(context, attributes);
  }
  
  public interface KeyboardListener {
    void saveKeyboard(Keyboard keyboard);
  }
  
  public void setKeyboardListener(final KeyboardListener keyboardListener) {
    this.keyboardListener = keyboardListener;
  }
  
  public void setKeyboard(final Keyboard keyboard) {
    keyboardListener.saveKeyboard(keyboard);
    this.keyboard = keyboard;
    keyList = keyboard.getKeyList();
    // TODO: paints
    // TODO: shift mode
    requestLayout();
  }
  
  @Override
  public void onClick(final View view) {
  }
  
}
