package com.example.edutrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Gestionnaire de la base de données pour les étudiants
 */
public class MyDB extends SQLiteOpenHelper {

    // Configuration de la base de données
    private static final String DB_NAME = "etudiants.db";
    private static final int DB_VERSION = 2;
    
    private static final String TABLE_NAME = "etudiants";
    private static final String COL_ID = "id";
    private static final String COL_NOM = "nom";
    private static final String COL_MODULE = "module";
    private static final String COL_SEMESTRE = "semestre";
    
    
    private static final String COL_GENRE = "genre";
    private static final String COL_IMAGE = "image";

    public MyDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Création de la table lors de la première initialisation
    @Override
    public void onCreate(SQLiteDatabase db) {
        String req = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM + " TEXT, " +
                COL_MODULE + " TEXT, " +
                COL_SEMESTRE + " INTEGER, " +
                COL_GENRE + " TEXT, " +
                COL_IMAGE + " INTEGER)";
        db.execSQL(req);
    }

    // Mise à jour de la base lors d'un changement de version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Ajoute un étudiant à la base de données
     * @param etudiant L'étudiant à ajouter
     * @return L'ID de la nouvelle ligne insérée
     */
    public long ajouterEtudiant(Etudiant etudiant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOM, etudiant.getNom());
        values.put(COL_MODULE, etudiant.getVille());
        values.put(COL_SEMESTRE, etudiant.getAge());
        values.put(COL_GENRE, etudiant.getGenre());
        values.put(COL_IMAGE, etudiant.getImage());
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Supprime un étudiant de la base de données
     * @param nom Le nom de l'étudiant
     * @param ville La ville de l'étudiant
     * @return Le nombre de lignes affectées
     */
    public int supprimerEtudiant(String nom, String ville) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COL_NOM + "=? AND " + COL_MODULE + "=?",
                new String[]{nom, ville});
    }

    /**
     * Récupère tous les étudiants de la base de données
     * @return Liste des étudiants
     */
    public ArrayList<Etudiant> getAllEtudiants() {
        ArrayList<Etudiant> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Récupération des données dans un curseur
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extraction des données du curseur
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOM));
                String ville = cursor.getString(cursor.getColumnIndexOrThrow(COL_MODULE));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEMESTRE));
                String genre = cursor.getString(cursor.getColumnIndexOrThrow(COL_GENRE));
                int image = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGE));

                // Création de l'objet Etudiant
                list.add(new Etudiant(nom, age, ville, genre, image));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }
}