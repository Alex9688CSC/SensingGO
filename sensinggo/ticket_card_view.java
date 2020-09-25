package edu.nctu.wirelab.sensinggo;

public class ticket_card_view {
    private int logo;
    private int coin_logo;
    private String name;
    private int money;
    private String logoURL;
    private int width;
    public ticket_card_view() {
        super();
    }

    public ticket_card_view(int coin_logo, String name, int money, String logoURL, int width) {
        super();
        this.logo = logo;
        this.coin_logo = coin_logo;
        this.name = name;
        this.money = money;
        this.logoURL = logoURL;
        this.width = width;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int id) {
        this.logo = logo;
    }

    public int getCoin_logo() {
        return coin_logo;
    }

    public void setCoin_logo(int image) {
        this.coin_logo = coin_logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setlogoURL() {this.logoURL = logoURL;}

    public String getlogoURL() {return logoURL;}

    public void setMoney(int money) {
        this.money = money;
    }

    public int getWidth() {
        return width;
    }
}
