package com.example.kevinduglue.towerdefensetablet.monsters;

/**
 * Created by kevinduglue on 25/01/2018.
 */

public class Monster {

    private int id;
    private int health;
    private int attack;
    private int image;
    private String name;
    private int price;
    private boolean disable;

    public Monster(int id, String name, int health, int attack, int price, int image, boolean disable) {
        this.id = id;
        this.health = health;
        this.attack = attack;
        this.image = image;
        this.name = name;
        this.price = price;
        this.disable = disable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
