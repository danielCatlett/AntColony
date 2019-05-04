package com.daniel_catlett.antcolony;

import java.util.ArrayList;

public class Forager extends Ant
{
    public Forager(int idOfAnt)
    {
        id = idOfAnt;
        location = new int[] {13, 13};
        age = 0;
        maxAge = 3650;
        carryingFood = false;
    }

    public Tile[] filterViableTiles(Tile[] tiles)
    {
        if(!carryingFood)
        {
            //filter out unexplored tiles
            Tile[] exploredTiles = new Tile[8];
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

            //filter out most recent tile
            //TODO: FIGURE OUT INFINITE LOOPS
            //start by checking there are multiple possible tiles
            int validChoices = 0;
            Tile[] noBackTracking = new Tile[8];
            for(int i = 0; i < 8; i++)
            {
                if(exploredTiles[i] != null)
                {
                    validChoices++;
                }
                if(validChoices > 1)
                    break;
            }
            //if there are multiple valid tiles, filter out the most recent tile
            if(validChoices > 1)
            {
                boolean atLeastOneInserted = false;
                for(int i = 0; i < 8; i++)
                {
                    if(exploredTiles[i] != null)
                    {
                        boolean invalid = false;
                        for(int j = 0; j < movementHistory.size(); j++)
                        {
                            if(sameTile(exploredTiles[i], movementHistory.get(j)))
                            {
                                invalid = true;
                            }
                        }
                        if(!invalid)
                        {
                            noBackTracking[i] = exploredTiles[i];
                            atLeastOneInserted = true;
                        }
                    }
                }
                //if all have been visited, make sure at least the most recent is inelligible
                if(!atLeastOneInserted)
                {
                    for(int i = 0; i < 8; i++)
                    {
                        if(exploredTiles[i] != null)
                        {
                            if(!sameTile(exploredTiles[i], movementHistory.get(0)))
                            {
                                noBackTracking[i] = exploredTiles[i];
                            }
                        }
                    }
                }

            }
            //otherwise return the one choice
            else
            {
                return exploredTiles;
            }

            //find the tile(s) with the most pheromones
            int maxPheromones = 0;
            ArrayList<Integer> pheromoneTileIndices = new ArrayList<Integer>();
            for(int i = 0; i < 8; i++)
            {
                if(noBackTracking[i] != null)
                {
                    if(noBackTracking[i].getPheromones() > maxPheromones)
                    {
                        maxPheromones = noBackTracking[i].getPheromones();
                        pheromoneTileIndices.clear();
                        pheromoneTileIndices.add(i);
                    }
                    else if(noBackTracking[i].getPheromones() == maxPheromones)
                    {
                        pheromoneTileIndices.add(i);
                    }
                }
            }

            Tile[] pheromoneTiles = new Tile[8];
            for(int i = 0; i < 8; i++)
            {
                if(pheromoneTileIndices.contains(i))
                {
                    pheromoneTiles[i] = noBackTracking[i];
                }
            }

            return pheromoneTiles;
        }
        else
        {
            Tile[] rv = new Tile[8];
            for(int i = 0; i < 8; i++)
            {
                if(tiles[i] != null)
                {
                    int historyX = movementHistory.get(0).getCoordinates()[1];
                    int historyY = movementHistory.get(0).getCoordinates()[0];

                    int checkingX = tiles[i].getCoordinates()[1];
                    int checkingY = tiles[i].getCoordinates()[0];

                    if(historyX == checkingX && historyY == checkingY)
                    {
                        rv[i] = tiles[i];
                    }
                }
            }

            return rv;
        }
    }

    private boolean sameTile(Tile tile1, Tile tile2)
    {
        int[] previousLoc = tile1.getCoordinates();
        int[] possibleLoc = tile2.getCoordinates();

        if(!(previousLoc[0] == possibleLoc[0] && previousLoc[1] == possibleLoc[1]))
        {
            return false;
        }

        return true;
    }

    public void addToHistory(Tile tile)
    {
        movementHistory.add(0, tile);
    }

    public void removeFromHistory()
    {
        movementHistory.remove(0);
    }
}
