/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import io.github.yawnoc.strokeinput.R;

public final class Contexty
{
  private Contexty()
  {
    // Do not instantiate
  }
  
  public static String loadPreferenceString(
    final Context context,
    final String preferenceFileName,
    final String preferenceKey
  )
  {
    final SharedPreferences preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
    return preferences.getString(preferenceKey, null);
  }
  
  public static void savePreferenceString(
    final Context context,
    final String preferenceFileName,
    final String preferenceKey,
    final String preferenceValue
  )
  {
    final SharedPreferences preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
    SharedPreferences.Editor preferencesEditor = preferences.edit();
    preferencesEditor.putString(preferenceKey, preferenceValue);
    preferencesEditor.apply();
  }
  
  public static void showErrorMessage(final Context context, final String message)
  {
    final TextView alertTextView = new TextView(new ContextThemeWrapper(context, R.style.StrokeInputMessage));
    alertTextView.setText(message);
    alertTextView.setTextIsSelectable(true);
    
    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.StrokeInputAlert);
    alertDialogBuilder
            .setPositiveButton(R.string.label__main_activity__return, null)
            .setView(alertTextView);
    
    final Dialog alertDialog = alertDialogBuilder.create();
    final int dialog_size = ViewGroup.LayoutParams.WRAP_CONTENT;
    alertDialog.show();
    alertDialog.getWindow().setLayout(dialog_size, dialog_size);
  }
  
  public static void showSystemInputMethodSettings(final Context context)
  {
    final Intent inputMethodSettingsIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
    context.startActivity(inputMethodSettingsIntent);
  }
  
  public static void showSystemKeyboardChanger(final Context context)
  {
    final InputMethodManager inputMethodManager =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showInputMethodPicker();
  }
  
  public static void openInBrowser(final Context context, final String uri)
  {
    final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    try
    {
      context.startActivity(browserIntent);
    }
    catch (Exception exception)
    {
      showErrorMessage(context, context.getString(R.string.message__error__no_browser, uri));
    }
  }
}
