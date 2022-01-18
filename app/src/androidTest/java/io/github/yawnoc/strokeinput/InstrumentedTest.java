/*
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/*
 An instrumented test, to be executed on an Android device or emulator.
*/
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest
{
  @Test
  public void useAppContext()
  {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    assertEquals("io.github.yawnoc.strokeinput", appContext.getPackageName());
  }
}
