/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*
  An adapter for a candidates bar, which holds candidate buttons.
*/
public class CandidatesBarAdapter
  extends RecyclerView.Adapter<CandidatesBarAdapter.ButtonHolder>
{
  private final LayoutInflater layoutInflater;
  private final List<String> candidateList;
  
  CandidatesBarAdapter(
    final Context context,
    final List<String> candidateList
  )
  {
    this.layoutInflater = LayoutInflater.from(context);
    this.candidateList = candidateList;
  }
  
  @NonNull
  @Override
  public ButtonHolder onCreateViewHolder(
    @NonNull final ViewGroup viewGroup,
    final int viewType
  )
  {
    Button candidateButton =
      (Button)
        layoutInflater.inflate(R.layout.candidate_button, viewGroup, false);
    
    return new ButtonHolder(candidateButton);
  }
  
  @Override
  public void onBindViewHolder(
    @NonNull final ButtonHolder buttonHolder,
    final int candidateIndex
  )
  {
    String candidate = candidateList.get(candidateIndex);
    buttonHolder.candidateButton.setText(candidate);
  }
  
  @Override
  public int getItemCount() {
    return candidateList.size();
  }
  
  public static class ButtonHolder extends RecyclerView.ViewHolder {
    
    private final Button candidateButton;
    
    public ButtonHolder(final Button candidateButton) {
      super(candidateButton);
      this.candidateButton = candidateButton;
    }
    
    public Button getCandidateButton() {
      return candidateButton;
    }
  }
}