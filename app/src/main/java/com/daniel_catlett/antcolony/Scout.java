package com.daniel_catlett.antcolony;

public class Scout extends Ant
{
    public Scout(int idOfAnt)
    {
        id = idOfAnt;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 3650;
    }
}
