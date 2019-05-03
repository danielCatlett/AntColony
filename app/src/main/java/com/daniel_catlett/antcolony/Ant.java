package com.daniel_catlett.antcolony;

import java.util.Random;

public abstract class Ant
{
    protected int id;
    protected int[] location;
    protected int age;
    protected int maxAge;

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

    public int[] getLocation()
    {
        return location;
    }

    public int[] move()
    {
        Random rngesus = new Random();
        int direction;
        //if on top row
        if(location[0] == 0)
        {
            do
            {
                direction = rngesus.nextInt(8);
            } while(direction < 3);
        }
        //if on left column
        else if(location[1] == 0)
        {
            do
            {
                direction = rngesus.nextInt(8);
            } while(direction == 0 || direction == 3 || direction == 5);
        }
        //if on right column
        else if(location[1] == 26)
        {
            do
            {
                direction = rngesus.nextInt(8);
            } while(direction == 2 || direction == 4 || direction == 7);
        }
        //if on bottom row
        else if(location[0] == 26)
        {
            do
            {
                direction = rngesus.nextInt(8);
            } while(direction > 4);
        }
        //if not on an edge
        else
        {
            direction = rngesus.nextInt(8);
        }

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
