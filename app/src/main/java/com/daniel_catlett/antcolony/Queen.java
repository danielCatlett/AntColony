package com.daniel_catlett.antcolony;
import java.util.Random;

public class Queen extends Ant
{
    public Queen()
    {
        id = 0;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 73000;
    }

    @Override
    public int[] move(Tile[] tilesNearBy)
    {
        System.out.println("Queen tried to move, but failed!");
        return location;
    }

    public Tile[] filterViableTiles(Tile[] tiles)
    {
        Tile[] rv = new Tile[0];
        return rv;
    }
}
