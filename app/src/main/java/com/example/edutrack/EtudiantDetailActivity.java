package com.example.edutrack;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activité pour afficher les détails d'un étudiant et son historique de présence
 */
public class EtudiantDetailActivity extends AppCompatActivity {

    private TextView txtNom, txtPresence, txtAbsence;
    private ImageView imgProfile;
    private ListView listHistorique;
    private Button btnRetour;
    private PresenceDB presenceDB;
    private String nom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant_detail);

        // Initialisation des composants
        txtNom = findViewById(R.id.txtNom);
        txtPresence = findViewById(R.id.txtPresence);
        txtAbsence = findViewById(R.id.txtAbsence);
        imgProfile = findViewById(R.id.imgProfile);
        listHistorique = findViewById(R.id.listHistorique);
        btnRetour = findViewById(R.id.btnRetour);

        presenceDB = new PresenceDB(this);

        // Récupération des données de l'étudiant
        nom = getIntent().getStringExtra("etudiant_nom");
        int image = getIntent().getIntExtra("etudiant_image", R.drawable.ic_launcher_foreground);

        // Mise à jour de l'interface avec les données
        txtNom.setText(nom);
        imgProfile.setImageResource(image);

        // Mise à jour des compteurs de présence/absence
        txtPresence.setText("Présent : " + presenceDB.compterParStatut(nom, "Présent"));
        txtAbsence.setText("Absent : " + presenceDB.compterParStatut(nom, "Absent"));

        // Chargement de l'historique
        refreshHistorique();

        // Gestion du bouton retour
        btnRetour.setOnClickListener(v -> finish());
    }

    /**
     * Rafraîchit l'affichage de l'historique des présences
     */
    private void refreshHistorique() {
        ArrayList<HashMap<String, String>> historique = presenceDB.getHistorique(nom);

        // Création de l'adaptateur pour la liste
        ArrayAdapter<HashMap<String, String>> adapter = new ArrayAdapter<HashMap<String, String>>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                historique) {

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                String date = getItem(position).get("date");
                String statut = getItem(position).get("statut");

                // Formatage des données
                text1.setText("📅 " + date);
                text2.setText("📌 " + statut);

                // Gestion du clic long pour supprimer une entrée
                view.setOnLongClickListener(v -> {
                    new AlertDialog.Builder(EtudiantDetailActivity.this)
                            .setTitle("Supprimer cette entrée ?")
                            .setMessage(date + " - " + statut)
                            .setPositiveButton("Supprimer", (dialog, which) -> {
                                presenceDB.supprimerPresence(nom, date);
                                refreshHistorique();
                                // Mise à jour des compteurs
                                txtPresence.setText("Présent : " + presenceDB.compterParStatut(nom, "Présent"));
                                txtAbsence.setText("Absent : " + presenceDB.compterParStatut(nom, "Absent"));
                            })
                            .setNegativeButton("Annuler", null)
                            .show();
                    return true;
                });

                return view;
            }
        };

        listHistorique.setAdapter(adapter);
    }
}