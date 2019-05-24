
import java.util.Random;

public class sudokuPopulation {

    private sudokuIndividual sudokuPopulation[];
    private double populationFitness;

    public sudokuPopulation(int populationSize, sudokuGrid grid){
        sudokuPopulation = new sudokuIndividual[populationSize];
        for(int i=0;i<populationSize;i++){
            sudokuPopulation[i] = new sudokuIndividual(new sudokuGrid(grid));
        }
    }

    public sudokuPopulation(int populationSize, int dimensionCount){
        sudokuPopulation = new sudokuIndividual[populationSize];
        for(int i=0;i<populationSize;i++){
            sudokuPopulation[i] = new sudokuIndividual(dimensionCount);
        }
    }

    public sudokuIndividual getFittest(){
        return sudokuGeneticsUtils.getFittest(sudokuPopulation, 0);
    }

    public sudokuIndividual getFittestByIndex(int index){
        return sudokuGeneticsUtils.getFittest(sudokuPopulation, index);
    }

    public double getPopulationFitness(){
        return populationFitness;
    }

    public void setPopulationFitness(double pPopulationFitness){
        populationFitness = pPopulationFitness;
    }

    public void setIndividual(sudokuIndividual individual, int index){
        sudokuPopulation[index] = individual;
    }

    public sudokuIndividual getIndividual(int index){
        return sudokuPopulation[index];
    }

    public sudokuIndividual[] getIndividuals(){
        return sudokuPopulation;
    }

    public void shuffle(){
        Random rnd = new Random();
        for(int i=sudokuPopulation.length-1;i>0;i--){
            int index = rnd.nextInt(i+1);
            sudokuIndividual a      = sudokuPopulation[index];
            sudokuPopulation[index] = sudokuPopulation[i];
            sudokuPopulation[i]     = a;
        }
    }

}