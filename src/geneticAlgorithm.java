import java.util.ArrayList;
import java.util.Random;

public class geneticAlgorithm {

    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private int generationCount;
    private int maxGenerations;

    private sudokuPopulation population;
    private int sudoku_dimensions;

    private int tournamentSize;
    private double tournamentSelectProbability;

    public geneticAlgorithm(int pSize, double pMR, double pCR, int pE, int pTS, double pTSP, int mG){
        populationSize              = pSize;
        mutationRate                = pMR;
        crossoverRate               = pCR;
        elitismCount                = pE;
        tournamentSize              = pTS;
        tournamentSelectProbability = pTSP;
        maxGenerations              = mG;
        population                  = null;
    }

    public void initPopulation(sudokuGrid grid){
        generationCount   = 1;
        population        = new sudokuPopulation(populationSize, grid);
        sudoku_dimensions = grid.getDimensions();
    }

    public double getFitness(sudokuIndividual indiv){
        double fitness = sudokuGeneticsUtils.getFitness(indiv);
        indiv.setFitness(fitness);
        return fitness;
    }

    public int getPopulationSize(){
        return populationSize;
    }

    public void evalPopulation(){
        sudokuGeneticsUtils.evalPopulation(population);
    }

    public boolean isTerminationConditionMet(){
        return population.getFittest().getFitness() == sudokuGeneticsUtils.BoardSolvedState;
    }

    public void increaseGenerationCount(){
        generationCount++;
    }

    public int getCurrentGenerationCount(){
        return generationCount;
    }

    private sudokuIndividual RouletteWheelSelection(){

        double rouletteWheelPosition = Math.random() * population.getPopulationFitness();

        double spinWheel = 0;
        for(sudokuIndividual a : population.getIndividuals()){
            spinWheel += a.getFitness();
            if(spinWheel >= rouletteWheelPosition){
                return a;
            }
        }
        return population.getIndividuals()[populationSize - 1];
    }

    private sudokuIndividual TournamentSelection(){
        sudokuPopulation Tournament = new sudokuPopulation(tournamentSize, sudoku_dimensions);

        population.shuffle();
        for(int i=0;i<tournamentSize;i++){
            Tournament.setIndividual(population.getIndividual(i), i);
        }

        return Tournament.getFittest();
    }

    /**
     * Realize turnīrā selekciju ar varbūtību kā tiek izvēlēts noteikts sudoku individuāls
     * @return - sudoku Individuāls
     */
    private sudokuIndividual TournamentSelectionWithProbability(){
        // Izveidojam jaunu populāciju - turnīru ar noteikto skaitu
        sudokuPopulation Tournament = new sudokuPopulation(tournamentSize, sudoku_dimensions);

        // Sajaucam individuālus un inicializējam populāciju
        population.shuffle();
        for(int i=0;i<tournamentSize;i++){
            Tournament.setIndividual(population.getIndividual(i), i);
        }

        // Ar varbūtību, mēģinām izvelēties vispiemērotāko individuālu
        for(int i=0;i<tournamentSize;i++){
            if(tournamentSelectProbability < Math.random()){
                return Tournament.getFittestByIndex(i);
            }
        }
        // Jā neizdevās, atriežam vispiemērotāko individuālu
        return Tournament.getFittest();
    }

    /**
     * Izdara vienmērīgo sadalījumā krustošanu
     */
    public void sudokuUniformCrossover(){
        sudokuPopulation newPopulation = new sudokuPopulation(populationSize, sudoku_dimensions);

        for(int i=0;i<populationSize;i++){
            sudokuIndividual parent1 = TournamentSelectionWithProbability();
            if(crossoverRate > Math.random() && i >= elitismCount){
                sudokuIndividual parent2   = TournamentSelectionWithProbability();
                sudokuIndividual offspring = new sudokuIndividual(parent1.getIndividual().getDimensions());
                offspring.mapPredeterminedCells(parent1);

                ArrayList<gridNumber> parent1_chromosome   = parent1.getChromosome();
                ArrayList<gridNumber> parent2_chromosome   = parent2.getChromosome();
                ArrayList<gridNumber> offspring_chromosome = new ArrayList<>();
                if(parent1_chromosome.isEmpty()){
                    newPopulation.setIndividual(parent1, i);
                    continue;
                }

                for(int j=0;j<parent1_chromosome.size();j++){
                    if(0.5 > Math.random()){
                        offspring_chromosome.add(parent1_chromosome.get(j));
                    } else {
                        offspring_chromosome.add(parent2_chromosome.get(j));
                    }
                }

                offspring.setChromosome(offspring_chromosome);
                newPopulation.setIndividual(offspring,i);
            } else {
                newPopulation.setIndividual(parent1, i);
            }
        }
        population = newPopulation;
    }

    /**
     * Sudoku Mutacija
     */
    public void sudokuMutate(){
        gridNumber swapNumber, swapNumber2; int x, y, z;
        for(int i=0;i<populationSize;i++){
            sudokuIndividual individual = population.getIndividual(i);
            int D = individual.getIndividual().getDimensions();

            if(mutationRate > Math.random()){
                Random rnd = new Random();

                do {
                    x = rnd.nextInt(D);
                    y = rnd.nextInt(D);
                    swapNumber = individual.getGene(x, y);
                } while(swapNumber.isLocked());

                int j = 0;
                if(0.5 > Math.random()){
                    do{
                        z = rnd.nextInt(D);
                        swapNumber2 = individual.getGene(x,z);
                        j++;
                    } while(swapNumber2.isLocked() && z!=y && j<D);

                    individual.setGene(swapNumber,x,z);
                    individual.setGene(swapNumber2,x,y);
                } else {
                    do{
                        z = rnd.nextInt(D);
                        swapNumber2 = individual.getGene(z,y);
                        j++;
                    } while(swapNumber2.isLocked() && z!=x && j<D);

                    individual.setGene(swapNumber,z,y);
                    individual.setGene(swapNumber2,x,y);
                }
            }
        }

    }

    public boolean isMaxGenerationsReached(){
        return generationCount >= maxGenerations;
    }

    public sudokuPopulation getPopulation(){
        return population;
    }

}