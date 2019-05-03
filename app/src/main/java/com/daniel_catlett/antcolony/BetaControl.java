package com.daniel_catlett.antcolony;

public class BetaControl
{
    static Tile[][] board = new Tile[27][27];
    static int turn;

    public static void main(String[] args)
    {
        createBoard();
        printBoard();
    }

    //TODO: birth other ants
    private static boolean queenTurn(Queen q)
    {
        //eat food
        if(board[13][13].getFood() < 1)
        {
            return false;
        }
        else
        {
            board[13][13].takeFood();
        }

        q.growOlder();
        if(q.age >= q.maxAge)
        {
            return false;
        }

        return true;
    }

    private static int[] locationConversion(int location)
    {
        int[] rv = new int[2];
        rv[0] = location % 27;
        rv[1] = location / 27;
        return rv;
    }

    private static void createBoard()
    {
        //create environment

        for(int x = 0; x < 27; x++)
        {
            for(int y = 0; y < 27; y++)
            {
                boolean isEntrance = (x == 13 && y == 13);
                board[x][y] = new Tile(isEntrance);
            }
        }

        //set initial explored tiles
        for(int i = 12; i <= 14; i++)
        {
            for(int j = 12; j <= 14; j++)
            {
                board[i][j].exploreTile();
            }
        }
    }

    private static void printBoard()
    {
        for(int x = 0; x < 27; x++)
        {
            for(int y = 0; y < 27; y++)
            {
                Tile ct = board[x][y];
                String colonyEnt;
                String explored;
                String food;
                String pheromones;

                if(ct.isColonyEntrance())
                    colonyEnt = "Colony Entrance";
                else
                    colonyEnt = "Not Colony Entrance";

                if(ct.isExplored())
                    explored = "Tile is explored";
                else
                    explored = "Tile is not explored";

                food = "Tile has " + Integer.toString(ct.getFood()) + " food";

                pheromones = "Tile has " + Integer.toString(ct.getPheromones()) + " pheromones";
                System.out.println("Tile " + x + ", " + y
                        + "\n" + colonyEnt
                        + "\n" + explored
                        + "\n" + food
                        + "\n" + pheromones);
            }

            System.out.println("\n\n\n");
        }
    }
}
