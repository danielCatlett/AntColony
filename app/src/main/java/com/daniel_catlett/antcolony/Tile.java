package com.daniel_catlett.antcolony;
import java.util.ArrayList;
import java.util.Random;

public class Tile
{
    private boolean colonyEntrance;
    private boolean explored;
    private int food;
    private int pheromones;
    private int[] coordinates;

    //arrays with the IDs of the ants in the master ant arraylist
    private ArrayList<Integer> foragers = new ArrayList<Integer>();
    private ArrayList<Integer> scouts = new ArrayList<Integer>();
    private ArrayList<Integer> soldiers = new ArrayList<Integer>();
    private ArrayList<Integer> balas = new ArrayList<Integer>();

    public Tile(boolean isEntrance, int yCoord, int xCoord)
    {
        colonyEntrance = isEntrance;
        explored = false;

        if(colonyEntrance)
        {
            food = 1000;
        }
        else
        {
            Random rngesus = new Random();
            int foodHere = rngesus.nextInt(4);
            if(foodHere == 0)
            {
                food = rngesus.nextInt(500) + 501;
            }
            else
            {
                food = 0;
            }
        }
        pheromones = 0;
    }

    public boolean isColonyEntrance()
    {
        return colonyEntrance;
    }

    public int[] getCoordinates()
    {
        return coordinates;
    }

    public void exploreTile()
    {
        explored = true;
    }

    public boolean isExplored()
    {
        return explored;
    }

    public void addFood()
    {
        food++;
    }

    public void takeFood()
    {
        food--;
    }

    public int getFood()
    {
        return food;
    }

    public int getPheromones()
    {
        return pheromones;
    }

    public void addPhermones()
    {
        pheromones += 10;
    }

    public void halfPheromones()
    {
        pheromones /= 2;
    }

    public boolean containsAnAnt()
    {
        if(colonyEntrance)
            return true;
        if(foragers.size() > 0)
            return true;
        if(scouts.size() > 0)
            return true;
        if(soldiers.size() > 0)
            return true;
        if(balas.size() > 0)
            return true;

        return false;
    }

    public boolean containsAFriendly()
    {
        if(colonyEntrance)
            return true;
        if(foragers.size() > 0)
            return true;
        if(scouts.size() > 0)
            return true;
        if(soldiers.size() > 0)
            return true;

        return false;
    }

    public ArrayList<Integer> getForagers()
    {
        return foragers;
    }

    public void addForager(int index)
    {
        foragers.add(index);
    }

    public void removeForager(int antId)
    {
        for(int i = 0; i < foragers.size(); i++)
        {
            if(foragers.get(i) == antId)
            {
                foragers.remove(i);
                break;
            }
        }
    }

    public int getNumForagers()
    {
        return foragers.size();
    }

    public ArrayList<Integer> getScouts()
    {
        return scouts;
    }

    public void addScout(int antId)
    {
        scouts.add(antId);
    }

    public void removeScout(int antId)
    {
        for(int i = 0; i < scouts.size(); i++)
        {
            if(scouts.get(i) == antId)
            {
                scouts.remove(i);
                break;
            }
        }
    }

    public int getNumScouts()
    {
        return scouts.size();
    }

    public ArrayList<Integer> getSoldiers()
    {
        return soldiers;
    }

    public void addSoldier(int antId)
    {
        soldiers.add(antId);
    }

    public void removeSoldier(int antId)
    {
        for(int i = 0; i < soldiers.size(); i++)
        {
            if(soldiers.get(i) == antId)
            {
                soldiers.remove(i);
                break;
            }
        }
    }

    public int getNumSoldiers()
    {
        return soldiers.size();
    }

    public ArrayList<Integer> getBalas()
    {
        return balas;
    }

    public void addBala(int antId)
    {
        balas.add(antId);
    }

    public void removeBala(int antId)
    {
        for(int i = 0; i < balas.size(); i++)
        {
            if(balas.get(i) == antId)
            {
                balas.remove(i);
                break;
            }
        }
    }

    public int getNumBalas()
    {
        return balas.size();
    }
}
