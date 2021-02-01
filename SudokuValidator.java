/*
 * Implementing Multithreaded Sudoku Solution Validator
 * This Program reads Sudoku Solution from a JSON file and checks if its valid or not.
 * It works for 4 x 4, 9 x 9, 16 x 16, and 25 x 25 sudoku solvers.
 */

/* 
 * File:  SudokuValidator.java
 * Author: priyankamuddalapuram
 *
 * Created on April 15, 2020, 3:12 PM
 */

package sudokuproject;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.time.*;

public class SudokuValidator 
{
    //Global variable for Sudoku Size (for 4 x 4, 9 x 9, 16 x 16 and 25 x 25)
    private static int sudokuSize;
    
    // Global variable for number of threads
    private static int NUM_OF_THREADS;

    // Sudoku puzzle solution to validate
    private static int[][] sudokuPuzzle = new int[sudokuSize][sudokuSize];;

    // Array that worker threads will update to check if solution is valid or not
    private static boolean[] validity;
    
    //JSON file full path
    private static final String jsonFile = "/Users/priyankamuddalapuram/Desktop/AOS_Programs/SudokuProject/src/sudokuproject/SudokuSol4.json";
    
    // General object that will be extended by worker thread objects
    //only contains constructor to assign row and column values
    public static class RowAndColumnObject 
    {
        int row;
        int column;
        RowAndColumnObject(int row, int column) 
        {
            this.row = row;
            this.column = column;
        }
    }

    ///************************************************************************************************** /
    // Class: IsValidRow
    // Description: Runnable object that determines if valid numbers only appear once in a row
    //              Valid numbers for 4 x 4 Sudoku Solver are 1 to 4
    //              Valid numbers for 9 x 9 Sudoku Solver are 1 to 9
    //              Valid numbers for 16 x 16 Sudoku Solver are 1 to 16
    //              Valid numbers for 25 x 25 Sudoku Solver are 1 to 25
    // Constructor: 2 Parameters  Constructor (row and column).
    // Functions: Overrides run() function of Runnable interface
    // run() Function task: Verifies if row contains only valid numbers and it should appier once.
    //                      If yes, sets relevent element in validity array to true.
    //                      If no, exists thread execution.
    ///**************************************************************************************************/
    public static class IsValidRow extends RowAndColumnObject implements Runnable 
    {
        //Constructor
        IsValidRow(int row, int column) {
            super(row, column);
        }

        @Override
        public void run() 
        {
            //Check if parameters are valid or not
            if (column != 0 || row > (sudokuSize - 1)) {
                System.out.println("Row or column for row subsection is invalid!");
                return;
            }

            // Check if valid number only appear once in the row
            boolean[] arrayRowValidation = new boolean[sudokuSize];
            int i;

            for (i = 0; i < sudokuSize; i++) 
            {
                // If the corresponding index for the number is set to 1, and the number is encountered again,
                // the valid array will not be updated and the thread will exit.
                int num = sudokuPuzzle[row][i];

                if (num < 1 || num > sudokuSize || arrayRowValidation[num - 1]) 
                {
                    return;
                } 
                else
                {
                    arrayRowValidation[num - 1] = true;
                }
            }

            // If reached this point, row subsection is valid.
            validity[sudokuSize + row] = true;
        }
    }

    ///************************************************************************************************** /
    // Class: IsValidColumn
    // Description: Runnable object that determines if valid numbers only appear once in a column
    //              Valid numbers for 4 x 4 Sudoku Solver are 1 to 4
    //              Valid numbers for 9 x 9 Sudoku Solver are 1 to 9
    //              Valid numbers for 16 x 16 Sudoku Solver are 1 to 16
    //              Valid numbers for 25 x 25 Sudoku Solver are 1 to 25
    // Constructor: 2 Parameters  Constructor (row and column).
    // Functions: Overrides run() function of Runnable interface
    // run() Function task: Verifies if column contains only valid numbers and it should appier once.
    //                      If yes, sets relevent element in validity array to true.
    //                      If no, exists thread execution.
    ///**************************************************************************************************/
    public static class IsValidColumn extends RowAndColumnObject implements Runnable 
    {
        //Constructor
        IsValidColumn(int row, int column) 
        {
            super(row, column);
        }

        @Override
        public void run() 
        {
            //Check if parameters are valid or not
            if (row != 0 || column > (sudokuSize - 1)) 
            {
                System.out.println("Row or column for column subsection is invalid.");
                return;
            }

            // Verify valid number only appear once in the column
            boolean[] arraycolValidation = new boolean[sudokuSize];
            
            int i;
            for (i = 0; i < sudokuSize; i++) 
            {
                // If the corresponding index for the number is set to 1, and the number is encountered again,
                // the valid array will not be updated and the thread will exit.
                int num = sudokuPuzzle[i][column];
                if (num < 1 || num > sudokuSize || arraycolValidation[num - 1])
                {
                    return;
                }
                    else
                {
                    arraycolValidation[num - 1] = true;
                }
            }

            // If reached this point, column subsection is valid.
            validity[(sudokuSize * 2) + column] = true;
        }

    }

    ///************************************************************************************************** /
    // Class: IsValidRow
    // Description: Runnable object that determines if valid numbers only appear once in a Subsection Grid
    //              Valid numbers for 4 x 4 Sudoku Solver are 1 to 4
    //              Valid numbers for 9 x 9 Sudoku Solver are 1 to 9
    //              Valid numbers for 16 x 16 Sudoku Solver are 1 to 16
    //              Valid numbers for 25 x 25 Sudoku Solver are 1 to 25
    // Constructor: 2 Parameters  Constructor (row and column).
    // Functions: Overrides run() function of Runnable interface
    // run() Function task: Verifies if Grid contains only valid numbers and it should appier once.
    //                      If yes, sets relevent element in validity array to true.
    //                      If no, exists thread execution.
    ///**************************************************************************************************/
    public static class IsValidGrid extends RowAndColumnObject implements Runnable 
    {
        //Constructor
        IsValidGrid(int row, int column) {
            super(row, column);
        }

        @Override
        public void run() 
        {
            int sudokuSizeSqrt = (int) Math.sqrt(sudokuSize);
            //Check if parameters are valid or not
            if (row > (sudokuSize - sudokuSizeSqrt) || row % sudokuSizeSqrt != 0 || column > (sudokuSize - sudokuSizeSqrt) || column % sudokuSizeSqrt != 0) 
            {
                System.out.println("Row or column for subsection is invalid.");
                return;
            }

            // Check if valid number only appear once in Subsection Grid
            boolean[] arrayGridValidation = new boolean[sudokuSize];
            
            for (int i = row; i < row + sudokuSizeSqrt; i++) 
            {
                for (int j = column; j < column + sudokuSizeSqrt; j++) 
                {
                    int num = sudokuPuzzle[i][j];
                    if (num < 1 || num > sudokuSize || arrayGridValidation[num - 1]) 
                    {
                        return;
                    } 
                    else 
                    {
                        arrayGridValidation[num - 1] = true;
                    }
                }
            }

            // Maps the subsection to an index in the first (SudokuSize - 1) indices of the valid array
            validity[row + (column / sudokuSizeSqrt)] = true;
        }

    }
    
    ///************************************************************************************************** /
    // Functions: readJSONObjandConvertToArr
    // Description: Gets JSONObject by reading specified JSON file and converts as integer array.
    //              Gets sudokuSize from JSON file.
    //              Assignes converted integer array to Global array sudokuPuzzle.
    ///**************************************************************************************************/
    public static void readJSONObjandConvertToArr()
    {
        JSONParser parser = new JSONParser();
        try {
            //Read JSON file, convert to JSONObject and get sudoku size.
            Object obj = parser.parse(new FileReader(jsonFile));            
            JSONObject jsonObject = (JSONObject) obj;
            
            //Get Sudoku Size
            sudokuSize = jsonObject.size();
            
            int[][] arr = new int[sudokuSize][sudokuSize];
            String[] str = new String[sudokuSize];
            for(int i = 0; i < sudokuSize; ++i)
            {
                //Convert JSONObject to JSONArray
                JSONArray jArray = new JSONArray();
                jArray.add(jsonObject.get(Integer.toString(i+1)));
                
                //Convert JSONArray to String array
                String s = jArray.get(0).toString();
                String[] aStr = s.split(",");
                aStr[0] = aStr[0].substring(1);
                aStr[sudokuSize - 1] = aStr[sudokuSize - 1].substring(0, (aStr[sudokuSize - 1].length() - 1));

                //Convert String array to Integer array
                int[] iArr = new int[aStr.length];
                for(int j=0; j< aStr.length; ++j)
                {
                    iArr[j] = Integer.parseInt(aStr[j]);
                }
                arr[i] = iArr;
            }
            //Assign integer array to global array sudokuPuzzle
            sudokuPuzzle = arr;
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    ///************************************************************************************************** /
    // Function: initializeVariablesandSudokuSolArray
    // Description: Initializes all global variables and calls readJSONObjandConvertToArr function to
    //              read JSON file and get Sudoku Solution Array.
    ///**************************************************************************************************/
    public static void initializeVariablesandSudokuSolArray()
    {
        //Read JSON file and assign integer array to sudokuPuzzle
        readJSONObjandConvertToArr();
        
        //Initializing global variables
        NUM_OF_THREADS = sudokuSize * 3;
        validity = new boolean[NUM_OF_THREADS];
        
        //Print Sudoku Solution
        System.out.println(sudokuSize +" x "+ sudokuSize +" Sudoku Solution: ");
        for(int i = 0; i < sudokuSize; ++i)
        {
            System.out.print("[");
            for(int j = 0; j < sudokuSize; ++j)
            {
                System.out.print(sudokuPuzzle[i][j]);
                if(j != (sudokuSize-1))
                    System.out.print(", ");
            }
            System.out.println("]");
        }
    }
    
    ///************************************************************************************************** /
    // Function: main
    // Description: Creates (sudokuSize * 3) number of threads to verify if sudoku solution is valid or not.
    //              Prints "Sudoku solution is valid" if its valid otherwise prints "Sudoku solutions is invalid".
    ///**************************************************************************************************/
    public static void main(String args[]) 
    {
        //initialize all global variables and get sudoku solution array by reading JSON file.
        initializeVariablesandSudokuSolArray();
       
        //Get current time before starting validation
        Instant start = Instant.now();
        
        //Variables
        Thread[] threads = new Thread[NUM_OF_THREADS];
        int threadIndex = 0;

        //Create (sudokuSize) no of threads for subsection grids, (sudokuSize) no of threads for all columns and (sudokuSize) no of threads for all rows.
        //This will end with a total of (sudokuSize * 3) no of threads.
        for (int i = 0; i < sudokuSize; i++) 
        {
            for (int j = 0; j < sudokuSize; j++) 
            {
                if (i % (Math.sqrt(sudokuSize)) == 0 && j % (Math.sqrt(sudokuSize)) == 0) 
                {
                    threads[threadIndex++] = new Thread(new IsValidGrid(i, j));
                    System.out.println("Thread No for ("+i+","+j+"): "+ threadIndex);
                }
                if (i == 0) 
                {
                    threads[threadIndex++] = new Thread(new IsValidColumn(i, j));
                    System.out.println("Thread No for ("+i+","+j+"): "+ threadIndex);
                }
                if (j == 0) 
                {
                    threads[threadIndex++] = new Thread(new IsValidRow(i, j));
                    System.out.println("Thread No for ("+i+","+j+"): "+ threadIndex);
                }
            }
        }

        //Starting all threads
        for (int i = 0; i < threads.length; i++) 
        {
            threads[i].start();
        }

        //Wait for all threads to finish
        for (int i = 0; i < threads.length; i++) 
        {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        int isValid = 1;
        //The sudoku solution is invalid, If any of the entries in the valid array are false
        for (int i = 0; i < validity.length; i++) 
        {
            if (!validity[i]) 
            {
                System.out.println("Sudoku Solution for "+ sudokuSize +" x "+ sudokuSize +" Sudoku is Invalid!");
                --isValid;
                break;
            }
        }
        if(isValid == 1)
            System.out.println("Sudoku Solution for "+ sudokuSize +" x "+ sudokuSize +" Sudoku is Valid!");
        
        //Get current time after validation
        Instant finish = Instant.now();
        
        //Calculate total time take for the validation (in milliseconds).
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Total time taken for the validation is "+ timeElapsed+" milliseconds");
    }
}