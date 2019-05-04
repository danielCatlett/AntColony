package com.daniel_catlett.antcolony;

public class Soldier extends Ant
{
    public Soldier(int idOfAnt)
    {
        id = idOfAnt;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 3650;
        carryingFood = false;
    }

    public void addToHistory(Tile tile)
    {
        System.out.println("Soldiers have a very poor memory");
    }

    public void removeFromHistory()
    {
        System.out.println("Soldiers have a very poor memory");
    }

    public Tile[] filterViableTiles(Tile[] tiles)
    {
        Tile[] exploredTiles = new Tile[8];

        //filter out unexplored tiles
        for(int i = 0; i < 8; i++)
        {
            if(tiles[i] != null)
            {
                if(tiles[i].isExplored())
                {
                    exploredTiles[i] = tiles[i];
                }
            }
        }

        Tile[] rv = new Tile[8];
        boolean foundBala = false;
        for(int i = 0; i < 8; i++)
        {
            if(exploredTiles[i] != null)
            {
                if(exploredTiles[i].getBalas().size() != 0)
                {
                    rv[i] = exploredTiles[i];
                    foundBala = true;
                }
            }
        }

        if(foundBala)
        {
            return rv;
        }

        return exploredTiles;
    }
}
