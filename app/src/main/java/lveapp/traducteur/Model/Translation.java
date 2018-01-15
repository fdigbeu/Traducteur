package lveapp.traducteur.Model;

/**
 * Created by Maranatha on 11/01/2018.
 */

public class Translation {
    public String text;
    public String language;

    public Translation(String text, String language) {
        this.text = text;
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
