package com.example.edutrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gestionnaire de la base de données pour les présences
 */
public class PresenceDB extends SQLiteOpenHelper {

    // Configuration de la base de données
    private static final String DB_NAME = "presence.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "presence";
    private static final String COL_ID = "id";
    private static final String COL_NOM = "nom";
    private static final String COL_DATE = "date";
    private static final String COL_STATUT = "statut";

    public PresenceDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Création de la table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String req = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_STATUT + " TEXT)";
        db.execSQL(req);
    }

    // Mise à jour de la base
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Enregistre une présence/absence dans la base
     * @param nom Nom de l'étudiant
     * @param date Date de la présence
     * @param statut Statut (Présent/Absent)
     * @return ID de la nouvelle ligne insérée
     */
    public long enregistrerPresence(String nom, String date, String statut) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOM, nom);
        values.put(COL_DATE, date);
        values.put(COL_STATUT, statut);
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Récupère l'historique des présences pour un étudiant
     * @param nom Nom de l'étudiant
     * @return Liste des entrées de présence
     */
    public ArrayList<HashMap<String, String>> getHistorique(String nom) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Requête pour récupérer l'historique
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_DATE, COL_STATUT},
                COL_NOM + "=?",
                new String[]{nom},
                null, null,
                COL_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Création d'une entrée pour la liste
                HashMap<String, String> map = new HashMap<>();
                map.put("date", cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                map.put("statut", cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUT)));
                list.add(map);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    /**
     * Compte le nombre d'entrées pour un statut donné
     * @param nom Nom de l'étudiant
     * @param statut Statut à compter (Présent/Absent)
     * @return Nombre d'occurrences
     */
    public int compterParStatut(String nom, String statut) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME +
                " WHERE nom=? AND statut=?", new String[]{nom, statut});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Supprime une entrée de présence
     * @param nom Nom de l'étudiant
     * @param date Date à supprimer
     */
    public void supprimerPresence(String nom, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "nom=? AND date=?", new String[]{nom, date});
    }
}