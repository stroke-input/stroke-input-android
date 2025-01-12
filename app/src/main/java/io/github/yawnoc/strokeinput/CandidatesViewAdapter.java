/*
  Copyright 2021, 2023 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*
  An adapter which holds candidate buttons.
*/
public class CandidatesViewAdapter
  extends RecyclerView.Adapter<CandidatesViewAdapter.ButtonHolder>
{
  private CandidateListener candidateListener;
  private final LayoutInflater layoutInflater;
  private final List<String> candidates;

  CandidatesViewAdapter(final Context context, final List<String> candidates)
  {
    this.layoutInflater = LayoutInflater.from(context);
    this.candidates = candidates;
  }

  public interface CandidateListener
  {
    void onCandidate(String candidate);
  }

  public void setCandidateListener(final CandidateListener candidateListener)
  {
    this.candidateListener = candidateListener;
  }

  @SuppressLint("NotifyDataSetChanged")
  public void updateCandidates(final List<String> candidates)
  {
    this.candidates.clear();
    this.candidates.addAll(candidates);
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ButtonHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType)
  {
    final Button candidateButton = (Button) layoutInflater.inflate(R.layout.candidate_button, viewGroup, false);
    return new ButtonHolder(candidateButton);
  }

  @Override
  public void onBindViewHolder(@NonNull final ButtonHolder buttonHolder, final int candidateIndex)
  {
    final String candidate = candidates.get(candidateIndex);
    buttonHolder.candidateButton.setText(candidate);
  }

  @Override
  public int getItemCount()
  {
    return candidates.size();
  }

  public class ButtonHolder
    extends RecyclerView.ViewHolder
    implements View.OnClickListener
  {
    private final Button candidateButton;

    public ButtonHolder(final Button candidateButton)
    {
      super(candidateButton);
      candidateButton.setOnClickListener(this);
      this.candidateButton = candidateButton;
    }

    @Override
    public void onClick(View view)
    {
      if (candidateListener != null)
      {
        candidateListener.onCandidate((String) candidateButton.getText());
      }
    }
  }
}
