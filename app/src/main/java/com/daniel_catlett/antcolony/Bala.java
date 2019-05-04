package com.daniel_catlett.antcolony;

import java.util.Random;

public class Bala extends Ant
{
    public Bala(int idOfAnt)
    {
        id = idOfAnt;
        location = pickSpawnPoint();
        age = 0;
        maxAge = 3650;
        carryingFood = false;
    }

    public Tile[] filterViableTiles(Tile[] tiles)
    {
        return tiles;
    }


    private int[] pickSpawnPoint()
    {
        int[] rv = new int[2];
        Random rngesus = new Random();
        int spawnPt = rngesus.nextInt(103);
        //spawn on top row
        if(spawnPt < 27)
        {
            rv[0] = 0;
            rv[1] = spawnPt;
        }
        //spawn on left column
        else if(spawnPt < 52)
        {
            spawnPt -= 26;
            rv[0] = spawnPt;
            rv[1] = 0;
        }
        //spawn on right column
        else if(spawnPt < 77)
        {
            spawnPt -= 51;
            rv[0] = spawnPt;
            rv[1] = 26;
        }
        //spawn on bottom row
        else
        {
            spawnPt -= 77;
            rv[0] = 26;
            rv[1] = spawnPt;
        }

        return rv;
    }

    public void addToHistory(Tile tile)
    {
        System.out.println("Balas have a very poor memory");
    }

    public void removeFromHistory()
    {
        System.out.println("Balas have a very poor memory");
    }
}
