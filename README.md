# Sudoku-Solution-Validator

This program takes a Sudoku puzzle solution as an input and then determines whether the solution is valid or not. 

This validation is done using 27 threads.
  * 27 threads are created.
  * 9 for each 3x3 subsection.
  * 9 for the 9 columns.
  * 9 for the 9 rows.
  
Each thread will return true or false. 
  * If returned true then it indicats that the corresponding region in the puzzle they were responsible for is valid.
  * The program then waits for all threads to complete their execution and checks if the return values of all the threads have been set to true.  
  * If yes, the solution is valid. If not, solution is invalid. 
  
This program also displays the total time taken for validation.
