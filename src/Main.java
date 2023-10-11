import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

//Porgrammstart
public class Main {
    public static void main(String[] Args) {
        try {
            BlacklistFile.CreateBlacklist();
            getUserInput.getUserPassword();
        } catch (PwChExceptionManager.UserInputIsInvalid | PwChExceptionManager.WordIsInBlacklist |
                 FileNotFoundException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred: " + e);
        } finally {
            GloVar.scn.close();
        }
    }
}


// Der User wird aufgefordert sein Password einzugeben
class getUserInput{
    public static void getUserPassword() throws PwChExceptionManager.UserInputIsInvalid, PwChExceptionManager.WordIsInBlacklist, FileNotFoundException {
        System.out.print("Enter your password to test it: ");
        String usersPassword = GloVar.scn.nextLine();


        if(usersPassword.contains(" ") || usersPassword.length() < 8){
            throw new PwChExceptionManager.UserInputIsInvalid();
        } else if (BlacklistFile.SearchWordInBlacklist(usersPassword)) {
            throw new PwChExceptionManager.WordIsInBlacklist();
        } else{
            CheckPassword.GetPasswordScore(usersPassword);
        }
    }

}

class CheckPassword{
    //Berechnet den Score
    public static void GetPasswordScore(String usersPassword){
        int score = 0;

        if (usersPassword.length() <= 13){
            score += 2;
        } else {
            score += 8;
        }

        score += (usersPassword.matches(".*[a-z].*")) ? 3 : 0;
        score += (usersPassword.matches(".*[A-Z].*")) ? 3 : 0;
        score += (usersPassword.matches(".*[0-9].*")) ? 4 : 0;

        int SpecialCharacterCount = usersPassword.replaceAll("[a-zA-Z0-9 ]", "").length();

        if (SpecialCharacterCount == 1) {
            score += 3;
        } else if (SpecialCharacterCount <=2) {
            score += 5;
        } else {
            score +=10;
        }

        EvaluateScore(score);

    }

    // Wertet den Score aus
    public static void EvaluateScore(int score){
        if (score <= 8){
            System.out.println("⛔️ Your password is " + GloVar.RED + "very weak" + GloVar.RESET +"! Try a better one!");
        } else if (score <= 17) {
            System.out.println("⚠️ Your password is " + GloVar.YELLOW + "medium strong" + GloVar.RESET +"! Rather take a better!");
        } else if (score <= 23) {
            System.out.println("✅ Your password is " + GloVar.GREEN + "strong" + GloVar.RESET +"! Good Password, but goes better!");
        } else {
            System.out.println("\u200D\uD83D\uDD25 Your password is " + GloVar.GREEN + "very strong" + GloVar.RESET +"! Good Job!" );
        }
        try {
            AnotherTry.Again();
        } catch (PwChExceptionManager.UserInputIsInvalid | PwChExceptionManager.WordIsInBlacklist |
                 FileNotFoundException e) {
            System.err.println(e);
        }
    }
}

// Blacklist wird erstellt und dann überprüft ob das Password enthalten ist
class BlacklistFile {

    public static void CreateBlacklist() {
        try {
            File FileObj = new File(GloVar.FILENAME);
            if (FileObj.createNewFile()) {
                System.out.println("File created: " + FileObj.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred." + e);
        }

    }

    public static boolean SearchWordInBlacklist(String usersPassword) throws FileNotFoundException {
        File FileObj = new File(GloVar.FILENAME);
        try (Scanner myReader = new Scanner(FileObj)) {
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (word.equals(usersPassword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

// Erneuter Aufruf des Programmes
class AnotherTry{
    public static void Again() throws PwChExceptionManager.UserInputIsInvalid, PwChExceptionManager.WordIsInBlacklist, FileNotFoundException {
        System.out.print("Do you want to do again? ");
        String answer = GloVar.scn.nextLine();

        if (answer.matches("^.*?(yes|Yes|y|N|ja|Ja|j|J|true|True).*$")) {
            getUserInput.getUserPassword();
        }else if (answer.matches("^.*?(no|No|nein|Nein|n|N|false|False).*$")) {
            System.exit(0);
        } else {
            throw new PwChExceptionManager.UserInputIsInvalid();
        }
    }

}

// Globale Variablen
class GloVar{
    public static Scanner scn = new Scanner(System.in);
    public static String GREEN = "\u001B[32m";
    public static String RESET = "\u001B[0m";
    public static String YELLOW = "\u001B[33m";
    public static String RED = "\u001B[31m";
    public static String FILENAME = "blacklist.txt";
}

// Exceptions
class PwChExceptionManager {
    static class UserInputIsInvalid extends Exception {
        public UserInputIsInvalid() {
            super("Your input was invalid. Your password must be at least 8 characters long! Please try again!");
        }
    }
    static class WordIsInBlacklist extends Exception {
        public WordIsInBlacklist() {
            super("Your input was invalid. Your password is blacklisted! Please try again!");
        }
    }

}

