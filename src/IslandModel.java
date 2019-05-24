import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class IslandModel {

    private sudokuGApopulationThread archipelago[];
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private int generationCount;
    private int islandSize;
    private int migrationInterval;
    private int migrationSize;
    private int tournamentSize;
    private double tournamentSelectProbability;
    private CyclicBarrier barrier;
    private int maxGenerations;

    private double islandFitness;


    public IslandModel(int pSize, double pMR, double pCR, int pE, int pTS, double pTSP, int iSS, int iST, int mS, int mG){
        islandSize                  = iSS;
        migrationInterval           = iST;
        migrationSize               = mS;
        generationCount             = 1;
        populationSize              = pSize;
        mutationRate                = pMR;
        crossoverRate               = pCR;
        elitismCount                = pE;
        tournamentSize              = pTS;
        tournamentSelectProbability = pTSP;
        maxGenerations              = mG;
        barrier                     = new CyclicBarrier(islandSize+1);
    }

    public void init(sudokuGrid grid){
        archipelago = new sudokuGApopulationThread[islandSize];
        for(int i=0;i<islandSize;i++){
            archipelago[i] = new sudokuGApopulationThread(populationSize,mutationRate,crossoverRate,elitismCount,tournamentSize,
                                                      tournamentSelectProbability, "Thread["+(i+1)+"]", grid, barrier,maxGenerations);
        }
    }

    public void start_up(){
        for(int i=0;i<islandSize;i++){
            archipelago[i].start();
        }
    }

    public void awaitForThreads(){
        for(;;){
            if(barrier.getNumberWaiting()==islandSize){
                break;
            }
            try { Thread.sleep(1); } catch (InterruptedException e ) {}
        }
    }

    public void startNextThreadGeneration(){
        try { barrier.await(); } catch (InterruptedException | BrokenBarrierException e) { e.printStackTrace(); }
    }

    public void evaluateIslands(){
        islandFitness = 0.0f;

        for(sudokuGApopulationThread a : archipelago){
            islandFitness += a.getGA().getPopulation().getPopulationFitness();
        }
    }

    public boolean isIslandTerminationConditionMet(){
        return getFittestThread().getGA().getPopulation().getFittest().getFitness() == sudokuGeneticsUtils.BoardSolvedState;
    }

    public void incGenCount(){generationCount++;}

    public int getGenerationCount() {
        return generationCount;
    }

    public sudokuGApopulationThread getFittestThread(){
        int index = 0; double bestFitness = archipelago[index].getGA().getPopulation().getFittest().getFitness();

        for(int i=1;i<islandSize;i++){
            double currentFitness = archipelago[i].getGA().getPopulation().getFittest().getFitness();
            if(bestFitness < currentFitness) {
                index = i; bestFitness = currentFitness;
            }
        }
        return archipelago[index];
    }

    public boolean isArchipelagoReadyForMigration(){
        return generationCount%migrationInterval==0;
    }

    public void releaseThreads(){
        for(sudokuGApopulationThread a : archipelago){
            a.toStop = true;
        }
    }

    public void sudokuMigration(){
        int j = 0;
        for(sudokuGApopulationThread island : archipelago){
            j++; if(j==islandSize) j = 0;
            for(int i=0;i<migrationSize;i++){
                sudokuIndividual individual = new sudokuIndividual(island.getGA().getPopulation().getFittestByIndex(i));
                archipelago[j].getGA().getPopulation().setIndividual(individual,populationSize-i-1);
                archipelago[j].getGA().evalPopulation();
            }
        }
    }

    public boolean isMaxGenerationsReached(){
        return generationCount >= maxGenerations;
    }

    public double getIslandFitness(){
        return islandFitness;
    }

}