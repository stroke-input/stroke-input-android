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

/*
  A view that holds candidates.
*/
public class CandidatesView
  extends RecyclerView
{
  private CandidatesViewAdapter candidatesViewAdapter;
  
  public CandidatesView(final Context context, final AttributeSet attributes)
  {
    super(context, attributes);
    initialiseCandidatesViewAdapter(context);
  }
  
  public CandidatesViewAdapter getCandidatesViewAdapter()
  {
    return candidatesViewAdapter;
  }
  
  private void initialiseCandidatesViewAdapter(final Context context)
  {
    candidatesViewAdapter = new CandidatesViewAdapter(context, new ArrayList<>());
    setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    setAdapter(candidatesViewAdapter);
  }
  
  public void setCandidateListener(final CandidatesViewAdapter.CandidateListener candidateListener)
  {
    candidatesViewAdapter.setCandidateListener(candidateListener);
  }
}
