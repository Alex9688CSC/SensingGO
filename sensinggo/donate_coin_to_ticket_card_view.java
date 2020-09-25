package edu.nctu.wirelab.sensinggo;

public class donate_coin_to_ticket_card_view {
    private String phase;
    private int image;
    private String number;

    public donate_coin_to_ticket_card_view() {
        super();
    }

    public donate_coin_to_ticket_card_view(String phase, int image, String number) {
        super();
        this.phase = phase;
        this.image = image;
        this.number = number;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String name) {
        this.number = number;
    }
}
