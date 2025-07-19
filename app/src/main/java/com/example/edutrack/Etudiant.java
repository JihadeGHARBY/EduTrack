package com.example.edutrack;

/**
 * Classe modèle représentant un étudiant avec ses informations
 */
public class Etudiant {
    private final String nom;
    private final int age;          // ici : semestre
    private final String ville;     // ici : module
    private final String genre;
    private final int image;        // id de ressource drawable

    public Etudiant(String nom, int age, String ville, String genre, int image) {
        this.nom = nom;
        this.age = age;
        this.ville = ville;
        this.genre = genre;
        this.image = image;
    }

    public String getNom()   { return nom; }
    public int    getAge()   { return age; }
    public String getVille() { return ville; }
    public String getGenre() { return genre; }
    public int    getImage() { return image; }
}
