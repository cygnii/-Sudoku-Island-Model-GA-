import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TuningGAThread extends Thread {

    private geneticAlgorithm Thread_GA;
    private String Thread_Name;
    private CyclicBarrier barrier;

    private Thread t;

    private final int MaxThreadGenerations = 50;

    public TuningGAThread(int pSize, double pMR, double pCR, int pE, int pTS, double pTSP, String pN, sudokuGrid pG, CyclicBarrier pB){
        Thread_GA   = new geneticAlgorithm(pSize, pMR, pCR, pE, pTS, pTSP, MaxThreadGenerations);
        Thread_Name = pN;
        barrier     = pB;
        Thread_GA.initPopulation(pG);
        Thread_GA.evalPopulation();

    }

    public void start(){
        try {
            if (t == null) {
                t = new Thread (this, Thread_Name);
                t.start ();
            }
        } catch (Exception e){

        }
    }

    @Override
    public void run(){
        while (!Thread_GA.isTerminationConditionMet()) {

            if (Thread_GA.isMaxGenerationsReached()) break;

            Thread_GA.sudokuUniformCrossover();
            Thread_GA.sudokuMutate();

            Thread_GA.evalPopulation();
            Thread_GA.increaseGenerationCount();
        }

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) { }

    }

    public geneticAlgorithm getGA(){
        return Thread_GA;
    }

}