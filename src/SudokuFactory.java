
//https://en.wikipedia.org/wiki/Sudoku
//https://rafal.io/posts/solving-sudoku-with-dancing-links.html
//https://www.geeksforgeeks.org/program-sudoku-generator/
//https://gamedev.stackexchange.com/questions/56149/how-can-i-generate-sudoku-puzzles
//https://www.geeksforgeeks.org/sudoku-backtracking-7/

// Static class for sudoku game generation

// TODO: Implement for dimensions 4

// TODO Now works only for dimensions 9!

import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;
import java.util.*;

public class SudokuFactory {

    private static int[] number_sequence;

    private static sudokuGrid SudokuGrid;
    private static int sudoku_dimensions;
    private static int sudoku_root_dimensions;

    /**
     * Saģenerē jaunu sudoku režģī.
     * @param dimensions - dimensiju skaits. Parasti (9).
     * @param K - ciparu skaits, kuri tiek dzēsti nost no režģa.
     * @return - sudokuGrid intance
     */
    public static sudokuGrid sudokuFactory(int dimensions, int K){
        number_sequence = new int[dimensions];
        for(int i=1;i<=dimensions;i++){
            number_sequence[i-1] = i;
        }

        SudokuGrid = new sudokuGrid(dimensions);
        shuffleArray(number_sequence);

        // Izdara tukša sudoku režģa risināšanu ar nejauši izvelēto ciparu sekvenci
        SudokuSolveRecursiveBacktracking(SudokuGrid, number_sequence);

        // Nodzēš K ciparus no atrisinātā režģa
        removeGridNumbers(SudokuGrid, K);

        // Atzīme ciparus kuri palika ka ciparus, kurus nevar mainīt
        lockGrid(SudokuGrid);

        return SudokuGrid;
    }

    public static sudokuGrid SudokuSolveBacktracking(sudokuGrid grid){
        number_sequence = new int[grid.getDimensions()];
        for(int i=1;i<=grid.getDimensions();i++){
            number_sequence[i-1] = i;
        }

        // Izdara sudoku režģa risināšanu
        SudokuSolveRecursiveBacktracking(grid, number_sequence);

        return grid;
    }

    private static boolean SudokuSolveRecursiveBacktracking(sudokuGrid grid, int[] sequence){
        sudoku_dimensions      =  grid.getDimensions();
        sudoku_root_dimensions = (int)Math.sqrt(sudoku_dimensions);

        boolean isEmpty = false;
        int x = 0; int y = 0;

        for(int i=0;i<sudoku_dimensions;i++){
            for(int j=0;j<sudoku_dimensions;j++){
                if(grid.getNum(i,j).equals("")) {
                    x = i; y = j;
                    isEmpty = true;
                    break;
                }
            }
            if(isEmpty) break;
        }
        if(!isEmpty) return true;

        for(int i=0;i<sequence.length;i++){
            int N = sequence[i];
            if(isSafeToPlace(grid,x,y,N)){
                grid.setNum(x,y,N);
                if(SudokuSolveRecursiveBacktracking(grid, sequence)) return true;
                else grid.clearNum(x,y);
            }
        }
        return false;
    }

    private static boolean isSafeToPlace(sudokuGrid grid, int x, int y, int N){
        for(int i=0;i<sudoku_dimensions;i++){
            if(grid.getNum(x,i).equals(String.valueOf(N))) return false;
            if(grid.getNum(i,y).equals(String.valueOf(N))) return false;
        }

        int x0 = x - x % sudoku_root_dimensions;
        int y0 = y - y % sudoku_root_dimensions;

        for(int i=0;i<sudoku_root_dimensions;i++){
            for(int j=0;j<sudoku_root_dimensions;j++){
                if (grid.getNum(x0+i,y0+j).equals(String.valueOf(N))) return false;
            }
        }
        return true;
    }

    private static void shuffleArray(int[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i=0;i<n;i++) {
            int change = i+random.nextInt(n-i);
            swap(a,i,change);
        }
    }

    private static void swap(int[] a, int i, int change) {
        int helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }

    private static void removeGridNumbers(sudokuGrid grid, int K){
        sudoku_dimensions = grid.getDimensions();

        int[] indexes = new int[sudoku_dimensions*sudoku_dimensions];
        for(int i=1;i<indexes.length;i++){
            indexes[i-1] = i;
        }
        shuffleArray(indexes);

        int j=0;
        for(int i=0;i<indexes.length;i++){
            number_sequence = new int[sudoku_dimensions];
            for(int l=1;l<=number_sequence.length;l++){
                number_sequence[l-1] = l;
            }

            int ind = indexes[i];
            int x = ind / sudoku_dimensions;
            int y = ind % sudoku_dimensions;
            int N = Integer.parseInt(grid.getNum(x,y));

            grid.clearNum(x,y);
            number_sequence = ArrayUtils.removeAllOccurences(number_sequence, N);

            if(SudokuSolveRecursiveBacktracking(grid, number_sequence)){
                grid.setNum(x,y,N);
            } else {
                j++;
            }
            if(j==K) break;
        }
    }

    private static void lockGrid(sudokuGrid grid){
        for(int i=0;i<grid.getDimensions();i++){
            for(int j=0;j<grid.getDimensions();j++){
                if(!grid.getNum(i,j).equals("")){
                    grid.setLock(i,j,true);
                }
            }
        }
    }

    public static sudokuGrid SudokuSolveSubSquares(sudokuGrid grid){
        sudoku_root_dimensions = (int)Math.sqrt(grid.getDimensions());

        for(int i=0;i<grid.getDimensions();i+=sudoku_root_dimensions) {
            for(int j=0;j<grid.getDimensions();j+=sudoku_root_dimensions) {
                number_sequence = new int[grid.getDimensions()];
                for (int k = 1; k <= grid.getDimensions(); k++) {
                    number_sequence[k - 1] = k;
                }

                shuffleArray(number_sequence);

                for(int k=i;k<i+sudoku_root_dimensions;k++){
                    for(int l=j;l<j+sudoku_root_dimensions;l++){
                        if(!grid.getNum(k,l).equals("")) {
                            number_sequence = ArrayUtils.removeAllOccurences(number_sequence, Integer.parseInt(grid.getNum(k, l)));
                        }
                    }
                }
                for(int k=i;k<i+sudoku_root_dimensions;k++){
                    for(int l=j;l<j+sudoku_root_dimensions;l++){
                        if(grid.getNum(k,l).equals("")) {
                            int num = number_sequence[0];
                            grid.setNum(k,l,num);
                            number_sequence = ArrayUtils.removeAllOccurences(number_sequence, num);
                        }
                    }
                }

            }
        }
        return grid;
    }

    public static sudokuGrid SudokuFillPredeterminedCells(sudokuGrid grid){
        int sudoku_dimensions      = grid.getDimensions();
        int sudoku_root_dimensions = (int)Math.sqrt(sudoku_dimensions);
        int num = 0; int x = 0; int y = 0; boolean subgrid_start = false;
        int initial_rowX     = 0; int initial_rowY     = 0;
        int initial_columnX  = 0; int initial_columnY  = 0;
        int initial_subgridX = 0; int initial_subgridY = 0;

        for(int i=0;i<sudoku_dimensions;i++){
            int[] Row_Numbers     = new int[sudoku_dimensions];
            int[] Column_Numbers  = new int[sudoku_dimensions];

            for(int j=0;j<sudoku_dimensions;j++){
                Row_Numbers[j] = Column_Numbers[j] = j+1;
            }

            for(int j=0;j<sudoku_dimensions;j++){
                if(!grid.getNum(i,j).equals("")){
                    num = Integer.parseInt(grid.getNum(i,j));
                    Row_Numbers = ArrayUtils.removeAllOccurences(Row_Numbers, num);
                } else {
                    initial_rowX = i; initial_rowY = j;
                }
                // Cross Number from column
                if(!grid.getNum(j,i).equals("")){
                    num = Integer.parseInt(grid.getNum(j,i));
                    Column_Numbers = ArrayUtils.removeAllOccurences(Column_Numbers, num);
                } else {
                    initial_columnX = j; initial_columnY = i;
                }
            }

            if(Row_Numbers.length==1) grid.setNum(initial_rowX, initial_rowY, Row_Numbers[0]);
            else if(Column_Numbers.length==1) grid.setNum(initial_columnX, initial_columnY, Column_Numbers[0]);
        }

        for(int i=0;i<sudoku_dimensions;i+=sudoku_root_dimensions){
            for(int j=0;j<sudoku_dimensions;j+=sudoku_root_dimensions){
                int[] SubGrid_Numbers = new int[sudoku_dimensions];
                for(int h=0;h<sudoku_dimensions;h++){
                    SubGrid_Numbers[h] = h+1;
                }

                for(int k=i;k<i+sudoku_root_dimensions;k++){
                    for(int l=j;l<j+sudoku_root_dimensions;l++){
                        if(!grid.getNum(k,l).equals("")){
                            num = Integer.parseInt(grid.getNum(k,l));
                            SubGrid_Numbers = ArrayUtils.removeAllOccurences(SubGrid_Numbers, num);
                        } else {
                            initial_subgridX = k; initial_subgridY = l;
                        }
                    }
                }

                if(SubGrid_Numbers.length==1) grid.setNum(initial_subgridX, initial_subgridY, SubGrid_Numbers[0]);
            }
        }

        lockGrid(grid);
        return grid;
    }

}