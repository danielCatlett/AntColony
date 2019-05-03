package com.daniel_catlett.antcolony;

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

import java.util.ArrayList;
import java.util.Random;

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

    /*
     * Set up the starting environment for the simulation
     * Create all the tiles, fill with starting materials and ants
     * Begin tracking of region on display
     */
    private void createBoard()
    {
        for(int y = 0; y < 27; y++)
        {
            for(int x = 0; x < 27; x++)
            {
                boolean isEntrance = (x == 13 && y == 13);
                board[y][x] = new Tile(isEntrance);
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
        turn++;
        turnCounter.setText(Integer.toString(turn % 10));
        dayCounter.setText(Integer.toString(turn / 10));

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

        ArrayList<Integer> deadAnts = new ArrayList<Integer>();

        for(int i = 0; i < ants.size(); i++)
        {
            if(ants.get(i).getClass() == Queen.class)
            {
                boolean antSurvived = queenProcedure(i);
                if(!antSurvived)
                {
                    deadAnts.add(i);
                    break;
                }
            }
//            else if(ants.get(i).getClass() == Forager.class)
//            {
//                foragerProcedure(i);
//            }
//            else if(ants.get(i).getClass() == Scout.class)
//            {
//                scoutProcedure(i);
//            }
//            else if(ants.get(i).getClass() == Soldier.class)
//            {
//                soldierProcedure(i);
//            }
//            else
//            {
//                balaProcedure(i);
//            }
        }
        displayBoard();
        updateDisplays();
    }

    private int nextId()
    {
        return ants.get(ants.size() - 1).id + 1;
    }

    private boolean queenProcedure(int antIndex)
    {
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

        ants.get(antIndex).growOlder();
        if(ants.get(antIndex).shouldDie())
        {
            return false;
        }

        return true;
    }

//    private boolean foragerProcedure(int antIndex)
//    {
//
//    }
//
//    private boolean scoutProcedure(int antIndex)
//    {
//
//    }
//
//    private boolean soldierProcedure(int antIndex)
//    {
//
//    }
//
//    private boolean balaProcedure(int antIndex)
//    {
//
//    }

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
        playButton = findViewById(R.id.playButton);
        fastPlayButton = findViewById(R.id.fastPlayButton);

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
            else if(selectedX % 3 == 2)
            {
                selectedX += 18;
            }

            if(regionDisplayed < 6 && regionDisplayed > 2)
            {
                selectedY += 9;
            }
            else if(regionDisplayed < 9)
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
}
