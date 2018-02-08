package com.example.kevinduglue.towerdefensetablet.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevinduglue.towerdefensetablet.HomeActivity;
import com.example.kevinduglue.towerdefensetablet.R;
import com.example.kevinduglue.towerdefensetablet.monsters.Monster;

import java.util.ArrayList;

/**
 * Created by kevinduglue on 25/01/2018.
 */

public class CustomAdapter extends BaseAdapter {
    HomeActivity c;
    ArrayList<Monster> monsters;

    public CustomAdapter(HomeActivity c, ArrayList<Monster> spacecrafts) {
        this.c = c;
        this.monsters = spacecrafts;
    }

    @Override
    public int getCount() {
        return monsters.size();
    }

    @Override
    public Monster getItem(int i) {
        return monsters.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.cardview_monster,viewGroup,false);
        }

        final Monster s= this.getItem(i);

        ImageView img= view.findViewById(R.id.monsterImg);
        TextView attack= view.findViewById(R.id.attack);
        TextView health= view.findViewById(R.id.health);
        TextView price = view.findViewById(R.id.price);

        attack.setText(Integer.toString(s.getAttack()));
        health.setText(Integer.toString(s.getHealth()));
        price.setText(Integer.toString(s.getPrice()));
        img.setImageResource(s.getImage());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!s.isDisable())
                c.summonMonster(s);
            }
        });

        if(s.isDisable()) {
            view.setForeground(new ColorDrawable(c.getResources().getColor(R.color.disable_cardview)));
        } else {
            view.setForeground(new ColorDrawable(c.getResources().getColor(R.color.enable_cardview)));
        }

        return view;
    }

    public void addMonster(Monster m) {
        this.monsters.add(m);
    }

    public Monster getMonster(int index) {
        return this.monsters.get(index);
    }

    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }
}