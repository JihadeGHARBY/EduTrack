package com.example.edutrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * Activité pour ajouter un nouvel étudiant à la base de données
 */
public class AjoutEtudiantActivity extends Activity {

    // Déclaration des composants UI
    private EditText editNom, editAge;
    private RadioGroup radioGenre;
    private Spinner spinnerImages, spinnerModule;
    private Button btnAjouter;

    // Tableau des IDs des images des étudiants
    private int[] imageIds = {R.drawable.random_image, R.drawable.jihade, R.drawable.oussama,
            R.drawable.brahim, R.drawable.abdessamad, R.drawable.marwane,
            R.drawable.nacer, R.drawable.dib, R.drawable.ikram, R.drawable.assia,
            R.drawable.issam, R.drawable.morad, R.drawable.elhoussain, R.drawable.fatima};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout);

        // Initialisation des composants
        editNom = findViewById(R.id.editNom);
        editAge = findViewById(R.id.editAge); // Champ semestre
        spinnerModule = findViewById(R.id.spinnerVille);
        radioGenre = findViewById(R.id.radioGenre);
        spinnerImages = findViewById(R.id.spinnerImages);
        btnAjouter = findViewById(R.id.btnEnregistrer);

        // Configuration de la liste des modules
        String[] modules = {"Module", "Programmation Mobile", "Deep Learning"};

        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modules);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModule.setAdapter(moduleAdapter);

        // Configuration des noms d'images pour le spinner
        String[] nomsImages = {"Choisir une image", "Jihade GHARBY", "Oussama CHAKIR", "Brahim BAZI", "Abdessamad El FATHI",
                "Marwane ERRACHIDI", "Nacer BOUBKRAOUI", "Moohamed DIB", "Ikram CHAMI", "Assia El ALLAOUI",
                "Issam ECH-CHARIY", "Morad BOUSSAGMAN", "Elhoussain OULHAJ", "Fatima-Zahra BOULAGHLA"};

        ArrayAdapter<String> imageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomsImages);
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImages.setAdapter(imageAdapter);

        // Vérifier si c'est un mode édition
        Intent intent = getIntent();
        boolean isEdit = intent.getBooleanExtra("edit_mode", false);
        if (isEdit) {
            setTitle("Modifier Étudiant");
            btnAjouter.setText("Modifier");
            // Pré-remplir les champs
            editNom.setText(intent.getStringExtra("etudiant_nom"));
            editAge.setText(String.valueOf(intent.getIntExtra("etudiant_semestre", 0)));
            // module spinner selection
            String moduleInit = intent.getStringExtra("etudiant_module");
            for (int i = 0; i < modules.length; i++) {
                if (modules[i].equals(moduleInit)) {
                    spinnerModule.setSelection(i);
                    break;
                }
            }
            String genreInit = intent.getStringExtra("etudiant_genre");
            if (genreInit != null) {
                if (genreInit.equals("Homme")) radioGenre.check(R.id.radioHomme);
                else if (genreInit.equals("Femme")) radioGenre.check(R.id.radioFemme);
            }
            int imageInit = intent.getIntExtra("etudiant_image", R.drawable.ic_launcher_foreground);
            // set spinnerImages selection
            for (int i = 0; i < imageIds.length; i++) {
                if (imageIds[i] == imageInit) {
                    spinnerImages.setSelection(i);
                    break;
                }
            }
        }

        // Gestion du clic sur le bouton "Ajouter"
        btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération des valeurs des champs
                String nom = editNom.getText().toString().trim();
                String semestreStr = editAge.getText().toString().trim();
                int genreId = radioGenre.getCheckedRadioButtonId();

                // Validation des champs obligatoires
                if (nom.isEmpty() || semestreStr.isEmpty() || genreId == -1) {
                    Toast.makeText(AjoutEtudiantActivity.this,
                            "Veuillez saisir le nom, le semestre et le genre.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int semestre = Integer.parseInt(semestreStr);
                String module = spinnerModule.getSelectedItem().toString();
                RadioButton selectedRadio = findViewById(genreId);
                String genre = selectedRadio.getText().toString();
                int imageRes = imageIds[spinnerImages.getSelectedItemPosition()];

                // Préparation des données à retourner à MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("etudiant_nom", nom);
                resultIntent.putExtra("etudiant_semestre", semestre);
                resultIntent.putExtra("etudiant_module", module);
                resultIntent.putExtra("etudiant_genre", genre);
                resultIntent.putExtra("etudiant_image", imageRes);
                if (isEdit) {
                    resultIntent.putExtra("position", intent.getIntExtra("position", -1));
                }
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}