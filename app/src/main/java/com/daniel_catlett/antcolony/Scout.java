package com.daniel_catlett.antcolony;

public class Scout extends Ant
{
    public Scout(int idOfAnt)
    {
        id = idOfAnt;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 3650;
        carryingFood = false;
    }

    public Tile[] filterViableTiles(Tile[] tiles)
    {
        return tiles;
    }

    public void addToHistory(Tile tile)
    {
        System.out.println("Scouts have a very poor memory");
    }

    public void removeFromHistory()
    {
        System.out.println("Scouts have a very poor memory");
    }
}
