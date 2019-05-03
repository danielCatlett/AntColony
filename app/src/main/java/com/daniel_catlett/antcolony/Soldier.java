package com.daniel_catlett.antcolony;

public class Soldier extends Ant
{
    public Soldier(int idOfAnt)
    {
        id = idOfAnt;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 3650;
    }
}
