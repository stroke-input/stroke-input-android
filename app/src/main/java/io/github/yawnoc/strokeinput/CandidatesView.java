/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/*
  A view that holds candidates.
*/
public class CandidatesView
  extends RecyclerView
{
  
  // View properties
  private CandidatesViewAdapter candidatesViewAdapter;
  
  public CandidatesView(final Context context, final AttributeSet attributes) {
    super(context, attributes);
    initialiseCandidatesAdapting(context);
  }
  
  private void initialiseCandidatesAdapting(final Context context) {
    candidatesViewAdapter = new CandidatesViewAdapter(context, new ArrayList<>());
    setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    setAdapter(candidatesViewAdapter);
  }
  
}
