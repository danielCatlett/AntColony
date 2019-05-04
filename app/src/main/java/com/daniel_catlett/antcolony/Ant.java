package com.daniel_catlett.antcolony;

import java.util.ArrayList;
import java.util.Random;

public abstract class Ant
{
    protected int id;
    protected int[] location;
    protected int age;
    protected int maxAge;
    protected boolean carryingFood;
    protected ArrayList<Tile> movementHistory;

    public void growOlder()
    {
        age++;
    }

    public boolean shouldDie()
    {
        if(age < maxAge)
        {
            return false;
        }
        return true;
    }

    abstract Tile[] filterViableTiles(Tile[] tiles);

    abstract void addToHistory(Tile tile);

    abstract void removeFromHistory();

    public boolean isCarryingFood()
    {
        return carryingFood;
    }

    public int[] getLocation()
    {
        return location;
    }

    //generic move method randomly picks direction without consideration
    public int[] move(Tile[] tilesNearBy)
    {
        Random rngesus = new Random();
        int direction;

        //pick a random direction, and verify it exists in the array
        do
        {
            direction = rngesus.nextInt(8);
        } while(tilesNearBy[direction] == null);

        //move up left
        if(direction == 0)
        {
            location[0] -= 1;
            location[1] -= 1;
        }
        //move up
        else if(direction == 1)
        {
            location[0] -= 1;
        }
        //move up right
        else if(direction == 2)
        {
            location[0] -= 1;
            location[1] += 1;
        }
        //move left
        else if(direction == 3)
        {
            location[1]--;
        }
        //move right
        else if(direction == 4)
        {
            location[1]++;
        }
        //move down left
        else if(direction == 5)
        {
            location[0] += 1;
            location[1] -= 1;
        }
        //move down
        else if(direction == 6)
        {
            location[0] += 1;
        }
        //move down right
        else
        {
            location[0] += 1;
            location[1] += 1;
        }

        return location;
    }
}