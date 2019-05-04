package com.daniel_catlett.antcolony;

public class Forager extends Ant
{
    public Forager(int idOfAnt)
    {
        id = idOfAnt;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 3650;
    }

    public Tile[] filterViableTiles(Tile[] tiles)
    {
        return tiles;
    }
}
