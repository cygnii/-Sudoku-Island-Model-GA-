public class sudokuGrid {

    private final gridNumber[][] sudokuGrid;
    private int sudoku_dimensions;

    public sudokuGrid(int dimensions){
        sudoku_dimensions = dimensions;

        sudokuGrid = new gridNumber[dimensions][dimensions];

        for(int i=0;i<dimensions;i++){
            for(int j=0;j<dimensions;j++){
                sudokuGrid[i][j] = new gridNumber();
            }
        }
    }

    public sudokuGrid(sudokuGrid pGrid){
        sudoku_dimensions = pGrid.getDimensions();
        sudokuGrid        = new gridNumber[sudoku_dimensions][sudoku_dimensions];
        for(int i=0;i<sudoku_dimensions;i++){
            for(int j=0;j<sudoku_dimensions;j++){
                sudokuGrid[i][j] = new gridNumber();
                sudokuGrid[i][j].setNumber(pGrid.getNum(i,j));
                sudokuGrid[i][j].setLock(pGrid.isLocked(i,j));
            }
        }
    }

    public String getNum(int x, int y){
        return sudokuGrid[x][y].getNumber();
    }

    public void setNum(int x, int y, int pNum){
        sudokuGrid[x][y].setNumber(String.valueOf(pNum));
    }

    public void setLock(int x, int y, boolean pLock){ sudokuGrid[x][y].setLock(pLock); }

    public void clearNum(int x, int y){
        sudokuGrid[x][y].setNumber("");
    }

    public int getDimensions(){ return sudoku_dimensions; }

    public boolean isLocked(int x, int y){
        return sudokuGrid[x][y].isLocked();
    }

    public void print(){
        int sudoku_root_dimensions = (int)Math.sqrt(sudoku_dimensions);

        for(int i=0;i<sudoku_dimensions;i++){
            for(int j=0;j<sudoku_dimensions;j++) {
                if(!getNum(i,j).equals("")) {
                    System.out.print(getNum(i, j) + " ");
                } else {
                    System.out.print(". ");
                }
                if((j+1)%sudoku_root_dimensions==0){
                    System.out.print("\t");
                }
            }
            System.out.println();
            if ((i + 1) % sudoku_root_dimensions == 0 && i<(sudoku_dimensions-1)){
                System.out.println();
            }
        }
    }

    public gridNumber getGridNumber(int x, int y){
        return sudokuGrid[x][y];
    }

    public void setGridNumber(gridNumber num, int x, int y){
        sudokuGrid[x][y] = new gridNumber(num);
    }

}