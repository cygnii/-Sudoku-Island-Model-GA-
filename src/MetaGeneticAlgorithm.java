import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MetaGeneticAlgorithm {

    private TuningPopulation population;

    private TuningGAThread GAThread[];
    private CyclicBarrier barrier;

    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private int tournamentSize;
    private double tournamentSelectProbability;

    private int generationCount;

    private final int elitismCountForGA                = 1;
    private final int tournamentSizeForGA              = 5;
    private final double tournamentSelectProbabilityGA = 0.95f;

    public MetaGeneticAlgorithm(int Size, double mR, double cR, int eC, int pTS, double pTSP){
        population                  = new TuningPopulation(Size);
        populationSize              = Size;
        mutationRate                = mR;
        crossoverRate               = cR;
        elitismCount                = eC;
        tournamentSize              = pTS;
        tournamentSelectProbability = pTSP;
        barrier                     = new CyclicBarrier(populationSize+1);
    }

    public void evalPopulation(){
        for(TuningIndividual a : population.getPopulation()){
            a.calcFitness();
        }
    }

    public void init_GA(sudokuGrid grid){
        GAThread = new TuningGAThread[populationSize];
        for(int i=0;i<populationSize;i++){
            TuningIndividual tuningParams = population.getPopulation()[i];
            GAThread[i] = new TuningGAThread(tuningParams.populationSize,tuningParams.mutationRate,tuningParams.mutationRate,
                                             elitismCountForGA,tournamentSizeForGA,tournamentSelectProbabilityGA,String.valueOf(i+1),grid,barrier);
        }
    }

    public void start_up(){
        for(int i=0;i<populationSize;i++){
            GAThread[i].start();
        }
    }

    public void awaitForThreads(){
        for(;;){
            if(barrier.getNumberWaiting()==populationSize){
                break;
            }
            try { Thread.sleep(1); } catch (InterruptedException e ) {}
        }
    }

    public void releaseGAThreads(){
        try { barrier.await(); } catch (InterruptedException | BrokenBarrierException e) { }
    }

    public boolean isTerminationConditionMet(){
        return population.getFittestByIndex(0).getFitness() > 10;
    }

    public void Mutation(){
        TuningPopulation newPopulation = new TuningPopulation(populationSize);

        for(int i=0;i<populationSize;i++){

            if(mutationRate > Math.random()){
                newPopulation.getPopulation()[i] = new TuningIndividual();
            } else {
                newPopulation.getPopulation()[i] = new TuningIndividual(population.getPopulation()[i]);
            }
        }

        population = newPopulation;
    }

    public void AverageValueCrossover(){
        TuningPopulation newPopulation = new TuningPopulation(populationSize);

        for(int i=0;i<populationSize;i++){
            TuningIndividual parent1 = TournamentSelection();

            if(crossoverRate > Math.random() && i > elitismCount){
                TuningIndividual parent2   = TournamentSelection();
                TuningIndividual offspring = new TuningIndividual();

                offspring.populationSize = (parent1.populationSize + parent2.populationSize)/2;
                offspring.mutationRate = (float)((parent1.mutationRate + parent2.mutationRate)/2);
                offspring.crossoverRate = (float)((parent1.crossoverRate + parent2.crossoverRate)/2);

                newPopulation.getPopulation()[i] = offspring;
            } else {
                newPopulation.getPopulation()[i] = parent1;
            }
        }
        population = newPopulation;
    }

    private TuningIndividual TournamentSelection(){
        TuningPopulation Tournament = new TuningPopulation(tournamentSize);

        population.shuffle();
        for(int i=0;i<tournamentSize;i++){
            Tournament.getPopulation()[i] = new TuningIndividual(population.getPopulation()[i]);
        }

        for(int i=0;i<tournamentSize;i++){
            if(tournamentSelectProbability < Math.random()){
                return Tournament.getFittestByIndex(i);
            }
        }
        return Tournament.getFittestByIndex(0);
    }

    public TuningPopulation getPopulation(){
        return population;
    }

    public int getCurrentGenerationCount(){
        return generationCount;
    }

    public void incGenCount(){generationCount++;}

    public boolean isMaxGenerationsReached(){
        return generationCount > 1000;
    }

    public void collectResultsFromGA(){
        for(int i=0;i<populationSize;i++){
            population.getPopulation()[i].setGAdata(GAThread[i].getGA());
        }
    }

}
