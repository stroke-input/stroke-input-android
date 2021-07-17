/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

public final class Utilities {
  
  public static boolean isAscii(final String string) {
    return string.matches("\\p{ASCII}*");
  }
  
  public static void showSystemInputMethodSettings(final Context context) {
    
    final Intent inputSettingsIntent =
      new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
    
    context.startActivity(inputSettingsIntent);
  }
  
  public static void showSystemKeyboardSwitcher(final Context context) {
    
    final InputMethodManager inputMethodManager =
      (InputMethodManager)
        context.getSystemService(Context.INPUT_METHOD_SERVICE);
    
    inputMethodManager.showInputMethodPicker();
  }
  
}
