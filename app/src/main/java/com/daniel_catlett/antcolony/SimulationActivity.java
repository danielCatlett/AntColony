package com.daniel_catlett.antcolony;

import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SimulationActivity extends AppCompatActivity
{
    //UI elements
    TextView dayText, dayCounter, turnText, turnCounter, gridDisplayedText;
    TextView queenTextView, foodTextView, foragersTextView, soldiersTextView;
    TextView pheromonesTextView, scoutsTextView, balasTextView;
    Button turnAdvanceButton, zoomButton;
    ImageButton upButton, rightButton, downButton, leftButton, pauseButton, playButton, fastPlayButton;
    ArrayList<Guideline> guidelines;
    ArrayList<ArrayList<ImageButton>> buttons;
    GridListener gridListener;

    //simulation objects
    private Tile[][] board = new Tile[27][27];
    private int turn;
    private int regionDisplayed;
    private int[] selectedTile;
    private ArrayList<Ant> ants = new ArrayList<Ant>();
    ArrayList<Integer> deadAnts = new ArrayList<Integer>();
    boolean simulationStillActive;

    private Timer timer;

    /*
     * Set up the starting environment for the simulation
     * Create all the tiles, fill with starting materials and ants
     * Begin tracking of region on display
     */
    private void createBoard()
    {
        simulationStillActive = true;

        for(int y = 0; y < 27; y++)
        {
            for(int x = 0; x < 27; x++)
            {
                boolean isEntrance = (x == 13 && y == 13);
                board[y][x] = new Tile(isEntrance, y, x);
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

        regionDisplayed = 4; //center region

        //initial ants
        Queen queenie = new Queen();
        ants.add(queenie);
        for(int i = 0; i < 50; i++)
        {
            Forager forager = new Forager(nextId());
            ants.add(forager);
            board[13][13].addForager(forager.id);
        }
        for(int i = 0; i < 4; i++)
        {
            Scout scout = new Scout(nextId());
            ants.add(scout);
            board[13][13].addScout(scout.id);
        }
        for(int i = 0; i < 10; i++)
        {
            Soldier soldier = new Soldier(nextId());
            ants.add(soldier);
            board[13][13].addSoldier(soldier.id);
        }
    }

    private void displayBoard()
    {
        switch(regionDisplayed)
        {
            case 0:
                gridDisplayedText.setText("Displaying Upper Left Grid");
                break;
            case 1:
                gridDisplayedText.setText("Displaying Upper Center Grid");
                break;
            case 2:
                gridDisplayedText.setText("Displaying Upper Right Grid");
                break;
            case 3:
                gridDisplayedText.setText("Displaying Middle Left Grid");
                break;
            case 4:
                gridDisplayedText.setText("Displaying Middle Center Grid");
                break;
            case 5:
                gridDisplayedText.setText("Displaying Middle Right Grid");
                break;
            case 6:
                gridDisplayedText.setText("Displaying Lower Left Grid");
                break;
            case 7:
                gridDisplayedText.setText("Displaying Lower Center Grid");
                break;
            default:
                gridDisplayedText.setText("Displaying Lower Right Grid");
        }

        int startX = 0;
        if(regionDisplayed % 3 == 1)
        {
            startX = 9;
        }
        else if(regionDisplayed % 3 == 2)
        {
            startX = 18;
        }

        int startY = 0;
        if(regionDisplayed > 2 && regionDisplayed < 6)
        {
            startY = 9;
        }
        else if(regionDisplayed  > 5)
        {
            startY = 18;
        }

        int defaultStartX = startX;
        for(int y = 0; y < 9; y++)
        {
            startX = defaultStartX;
            for(int x = 0; x < 9; x++)
            {
                if(board[startY][startX].containsAnAnt())
                {
                    buttons.get(y).get(x).setImageResource(R.drawable.ic_android_black_24dp);
                }
                else if(!board[startY][startX].isExplored())
                {
                    buttons.get(y).get(x).setImageResource(R.drawable.unexploredsquare);
                }
                else
                {
                    buttons.get(y).get(x).setImageResource(R.drawable.exploredsquare);
                }
                startX++;
            }
            startY++;
        }
    }

    private void updateDisplays()
    {
        Tile copiedTile = board[selectedTile[0]][selectedTile[1]];

        if(copiedTile.isColonyEntrance())
        {
            queenTextView.setText("Queen is Present");
        }
        else
        {
            queenTextView.setText("Queen is not present");
        }

        String food = "Food: ";
        food += Integer.toString(copiedTile.getFood());
        foodTextView.setText(food);

        String pheromones = "Pheromones: ";
        pheromones += Integer.toString(copiedTile.getPheromones());
        pheromonesTextView.setText(pheromones);

        String foragers = "Foragers: ";
        foragers += Integer.toString(copiedTile.getNumForagers());
        foragersTextView.setText(foragers);

        String scouts = "Scouts: ";
        scouts += Integer.toString(copiedTile.getNumScouts());
        scoutsTextView.setText(scouts);

        String soldiers = "Soldiers: ";
        soldiers += Integer.toString(copiedTile.getNumSoldiers());
        soldiersTextView.setText(soldiers);

        String balas = "Balas: ";
        balas += Integer.toString(copiedTile.getNumBalas());
        balasTextView.setText(balas);
    }

    private void simulationProcedure()
    {
        if(!simulationStillActive)
        {
            Context context = getApplicationContext();
            CharSequence text = "Simulation over, the Queen is dead";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return;
        }

        turn++;
        turnCounter.setText(Integer.toString(turn % 10));
        dayCounter.setText(Integer.toString(turn / 10));

        int antsCurrentlyAlive = ants.size(); //ants can't be born and act on same turn

        //spawn bala
        Random rngesus = new Random();
        if(rngesus.nextInt(100) < 3)
        {
            Bala bala = new Bala(nextId());
            ants.add(bala);
            int[] balaLocation = bala.getLocation();
            board[balaLocation[0]][balaLocation[1]].addBala(bala.id);
        }

        //half pheromones
        if(turn % 10 == 0)
        {
            for(int y = 0; y < 27; y++)
            {
                for(int x = 0; x < 27; x++)
                {
                    board[y][x].halfPheromones();
                }
            }
        }

        for(int i = 0; i < Math.min(antsCurrentlyAlive, ants.size()); i++)
        {
            Ant ant = ants.get(i);
            if(ant.getClass() == Queen.class)
            {
                boolean antSurvived = queenProcedure(i);
                if(!antSurvived)
                {
                    deadAnts.add(ant.id);
                    break;
                }
            }
            else if(ant.getClass() == Forager.class)
            {
//                boolean antSurvived = foragerProcedure(i);
//                if(!antSurvived)
//                {
//                    deadAnts.add(ant.id);
//                }
            }
            else if(ant.getClass() == Scout.class)
            {
                boolean antSurvived = scoutProcedure(i);
                if(!antSurvived)
                {
                    deadAnts.add(ant.id);
                }
            }
            else if(ant.getClass() == Soldier.class)
            {
                boolean antSurvived = soldierProcedure(i);
                if(!antSurvived)
                {
                    deadAnts.add(ant.id);
                }
            }
            else
            {
                boolean antSurvived = balaProcedure(i);
                if(!antSurvived)
                {
                    deadAnts.add(ant.id);
                }
            }
        }
        //kill ants scheduled for death
        for(int i = 0; i < deadAnts.size(); i++)
        {
            killAnt(deadAnts.get(i));
        }

        displayBoard();
        updateDisplays();
        deadAnts.clear();

        //check if queen is still alive
        if(ants.get(0).getClass() != Queen.class)
        {
            simulationStillActive = false;
        }
    }

    private int nextId()
    {
        return ants.get(ants.size() - 1).id + 1;
    }

    private boolean queenProcedure(int antIndex)
    {
        Ant queen = ants.get(antIndex);
        //queen eats
        if(board[13][13].getFood() != 0)
        {
            board[13][13].takeFood();
        }
        else
        {
            return false;
        }

        //queen lays an egg
        if(turn % 10 == 0)
        {
            Random rngesus = new Random();

            int newAnt = rngesus.nextInt(4);
            if(newAnt == 0 || newAnt == 1)
            {
                Forager forager = new Forager(nextId());
                ants.add(forager);
                board[13][13].addForager(forager.id);
            }
            else if(newAnt == 2)
            {
                Scout scout = new Scout(nextId());
                ants.add(scout);
                board[13][13].addScout(scout.id);
            }
            else
            {
                Soldier soldier = new Soldier(nextId());
                ants.add(soldier);
                board[13][13].addSoldier(soldier.id);
            }
        }

        //age
        queen.growOlder();
        if(queen.shouldDie())
        {
            return false;
        }

        return true;
    }

    private boolean foragerProcedure(int antIndex)
    {
        Ant forager = ants.get(antIndex);

        //move
        int locX = forager.getLocation()[1];
        int locY = forager.getLocation()[0];
        Tile[] viableTiles = getAllAdjacentTiles(forager.getLocation());
        viableTiles = forager.filterViableTiles(viableTiles);

        int[] newLoc = forager.move(viableTiles);
        board[locY][locX].removeForager(forager.id);
        Tile newTile = board[newLoc[0]][newLoc[1]];
        newTile.addForager(forager.id);

        if(!forager.isCarryingFood())
        {
            forager.addToHistory(newTile);
            if(newTile.getFood() > 0 && !newTile.isColonyEntrance())
            {
                newTile.takeFood();
                forager.carryingFood = true;
                if(newTile.getPheromones() < 1000)
                {
                    newTile.addPhermones();
                }
            }
        }
        else
        {
            if(!newTile.isColonyEntrance())
            {
                forager.removeFromHistory();
                if(newTile.getPheromones() < 1000)
                {
                    newTile.addPhermones();
                }
            }
            else
            {
                forager.carryingFood = false;
                newTile.addFood();
            }
        }

        //age
        forager.growOlder();
        if(ants.get(antIndex).shouldDie())
        {
            return false;
        }

        return true;
    }

    private boolean scoutProcedure(int antIndex)
    {
        Ant scout = ants.get(antIndex);

        //move
        int locX = scout.getLocation()[1];
        int locY = scout.getLocation()[0];
        Tile[] viableTiles = getAllAdjacentTiles(scout.getLocation());

        int[] newLoc = scout.move(viableTiles);
        board[locY][locX].removeScout(scout.id);
        board[newLoc[0]][newLoc[1]].addScout(scout.id);

        if(!board[newLoc[0]][newLoc[1]].isExplored())
        {
            board[newLoc[0]][newLoc[1]].exploreTile();
        }

        //age
        scout.growOlder();
        if(ants.get(antIndex).shouldDie())
        {
            return false;
        }

        return true;
    }

    private boolean soldierProcedure(int antIndex)
    {
        Ant soldier = ants.get(antIndex);

        //move
        int locX = soldier.getLocation()[1];
        int locY = soldier.getLocation()[0];
        if(board[locY][locX].getBalas().size() == 0)
        {
            Tile[] viableTiles = getAllAdjacentTiles(soldier.getLocation());
            viableTiles = soldier.filterViableTiles(viableTiles);

            int[] newLoc = soldier.move(viableTiles);
            board[locY][locX].removeSoldier(soldier.id);
            board[newLoc[0]][newLoc[1]].addSoldier(soldier.id);
        }
        //attack
        else
        {
            Random rngesus = new Random();
            int attack = rngesus.nextInt(2);
            if(attack == 0)
            {
                int deadBalaId = board[locY][locX].getBalas().get(0);
                if(deadBalaId < soldier.id)
                {
                    deadAnts.add(deadBalaId);
                }
                else
                {
                    killAnt(deadBalaId);
                }
            }
        }

        //age
        soldier.growOlder();
        if(ants.get(antIndex).shouldDie())
        {
            return false;
        }

        return true;
    }

    private boolean balaProcedure(int antIndex)
    {
        Ant bala = ants.get(antIndex);

        //move
        int locX = bala.getLocation()[1];
        int locY = bala.getLocation()[0];
        if(!board[locY][locX].containsAFriendly())
        {
            Tile[] viableTiles = getAllAdjacentTiles(bala.getLocation());

            int[] newLoc = bala.move(viableTiles);
            board[locY][locX].removeBala(bala.id);
            board[newLoc[0]][newLoc[1]].addBala(bala.id);
        }
        //attack
        else
        {
            Random rngesus = new Random();
            int attack = rngesus.nextInt(2);
            if(attack == 0)
            {
                int deadFriendlyId;
                Tile tile = board[locY][locX];
                if(tile.getNumSoldiers() != 0)
                {
                    deadFriendlyId = tile.getSoldiers().get(0);
                }
                else if(tile.getNumScouts() != 0)
                {
                    deadFriendlyId = tile.getScouts().get(0);
                }
                else if(tile.getNumForagers() != 0)
                {
                    deadFriendlyId = tile.getForagers().get(0);
                }
                else
                {
                    deadFriendlyId = 0;
                }

                if(deadFriendlyId < bala.id)
                {
                    deadAnts.add(deadFriendlyId);
                }
                else
                {
                    killAnt(deadFriendlyId);
                }
            }
        }

        //age
        ants.get(antIndex).growOlder();
        if(ants.get(antIndex).shouldDie())
        {
            return false;
        }

        return true;
    }

    //binary search for ant in list of all ants
    //once found, remove it from it's tile,
    //then remove it from list
    private void killAnt(int antId)
    {
        int indexOfDeadAnt = -1;

        int low = 0;
        int mid;
        int high = ants.size() - 1;

        //if ant is the upper or lower bounds
        if(ants.get(low).id == antId)
            indexOfDeadAnt = low;
        else if(ants.get(high).id == antId)
            indexOfDeadAnt = high;

        while(high - low > 1)
        {
            mid = (low + high) / 2;

            //if mid is too low
            if(ants.get(mid).id < antId)
            {
                low = mid;
            }

            //if the mid is too high
            else if(ants.get(mid).id > antId)
            {
                high = mid;
            }

            //if the mid is right
            else
            {
                indexOfDeadAnt = mid;
                break;
            }
        }

        //there is a return high here in my binarysearch assignment, idk why
        //ignoring it for now

        Ant deadAnt = ants.get(indexOfDeadAnt);

        //remove from it's tile
        int[] loc = deadAnt.getLocation();
        Tile tile = board[loc[0]][loc[1]];
        if(deadAnt.getClass() == Forager.class)
        {
            tile.removeForager(antId);
        }
        else if(deadAnt.getClass() == Scout.class)
        {
            tile.removeScout(antId);
        }
        else if(deadAnt.getClass() == Soldier.class)
        {
            tile.removeSoldier(antId);
        }
        else if(deadAnt.getClass() == Bala.class)
        {
            tile.removeBala(antId);
        }

        //drop food if applicable
        if(deadAnt.isCarryingFood())
        {
            tile.addFood();
        }

        //remove from general ant list
        ants.remove(indexOfDeadAnt);
    }

    private Tile[] getAllAdjacentTiles(int[] location)
    {
        Tile[] rv = new Tile[8];
        //add upper left
        if(!(location[0] == 0) && !(location[1] == 0))
        {
            rv[0] = board[location[0] - 1][location[1] - 1];
        }
        //add upper center
        if(!(location[0] == 0))
        {
            rv[1] = board[location[0] - 1][location[1]];
        }
        //add upper right
        if(!(location[0] == 0) && !(location[1] == 26))
        {
            rv[2] = board[location[0] - 1][location[1] + 1];
        }
        //add center left
        if(!(location[1] == 0))
        {
            rv[3] = board[location[0]][location[1] - 1];
        }
        //add center right
        if(!(location[1] == 26))
        {
            rv[4] = board[location[0]][location[1] + 1];
        }
        //add bottom left
        if(!(location[0] == 26) && !(location[1] == 0))
        {
            rv[5] = board[location[0] + 1][location[1] - 1];
        }
        //add bottom center
        if(!(location[0] == 26))
        {
            rv[6] = board[location[0] + 1][location[1]];
        }
        //add bottom right
        if(!(location[0] == 26) && !(location[1] == 26))
        {
            rv[7] = board[location[0] + 1][location[1] + 1];
        }

        return rv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        dayText = findViewById(R.id.dayText);
        dayCounter = findViewById(R.id.dayCounter);
        turnText = findViewById(R.id.turnText);
        turnCounter = findViewById(R.id.turnCounter);
        gridDisplayedText = findViewById(R.id.gridDisplayedText);
        queenTextView = findViewById(R.id.queenTextView);
        foodTextView = findViewById(R.id.foodTextView);
        pheromonesTextView = findViewById(R.id.pheromonesTextView);
        foragersTextView = findViewById(R.id.foragersTextView);
        scoutsTextView = findViewById(R.id.scoutsTextView);
        soldiersTextView = findViewById(R.id.soldiersTextView);
        balasTextView = findViewById(R.id.balasTextView);
        turnAdvanceButton = findViewById(R.id.turnAdvanceButton);
        turnAdvanceButton.setOnClickListener(turnClick);
        zoomButton = findViewById(R.id.zoomButton);
        upButton = findViewById(R.id.upArrowButton);
        upButton.setOnClickListener(upClick);
        rightButton = findViewById(R.id.rightArrowButton);
        rightButton.setOnClickListener(rightClick);
        downButton = findViewById(R.id.downArrowButton);
        downButton.setOnClickListener(downClick);
        leftButton = findViewById(R.id.leftArrowButton);
        leftButton.setOnClickListener(leftClick);
        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(pauseClick);
        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(playClick);
        fastPlayButton = findViewById(R.id.fastPlayButton);
        fastPlayButton.setOnClickListener(fastPlayClick);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Fieldwork.otf");
        dayText.setTypeface(font);
        dayCounter.setTypeface(font);
        turnText.setTypeface(font);
        turnCounter.setTypeface(font);
        turnAdvanceButton.setTypeface(font);
        zoomButton.setTypeface(font);

        //get all the guidelines to use
        guidelines = new ArrayList<Guideline>();
        Guideline guideline0 = findViewById(R.id.guideline0);
        Guideline guideline1 = findViewById(R.id.guideline1);
        Guideline guideline2 = findViewById(R.id.guideline2);
        Guideline guideline3 = findViewById(R.id.guideline3);
        Guideline guideline4 = findViewById(R.id.guideline4);
        Guideline guideline5 = findViewById(R.id.guideline5);
        Guideline guideline6 = findViewById(R.id.guideline6);
        Guideline guideline7 = findViewById(R.id.guideline7);
        Guideline guideline8 = findViewById(R.id.guideline8);
        Guideline guideline9 = findViewById(R.id.guideline9);
        guidelines.add(guideline0);
        guidelines.add(guideline1);
        guidelines.add(guideline2);
        guidelines.add(guideline3);
        guidelines.add(guideline4);
        guidelines.add(guideline5);
        guidelines.add(guideline6);
        guidelines.add(guideline7);
        guidelines.add(guideline8);
        guidelines.add(guideline9);

        //set up the grid of buttons
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        buttons = new ArrayList<ArrayList<ImageButton>>();
        ConstraintSet constraintSet = new ConstraintSet();

        for(int y = 0; y < 9; y++)
        {
            buttons.add(new ArrayList<ImageButton>());
            for(int x = 0; x < 9; x++)
            {
                //create the button, and assign it's image to it
                ImageButton imageButton = new ImageButton(SimulationActivity.this);
                imageButton.setImageResource(R.drawable.unexploredsquare);
                imageButton.setBackground(this.getDrawable(R.drawable.tilebackground));
                imageButton.setPadding(0,0,0,0);
                imageButton.setId(View.generateViewId());
                gridListener = new GridListener(x, y);
                imageButton.setOnClickListener(gridListener);
                constraintLayout.addView(imageButton);
                buttons.get(y).add(imageButton);

                //add the constraints for the button
                constraintSet.clone(constraintLayout);
                constraintSet.connect(imageButton.getId(), ConstraintSet.LEFT, guidelines.get(x).getId(), ConstraintSet.RIGHT, 0);
                constraintSet.connect(imageButton.getId(), ConstraintSet.RIGHT, guidelines.get(x + 1).getId(), ConstraintSet.LEFT, 0);
                constraintSet.setDimensionRatio(imageButton.getId(), "1:1");
                if(y == 0)
                {
                    constraintSet.connect(imageButton.getId(), ConstraintSet.TOP, upButton.getId(), ConstraintSet.BOTTOM, 16);
                }
                else
                {
                    constraintSet.connect(imageButton.getId(), ConstraintSet.TOP, buttons.get(y - 1).get(0).getId(), ConstraintSet.BOTTOM,0);
                }
                constraintSet.applyTo(constraintLayout);
            }
        }

        //constrain arrow buttons
        constraintSet.connect(leftButton.getId(), ConstraintSet.TOP, buttons.get(0).get(0).getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(leftButton.getId(), ConstraintSet.BOTTOM, buttons.get(buttons.size() - 1).get(0).getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(rightButton.getId(), ConstraintSet.TOP, buttons.get(0).get(0).getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(rightButton.getId(), ConstraintSet.BOTTOM, buttons.get(buttons.size() - 1).get(0).getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(downButton.getId(), ConstraintSet.TOP, buttons.get(buttons.size() - 1).get(0).getId(), ConstraintSet.BOTTOM, 16);
        constraintSet.applyTo(constraintLayout);

        selectedTile = new int[2];
        selectedTile[0] = 13;
        selectedTile[1] = 13;

        createBoard();
        displayBoard();
    }

    public class GridListener implements View.OnClickListener
    {
        int x;
        int y;

        public GridListener(int xVar, int yVar)
        {
            x = xVar;
            y = yVar;
        }

        @Override
        public void onClick(View v)
        {
            int selectedX = x;
            int selectedY = y;

            if(regionDisplayed % 3 == 1)
            {
                selectedX += 9;
            }
            else if(regionDisplayed % 3 == 2)
            {
                selectedX += 18;
            }

            if(regionDisplayed < 6 && regionDisplayed > 2)
            {
                selectedY += 9;
            }
            else if(regionDisplayed > 5)
            {
                selectedY += 18;
            }

            selectedTile[0] = selectedY;
            selectedTile[1] = selectedX;

            updateDisplays();
        }
    }

    ImageButton.OnClickListener upClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(regionDisplayed > 2)
            {
                regionDisplayed -= 3;
                displayBoard();
            }
        }
    };

    ImageButton.OnClickListener rightClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(regionDisplayed % 3 != 2)
            {
                regionDisplayed++;
                displayBoard();
            }
        }
    };

    ImageButton.OnClickListener downClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(regionDisplayed < 6)
            {
                regionDisplayed += 3;
                displayBoard();
            }
        }
    };

    ImageButton.OnClickListener leftClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(regionDisplayed % 3 != 0)
            {
                regionDisplayed--;
                displayBoard();
            }
        }
    };

    Button.OnClickListener turnClick = new Button.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            simulationProcedure();
        }
    };

    ImageButton.OnClickListener pauseClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            turnAdvanceButton.setEnabled(true);
            timer.cancel();
        }
    };

    ImageButton.OnClickListener playClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            turnAdvanceButton.setEnabled(false);
            if(timer != null)
            {
                timer.cancel();
            }

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    timerMethod();
                }
            }, 0, 1000);
        }
    };

    ImageButton.OnClickListener fastPlayClick = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            turnAdvanceButton.setEnabled(false);
            if(timer != null)
            {
                timer.cancel();
            }
            
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    timerMethod();
                }
            }, 0, 250);
        }
    };

    private void timerMethod()
    {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable()
    {
        @Override
        public void run()
        {
            simulationProcedure();
        }
    };
}
