package lveapp.traducteur.Model;

/**
 * Created by Maranatha on 15/01/2018.
 */

public class History {
    private int id;
    private String langDeparture;
    private String langArrivale;
    private String messageDeparture;
    private String messageArrivale;
    private String date;

    public History(int id, String langDeparture, String langArrivale, String messageDeparture, String messageArrivale, String date) {
        this.id = id;
        this.langDeparture = langDeparture;
        this.langArrivale = langArrivale;
        this.messageDeparture = messageDeparture;
        this.messageArrivale = messageArrivale;
        this.date = date;
    }

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\"," +
                "\"langDeparture\":\"" + langDeparture +
                "\",\"langArrivale\":\"" + langArrivale +
                "\",\"messageDeparture\":\"" + messageDeparture +
                "\",\"messageArrivale\":\"" + messageArrivale +
                "\",\"date\":\"" + date + "\"}";
    }

    public String getLangDeparture() {
        return langDeparture;
    }

    public void setLangDeparture(String langDeparture) {
        this.langDeparture = langDeparture;
    }

    public String getLangArrivale() {
        return langArrivale;
    }

    public void setLangArrivale(String langArrivale) {
        this.langArrivale = langArrivale;
    }

    public String getMessageDeparture() {
        return messageDeparture;
    }

    public void setMessageDeparture(String messageDeparture) {
        this.messageDeparture = messageDeparture;
    }

    public String getMessageArrivale() {
        return messageArrivale;
    }

    public void setMessageArrivale(String messageArrivale) {
        this.messageArrivale = messageArrivale;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
