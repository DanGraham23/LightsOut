import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Stack;
import java.util.Random;
import java.util.Iterator;
/**
 * Simulates a game of Lights Out, where a 5x5 grid of lights
 * are created, and the goal is to turn on all the lights.
 * Each click turns on/off the chosen light and its four adjacent lights.
 *
 * @author Daniel Graham, Aimen Harizi, Zohaib Asif
 * @version 3/28/2021
 */
public class LightsOut extends MouseAdapter implements Runnable, ActionListener
{
    //Keep track of the boolean and JButton versions of the gameboard
    private JButton[][] boardButtons;

    //JPanel as instance variable
    private JPanel framePanel;

    //Track the moves needed to win the game for cheat button
    private Stack<Integer[]> moveTracker;

    //Track total moves
    private int moveCount = 0;
    private JLabel moveCountLabel = new JLabel("Moves : " + moveCount);

    //JButton for cheat button and reset button
    private JButton cheatButton;
    private JButton resetButton;

    //Panel to keep the game buttons
    private JPanel gameButtonPanel;

    //Array to keep track of original random moves for reset
    private int startingMoves[][];

    /**
     * Runs the main logic of the Lights Out game
     */
    @Override
    public void run(){
        //Create the game window
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Set window size and title
        JFrame frame = new JFrame("Lights Out");
        frame.setPreferredSize(new Dimension(1000,1000));

        //Exit operation when the user wants to quit
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Make cheat button
        cheatButton = new JButton("Cheat");
        cheatButton.setBackground(Color.red);
        cheatButton.setBorderPainted(true);
        cheatButton.setBorderPainted(true);
        cheatButton.addActionListener(this);

        //Make reset button
        resetButton = new JButton("Reset");
        resetButton.setBackground(Color.blue);
        resetButton.setBorderPainted(true);
        resetButton.setBorderPainted(true);
        resetButton.addActionListener(this);

        //Make gameButtonPanel and add to frame
        gameButtonPanel = new JPanel();

        gameButtonPanel.add(resetButton);
        gameButtonPanel.add(moveCountLabel);
        gameButtonPanel.add(cheatButton);

        frame.add(gameButtonPanel, BorderLayout.NORTH);

        //Main panel for the gameboard
        framePanel = new JPanel(new GridLayout(5,5));
        frame.add(framePanel, BorderLayout.CENTER);

        //Initialize empty JButton array
        boardButtons = new JButton[5][5];

        //Assign JButtons to the framePanel and JButtonArray
        for (int i = 0; i < boardButtons.length; i++){
            for (int j = 0; j < boardButtons.length; j++){
                JButton tempJ = new JButton("Off");
                tempJ.setBackground(Color.gray);
                tempJ.setOpaque(true);
                tempJ.setBorderPainted(true);
                tempJ.addActionListener(this);
                boardButtons[i][j] = tempJ;
                framePanel.add(boardButtons[i][j], i, j);
            }
        }

        //Randomize starting positions
        Random gen = new Random();
        int randomMoves = gen.nextInt(14)+12;
        //Save starting moves for reset button
        startingMoves = new int[randomMoves][2];
        moveTracker = new Stack<Integer[]>();
        for(int i = 0; i < randomMoves; i++){
            int rI = gen.nextInt(5);
            int rJ = gen.nextInt(5);
            //No duplicate values as back to back moves
            if (!moveTracker.isEmpty() && moveTracker.peek()[0] == rI && moveTracker.peek()[1] == rJ){
                while(moveTracker.peek()[0] == rI && moveTracker.peek()[1] == rJ){
                    rI = gen.nextInt(5);
                    rJ = gen.nextInt(5);
                }
            }
            startingMoves[i][0] = rI;
            startingMoves[i][1] = rJ;
            switchLights(rI, rJ);
            moveTracker.push(new Integer[] {rI, rJ});
        }

        //Start move count back at 0 after random moves
        moveCount = 0;
        moveCountLabel.setText("Moves : " + moveCount);
        
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Contains the logic for the switching of the lights
     * Also updates move counter
     * 
     * @param i the i coordinate
     * @param j the j coordinate
     */
    public void switchLights(int i, int j){

        //Logic to control which lights get turned on and off
        // (0, 0) is the bottom left of the game board

        //update incremenet moveCount and update label
        moveCount++;
        moveCountLabel.setText("Moves : " + moveCount);

        //Bottom row
        if (i == 0){
            //Bottom left corner
            if (j == 0){
                lightSwitchHelper(i, j);
                lightSwitchHelper(i+1, j);
                lightSwitchHelper(i, j+1);
            }
            //Bottom right corner
            else if (j == 4){
                lightSwitchHelper(i, j);
                lightSwitchHelper(i+1, j);
                lightSwitchHelper(i, j-1);
            }
            //Rest of bottom row
            else{
                lightSwitchHelper(i, j);
                lightSwitchHelper(i+1, j);
                lightSwitchHelper(i, j-1);
                lightSwitchHelper(i, j+1);
            }
        }
        //Top row
        else if (i == 4){
            //Top Left corner
            if (j == 0){
                lightSwitchHelper(i, j);
                lightSwitchHelper(i-1, j);
                lightSwitchHelper(i, j+1);
            }
            //Top right corner
            else if (j == 4){
                lightSwitchHelper(i, j);
                lightSwitchHelper(i-1, j);
                lightSwitchHelper(i, j-1);
            }
            //Rest of top row
            else{
                lightSwitchHelper(i, j);
                lightSwitchHelper(i-1, j);
                lightSwitchHelper(i, j-1);
                lightSwitchHelper(i, j+1);
            }
        }
        //Left row, no corner
        else if (j == 0){
            lightSwitchHelper(i, j);
            lightSwitchHelper(i-1, j);
            lightSwitchHelper(i+1, j);
            lightSwitchHelper(i, j+1);
        }
        //Right row, no corner
        else if (j == 4){
            lightSwitchHelper(i, j);
            lightSwitchHelper(i-1, j);
            lightSwitchHelper(i+1, j);
            lightSwitchHelper(i, j-1);
        }
        //Middle lights
        else{
            lightSwitchHelper(i, j);
            lightSwitchHelper(i, j+1);
            lightSwitchHelper(i, j-1);
            lightSwitchHelper(i+1, j);
            lightSwitchHelper(i-1, j);
        }
    }

    /**
     * Switches the light in the gameboard and repaints the panel
     * 
     * @param i the i coordinate
     * @param j the j coordinate
     */
    public void lightSwitchHelper(int i, int j){
        if (boardButtons[i][j].getText().equals("On")){
            //Switch light off
            boardButtons[i][j].setBackground(Color.gray);
            boardButtons[i][j].setText("Off");
        }else{
            //Switch light on
            boardButtons[i][j].setBackground(Color.yellow);
            boardButtons[i][j].setText("On");
        }
        //Repaint and upate the framePanel with new buttons
        framePanel.revalidate();
        framePanel.repaint();

    }

    /**
     * Checks the current stack to print out the current way to win
     * 
     */
    public void cheatMethod(){
        String winningMoves = "";
        Iterator<Integer[]> tempSIter = moveTracker.iterator();
        while(tempSIter.hasNext()){
            Integer[] temp = tempSIter.next();
            winningMoves = "[I: " + temp[0] + ",J: " + temp[1]+"] " + winningMoves;
        }
        JOptionPane.showMessageDialog(null, winningMoves);
    }

    /**
     * Resets the current board 
     * 
     */
    public void resetBoard(){
        //Reset all buttons back to On state
        for (int i = 0; i < boardButtons.length; i++){
            for (int j = 0; j < boardButtons.length; j++){
                boardButtons[i][j].setBackground(Color.gray);
                boardButtons[i][j].setText("Off");
            }
        }

        moveTracker.clear();
        //Put all buttons back to where they started after being randomized
        for (int i = 0; i < startingMoves.length; i++){
            int sI = startingMoves[i][0];
            int sJ = startingMoves[i][1];
            switchLights(sI, sJ);
            moveTracker.push(new Integer[] {sI, sJ});
        }

        //update moveCount and update label to original
        moveCount = 0;
        moveCountLabel.setText("Moves : " + moveCount);
    }

    /**
     * Checks to see if the game is over. There is a pop up if the game is won
     * 
     */
    public void checkWin(){
        //Check to see if all the lights are turned off
        for (int i = 0; i < boardButtons.length; i++){
            for (int j = 0; j < boardButtons.length; j++){
                if (boardButtons[i][j].getText().equals("On")){
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "You win!");
    }

    /**
     * Determines if the JButton has been clicked on gameboard
     * 
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e){

        //Check if Cheat button pressed
        if (e.getSource() == cheatButton){
            cheatMethod();
        }
        //Check to See if reset button pressed
        else if (e.getSource() == resetButton){
            resetBoard();
        }
        //If the button pressed is a light
        else{
            //Check to see which light button was pressed. Runs if other buttons not the one pressed.
            for (int i = 0; i < boardButtons.length; i++){
                for (int j = 0; j < boardButtons.length; j++){
                    //Correct move, remove from stack
                    if (e.getSource() == boardButtons[i][j] && moveTracker.peek()[0] == i && moveTracker.peek()[1] == j){
                        switchLights(i, j);
                        moveTracker.pop();
                        break;
                    }
                    //Incorrect move, add to stack
                    else if (e.getSource() == boardButtons[i][j]){
                        moveTracker.push(new Integer[] {i, j});
                        switchLights(i, j);
                        break;
                    }
                }
            }
            //Check to see if the game is over
            checkWin();
        }
    }

    public static void main(String[] args){

        javax.swing.SwingUtilities.invokeLater(new LightsOut());
    }
}