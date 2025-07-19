package com.example.edutrack;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur pour afficher la liste des étudiants dans un RecyclerView
 */
public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {

    private final Context context;
    private final List<Etudiant> etudiants;
    private final MyDB db;
    private final PresenceDB presenceDB;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants, MyDB db) {
        this.context = context;
        this.etudiants = etudiants;
        this.db = db;
        this.presenceDB = new PresenceDB(context);
    }

    @Override
    public EtudiantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Création de la vue pour chaque élément étudiant
        View v = LayoutInflater.from(context).inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EtudiantViewHolder holder, int position) {
        // Récupération de l'étudiant à la position donnée
        Etudiant e = etudiants.get(position);

        // Mise à jour des vues avec les données de l'étudiant
        holder.nom.setText(e.getNom());
        holder.ville.setText("Module : " + e.getVille());
        holder.age.setText("Semestre : " + e.getAge());
        holder.sexe.setText("Sexe : " + e.getGenre());
        holder.image.setImageResource(e.getImage());

        // Gestion de la suppression d'un étudiant
        holder.btnModifier.setOnClickListener(v -> {
            Intent intent = new Intent(context, AjoutEtudiantActivity.class);
            intent.putExtra("edit_mode", true);
            intent.putExtra("position", holder.getAdapterPosition());
            intent.putExtra("etudiant_nom", e.getNom());
            intent.putExtra("etudiant_semestre", e.getAge());
            intent.putExtra("etudiant_module", e.getVille());
            intent.putExtra("etudiant_genre", e.getGenre());
            intent.putExtra("etudiant_image", e.getImage());
            ((Activity) context).startActivityForResult(intent, 2);
        });

        holder.btnSupprimer.setOnClickListener(v -> {
            db.supprimerEtudiant(e.getNom(), e.getVille());
            etudiants.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, etudiants.size());
        });

        // Marquage comme présent
        holder.btnPresent.setOnClickListener(v -> {
            String today = getTodayDate();
            presenceDB.enregistrerPresence(e.getNom(), today, "Présent");
            Toast.makeText(context, e.getNom() + " est marqué Présent", Toast.LENGTH_SHORT).show();
        });

        // Marquage comme absent
        holder.btnAbsent.setOnClickListener(v -> {
            String today = getTodayDate();
            presenceDB.enregistrerPresence(e.getNom(), today, "Absent");
            Toast.makeText(context, e.getNom() + " est marqué Absent", Toast.LENGTH_SHORT).show();
        });

        // Gestion du clic pour voir les détails
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EtudiantDetailActivity.class);
            intent.putExtra("etudiant_nom", e.getNom());
            intent.putExtra("etudiant_image", e.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    // Obtention de la date actuelle au format YYYY-MM-DD
    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * ViewHolder contenant les vues pour chaque élément étudiant
     */
    public static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nom, ville, age, sexe;
        ImageView image;
        ImageButton btnSupprimer, btnModifier;
        Button btnPresent, btnAbsent;

        public EtudiantViewHolder(View itemView) {
            super(itemView);
            // Liaison des vues avec les IDs
            nom = itemView.findViewById(R.id.etudiantNom);
            ville = itemView.findViewById(R.id.etudiantVille);
            age = itemView.findViewById(R.id.etudiantAge);
            sexe = itemView.findViewById(R.id.etudiantSexe);
            image = itemView.findViewById(R.id.etudiantImage);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
            btnModifier = itemView.findViewById(R.id.btnModifier);
            btnPresent = itemView.findViewById(R.id.btnPresent);
            btnAbsent = itemView.findViewById(R.id.btnAbsent);
        }
    }
}