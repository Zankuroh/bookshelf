package com.eip.bookshelf;

/**
 * Created by joly_i on 22/02/16.
 */

class Book
{
    private String  titre;
    private String  image;
    private String  auteur;
    private String  date;
    private String  genre;
    private String  resum;
    private Double  note;
    private Long    isbn;

    String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getResum() {
        return resum;
    }

    public void setResum(String resum) {
        this.resum = resum;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

}
