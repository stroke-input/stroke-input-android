/*
  Copyright 2025 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.switchmaterial.SwitchMaterial;

/*
  A switch for toggling optional candidates.
*/
public class OptionalCandidatesSwitch
  extends SwitchMaterial
{
  public OptionalCandidatesSwitch(Context context, AttributeSet attributes)
  {
    super(context, attributes);
  }
}
