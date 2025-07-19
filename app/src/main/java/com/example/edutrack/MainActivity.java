// MainActivity.java
package com.example.edutrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Etudiant> etudiantList;
    private ArrayList<Etudiant> allEtudiants;
    private EtudiantAdapter adapter;
    private EditText searchBar;
    private Spinner spinnerGenre, spinnerVille, spinnerSemestre;
    private Button btnAdd;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_EDIT = 2;
    private MyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des composants UI
        recyclerView = findViewById(R.id.recyclerEtudiants);
        searchBar = findViewById(R.id.searchBar);
        btnAdd = findViewById(R.id.btnAdd);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        spinnerVille = findViewById(R.id.spinnerVille);
        spinnerSemestre = findViewById(R.id.spinnerAge);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation de la base de données
        db = new MyDB(this);

        // Chargement des étudiants depuis la BD
        allEtudiants = db.getAllEtudiants();
        etudiantList = new ArrayList<>(allEtudiants);

        // Initialisation de l'adaptateur
        adapter = new EtudiantAdapter(this, etudiantList, db);
        recyclerView.setAdapter(adapter);

        // Écouteur pour la barre de recherche
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Configuration des spinners de filtre
        String[] genres = {"Genres", "Tous", "Homme", "Femme"};
        String[] modules = {
                "Modules", "Tous", "Programmation Mobile", "Deep Learning"
        };
        String[] semestres = {"Semestres", "Tous", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        spinnerGenre.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genres));
        spinnerVille.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, modules));
        spinnerSemestre.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semestres));

        // Écouteurs pour les spinners
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters(); // Appliquer les filtres quand une sélection change
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerGenre.setOnItemSelectedListener(filterListener);
        spinnerVille.setOnItemSelectedListener(filterListener);
        spinnerSemestre.setOnItemSelectedListener(filterListener);

        // Bouton pour ajouter un nouvel étudiant
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AjoutEtudiantActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    /**
     * Applique les filtres de recherche et les sélections des spinners
     * avec une logique ET entre tous les filtres
     */
    private void applyFilters() {
        String query = searchBar.getText().toString().toLowerCase();
        String genreSelected = spinnerGenre.getSelectedItem().toString();
        String moduleSelected = spinnerVille.getSelectedItem().toString();
        String semestreSelected = spinnerSemestre.getSelectedItem().toString();

        ArrayList<Etudiant> filtered = new ArrayList<>();

        for (Etudiant e : allEtudiants) {
            // Vérifier si l'étudiant correspond à TOUS les critères
            boolean matches = true;

            // Filtre par nom (si recherche non vide)
            if (!query.isEmpty()) {
                if (e.getNom() == null || !e.getNom().toLowerCase().contains(query)) {
                    matches = false;
                }
            }

            // Filtre par genre (si sélectionné)
            if (matches && !genreSelected.equals("Genres") && !genreSelected.equals("Tous")) {
                if (e.getGenre() == null || !e.getGenre().equalsIgnoreCase(genreSelected)) {
                    matches = false;
                }
            }

            // Filtre par module (si sélectionné)
            if (matches && !moduleSelected.equals("Modules") && !moduleSelected.equals("Tous")) {
                if (e.getVille() == null || !e.getVille().equalsIgnoreCase(moduleSelected)) {
                    matches = false;
                }
            }

            // Filtre par semestre (si sélectionné)
            if (matches && !semestreSelected.equals("Semestres") && !semestreSelected.equals("Tous")) {
                if (String.valueOf(e.getAge()) == null || !String.valueOf(e.getAge()).equals(semestreSelected)) {
                    matches = false;
                }
            }

            // Si tous les critères sont satisfaits, ajouter à la liste filtrée
            if (matches) {
                filtered.add(e);
            }
        }

        // Mettre à jour la liste affichée
        etudiantList.clear();
        etudiantList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    // Gestion du résultat de l'activité d'ajout
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Récupérer les données du nouvel étudiant
            String nom = data.getStringExtra("etudiant_nom");
            int semestre = data.getIntExtra("etudiant_semestre", 0);
            String module = data.getStringExtra("etudiant_module");
            String genre = data.getStringExtra("etudiant_genre");
            int image = data.getIntExtra("etudiant_image", R.drawable.ic_launcher_foreground);

            // Créer le nouvel étudiant
            Etudiant newEtudiant = new Etudiant(nom, semestre, module, genre, image);

            // Ajouter à la base de données
            db.ajouterEtudiant(newEtudiant);

            // Ajouter à la liste complète et appliquer les filtres
            allEtudiants.add(0, newEtudiant);
            applyFilters();
        } else if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                // remove old from DB
                Etudiant old = allEtudiants.get(position);
                db.supprimerEtudiant(old.getNom(), old.getVille());
                // construct new student
                String nom = data.getStringExtra("etudiant_nom");
                int semestre = data.getIntExtra("etudiant_semestre", 0);
                String module = data.getStringExtra("etudiant_module");
                String genre = data.getStringExtra("etudiant_genre");
                int image = data.getIntExtra("etudiant_image", R.drawable.ic_launcher_foreground);
                Etudiant updated = new Etudiant(nom, semestre, module, genre, image);
                // add to db and list
                db.ajouterEtudiant(updated);
                allEtudiants.set(position, updated);
                adapter.notifyItemChanged(position);
            }
        }
    }
}