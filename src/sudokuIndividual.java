import java.util.ArrayList;

public class sudokuIndividual {
    private sudokuGrid individual;
    private double fitness = 0.0f;

    public sudokuIndividual(sudokuGrid pIndividual){
        individual = pIndividual;

        individual = SudokuFactory.SudokuFillPredeterminedCells(individual);
        individual = SudokuFactory.SudokuSolveSubSquares(individual);
    }

    public sudokuIndividual(int dimensionCount){
        individual = new sudokuGrid(dimensionCount);
    }

    public sudokuIndividual(sudokuIndividual pIndividual){
        individual = pIndividual.getIndividual();
    }

    public sudokuGrid getIndividual(){
        return individual;
    }

    public void setFitness(double pFitness){
        fitness = pFitness;
    }

    public double getFitness() {
        return fitness;
    }

    public void mapPredeterminedCells(sudokuIndividual parent){
        for(int i=0;i<parent.getIndividual().getDimensions();i++){
            for(int j=0;j<parent.getIndividual().getDimensions();j++){
                if(parent.getIndividual().isLocked(i,j)){
                    individual.setGridNumber(parent.getIndividual().getGridNumber(i,j),i,j);
                }
            }
        }
    }

    public ArrayList<gridNumber> getChromosome(){
        ArrayList<gridNumber> chromosome = new ArrayList<gridNumber>();

        for(int i=0;i<individual.getDimensions();i++){
            for(int j=0;j<individual.getDimensions();j++){
                if(!individual.isLocked(i,j)){
                    gridNumber num = new gridNumber(individual.getGridNumber(i,j));
                    chromosome.add(num);
                }
            }
        }

        return chromosome;
    }

    public void setChromosome(ArrayList<gridNumber> chromosome){
        int n = 0;
        for(int i=0;i<individual.getDimensions();i++){
            for(int j=0;j<individual.getDimensions();j++){
                if(!individual.isLocked(i,j)){
                    gridNumber num = chromosome.get(n); n++;
                    individual.setGridNumber(num,i,j);
                }
            }
        }
    }

    public gridNumber getGene(int x, int y){
        return individual.getGridNumber(x,y);
    }

    public void setGene(gridNumber N, int x, int y){
        individual.setGridNumber(N,x,y);
    }

}