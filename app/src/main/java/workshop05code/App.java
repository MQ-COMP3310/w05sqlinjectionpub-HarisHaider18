package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.info("Wordle created and connected.");
        } else {
            logger.severe("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.info("Wordle structures in place.");
        } else {
            logger.severe("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if(line.matches("^[a-z]{4}$")){
                logger.info("Valid word from data.txt: " + line);
                wordleDatabaseConnection.addValidWord(i, line);
            } else{
                logger.severe("Invalid word in data.txt");
            }
                i++;
        }
    
            
        

        } catch (IOException e) {
            logger.log(Level.SEVERE,"Not able to load . Sorry!" + e.getMessage(), e );
            
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                if(guess.matches("^[a-z]{4}$")){
                    logger.info("You've guessed: " + guess);
                


                if (wordleDatabaseConnection.isValidWord(guess)) { 
                    System.out.println("Success! It is in the the list.\n");
                    logger.info("Correct guess: " + guess);

                }else{
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                    logger.warning("Incorrect guess, not in the list: " + guess);
                }
            } else{
                System.out.print("the word '" + guess + "' is not valid." );
                logger.warning("Invalid guess entered: " + guess);
            }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "An error occurred while reading user input.", e);
        }

    }
}