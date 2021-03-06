package com.example.footballapi.controleur;

import androidx.annotation.NonNull;

import com.example.footballapi.model.model_recyclerview.matches.MatchesModel;
import com.example.footballapi.model.model_retrofit.competition.Classement;
import com.example.footballapi.model.model_retrofit.retrofit.football_data.RestFootballData;
import com.example.footballapi.view.fragments.MatchesFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchesCompetController {

    private MatchesFragment fragment;

    public MatchesCompetController(MatchesFragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Affiche la liste des matches d'une compétition
     * @param token token de la connexion
     */
    public void onCreate(final String token, int idCompet) {
        Call<Classement> call = RestFootballData.get().matchesCompetition(token, idCompet);
        call.enqueue(new Callback<Classement>() {
            @Override
            public void onResponse(@NonNull Call<Classement> call, @NonNull Response<Classement> response) {
                if (response.isSuccessful()) {
                    Classement classement = response.body();
                    assert classement != null;

                    List<MatchesModel> listFinal = new ArrayList<>();

                    for (int i = 0; i < classement.getMatches().size(); i++) {
                        MatchesModel model = new MatchesModel();
                        model.setMatchDay(String.valueOf(classement.getMatches().get(i).getMatchday()));
                        model.setHomeTeam(classement.getMatches().get(i).getHomeTeam().getName());
                        model.setAwayTeam(classement.getMatches().get(i).getAwayTeam().getName());
                        model.setWinner(classement.getMatches().get(i).getScore().getWinner());
                        model.setIdTeamAway(String.valueOf(classement.getMatches().get(i).getAwayTeam().getId()));
                        model.setIdTeamHome(String.valueOf(classement.getMatches().get(i).getHomeTeam().getId()));
                        model.setIdMatch(String.valueOf(classement.getMatches().get(i).getId()));
                        model.setStatus(String.valueOf(classement.getMatches().get(i).getStatus()));
                        model.setUtcDate(String.valueOf(classement.getMatches().get(i).getUtcDate()));

                        String date = classement.getMatches().get(i).getUtcDate().split("T")[0]; // Day
                        String[] dateDay = date.split("-");

                        if(classement.getMatches().get(i).getStatus().equals("FINISHED")) fragment.incrPositionDay();

                        // On vérifie si le match a déjà été joué ou pas
                        if (classement.getMatches().get(i).getStatus().equals("FINISHED"))
                            model.setScore(classement.getMatches().get(i).getScore().getFullTime().getHomeTeam() + " - " + classement.getMatches().get(i).getScore().getFullTime().getAwayTeam());
                        else model.setScore(dateDay[2] + "/" + dateDay[1]);

                        listFinal.add(model);
                    }
                    fragment.showList(listFinal);
                } else {
                    Snackbar.make(Objects.requireNonNull(fragment.getView()), "Le nombre d'appels a été dépassé", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Classement> call, @NonNull Throwable t) {
                Snackbar.make(Objects.requireNonNull(fragment.getView()), "Vérifiez votre connexion Internet", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
