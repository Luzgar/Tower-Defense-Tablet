package com.example.kevinduglue.towerdefensetablet.monsters;

import com.example.kevinduglue.towerdefensetablet.R;

/**
 * Created by kevinduglue on 08/02/2018.
 */

public enum MonstersImage {
    SKELETON(R.drawable.squelette , "skeleton"),
    WARRIOR(R.drawable.guerrier, "warrior"),
    THUNDER(R.drawable.magdamonster, "magdamonster"),
    CATERPILLAR(R.drawable.caterpillar,"caterpillar");

    private int imageRes;
    private String monsterName;

    MonstersImage(int imageRes, String monsterName)
    {
        this.imageRes = imageRes;
        this.monsterName = monsterName;
    }

    public int getImageRes()
    {
        return imageRes;
    }

    public String toString() {
        return monsterName;
    }

    public static MonstersImage getEnumByString(String text) {
        for (MonstersImage b : MonstersImage.values()) {
            if (b.monsterName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
