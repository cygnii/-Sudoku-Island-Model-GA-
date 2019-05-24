import java.util.Arrays;
import java.util.Comparator;

public class sudokuGeneticsUtils {

    public static final double BoardSolvedState = 1.0f;

    /**
     * Piemērotības funkcija.
     */
    public static double getFitness(sudokuIndividual individual){
        sudokuGrid grid = individual.getIndividual();

        int sudoku_root_dimensions = (int)Math.sqrt(grid.getDimensions());
        int dupCount               = 0;

        int[] row    = new int[grid.getDimensions()];
        int[] column = new int[grid.getDimensions()];
        int[] square = new int[grid.getDimensions()];

        for (int j=0;j<grid.getDimensions();j++) {

            for (int i = 0; i < row.length; i++) {
                if(grid.getNum(j, i).equals("")){
                    row[i] = 0;
                } else {
                    row[i]    = Integer.parseInt(grid.getNum(j, i));
                    column[i] = Integer.parseInt(grid.getNum(i, j));
                }
            }

            Arrays.sort(row);
            Arrays.sort(column);

            for (int i = 1; i < row.length; i++) {
                if (row[i] == row[i - 1]) {
                    dupCount++;
                }
                if(column[i] == column[i-1]){
                    dupCount++;
                }
            }

            if(j%sudoku_root_dimensions==0){
                for(int l=0;l<sudoku_root_dimensions;l++) {
                    int pos = 0;
                    for (int i = l*sudoku_root_dimensions; i < l*sudoku_root_dimensions+sudoku_root_dimensions; i++) {
                        for (int k = j; k < j+sudoku_root_dimensions; k++) {
                            if(grid.getNum(i, k).equals("")){
                                square[pos] = 0;
                            } else {
                                square[pos] = Integer.parseInt(grid.getNum(i, k));
                            }
                            pos++;
                        }
                    }
                    Arrays.sort(square);
                    for(int i = 1;i<square.length;i++){
                        if (square[i] == square[i - 1]) {
                            dupCount++;
                        }
                    }
                }
            }

        }
        return (double)1/(1+dupCount);
    }

    public static sudokuIndividual getFittest(sudokuIndividual[] population, int index){
        Arrays.sort(population, new Comparator<sudokuIndividual>() {
            @Override
            public int compare(sudokuIndividual o1, sudokuIndividual o2) {
                return compareTo(o1, o2);
            }
        });
        return population[index];
    }

    public static int compareTo(sudokuIndividual o1, sudokuIndividual o2) {
        //dilstošā secība
        return Double.compare(o1.getFitness(),o2.getFitness())*(-1);
    }

    public static void evalPopulation(sudokuPopulation population){
        double populationFitness = 0;

        for(sudokuIndividual a : population.getIndividuals()){
            a.setFitness(getFitness(a));
            populationFitness += a.getFitness();
        }

        population.setPopulationFitness(populationFitness);
    }

    public static double calculateFitnessForTuning(int generationCount, double fitness){
        return ((double)generationCount/fitness);
    }

}