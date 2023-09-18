package rockpaperscissors;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    // Global variables to control the game state and options
    protected static boolean gameIsFinished = false;
    protected static String[] options = {"rock", "scissors", "paper"};
    protected final static String bye = "Bye!";

    // Method to read ratings from a file and return them as a HashMap
    private static HashMap<String, Integer> readRatingsFromFile() {
        HashMap<String, Integer> ratings = new HashMap<>();
        try {
            File ratingFile = new File("rating.txt");
            Scanner fileScanner = new Scanner(ratingFile);

            // Parsing each line to extract username and rating
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" ");
                ratings.put(parts[0], Integer.parseInt(parts[1]));
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
        }
        return ratings;
    }

    // Method to update the rating file with new ratings
    private static void updateRatingInFile(String userName, int newRating) {
        HashMap<String, Integer> ratings = readRatingsFromFile();
        ratings.put(userName, newRating);

        try (FileWriter writer = new FileWriter("rating.txt")) {
            for (String user : ratings.keySet()) {
                writer.write(user + " " + ratings.get(user) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error: Could not update rating file.");
        }
    }

    // Method to find the outcome of a standard game round
    private static int findOutcomeStandard(String userChoice, String computerChoice) {
        if (userChoice.equals(computerChoice)) return 0; // Draw
        if ("rock".equals(userChoice) && "scissors".equals(computerChoice) ||
                "scissors".equals(userChoice) && "paper".equals(computerChoice) ||
                "paper".equals(userChoice) && "rock".equals(computerChoice)) {
            return 1; // User wins
        }
        return -1; // Computer wins
    }

    // Method to find the outcome of an advanced game round
    private static int findOutcomeAdvanced(String userChoice, String computerChoice) {
        if (userChoice.equals(computerChoice)) return 0; // Draw

        List<String> optionsList = Arrays.asList(options);
        int userIndex = optionsList.indexOf(userChoice);

        // Reordering the options based on user's choice
        List<String> reorderedOptions = new ArrayList<>();
        reorderedOptions.addAll(optionsList.subList(userIndex + 1, optionsList.size()));
        reorderedOptions.addAll(optionsList.subList(0, userIndex));

        int halfSize = reorderedOptions.size() / 2;

        if (reorderedOptions.subList(0, halfSize).contains(computerChoice)) {
            return -1; // Computer wins
        } else {
            return 1; // User wins
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // User name input
        System.out.print("Enter your name: ");
        String userName = sc.nextLine();
        System.out.println("Hello, " + userName);

        // Reading initial ratings
        HashMap<String, Integer> ratings = readRatingsFromFile();
        int userRating = ratings.getOrDefault(userName, 0);

        // Game options input
        System.out.println("Enter the game options separated by comma (or press Enter for default options):");
        String userInputOptions = sc.nextLine();
        boolean isStandardGame = userInputOptions.isEmpty();
        if (!isStandardGame) {
            options = userInputOptions.split(",");
        }
        System.out.println("Okay, let's start");

        Random random = new Random();

        // Main game loop
        while (!gameIsFinished) {
            String userChoice = sc.nextLine();

            // Exit command
            if ("!exit".equals(userChoice)) {
                gameIsFinished = true;
                continue;
            }

            // Rating command
            if ("!rating".equals(userChoice)) {
                System.out.println("Your rating: " + userRating);
                continue;
            }

            // Validate user choice
            boolean validChoice = false;
            for (String option : options) {
                if (userChoice.equals(option)) {
                    validChoice = true;
                    break;
                }
            }
            if (!validChoice) {
                System.out.println("Invalid input");
                continue;
            }

            // Computer's choice
            String computerChoice = options[random.nextInt(options.length)];

            // Determine outcome
            int outcome;
            if (isStandardGame) {
                outcome = findOutcomeStandard(userChoice, computerChoice);
            } else {
                outcome = findOutcomeAdvanced(userChoice, computerChoice);
            }

            // Update rating based on outcome
            switch (outcome) {
                case 0:
                    System.out.printf("There is a draw (%s)\n", computerChoice);
                    userRating += 50;
                    updateRatingInFile(userName, userRating);
                    break;
                case 1:
                    System.out.printf("Well done. The computer chose %s and failed\n", computerChoice);
                    userRating += 100;
                    updateRatingInFile(userName, userRating);
                    break;
                case -1:
                    System.out.printf("Sorry, but the computer chose %s\n", computerChoice);
                    break;
            }
        }

        // Exit message
        System.out.println(bye);
    }
}
