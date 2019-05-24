import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class sudokuGApopulationThread extends Thread {

    private geneticAlgorithm Thread_GA;
    private String Thread_Name;
    private CyclicBarrier barrier;

    public boolean toStop = false;

    private Thread t;

    public sudokuGApopulationThread(int pSize, double pMR, double pCR, int pE, int pTS, double pTSP, String pN, sudokuGrid pG, CyclicBarrier pB, int mG){
        Thread_GA   = new geneticAlgorithm(pSize,pMR,pCR,pE,pTS,pTSP,mG);
        Thread_Name = pN;
        barrier     = pB;
        Thread_GA.initPopulation(pG);
        Thread_GA.evalPopulation();
    }

    @Override
    public void start(){
        try {
            if (t == null) {
                t = new Thread (this, Thread_Name);
                t.start ();
            }
        } catch (Exception e){ }
    }

    @Override
    public void run(){
        for(;;){
            Thread_GA.sudokuUniformCrossover();
            Thread_GA.sudokuMutate();

            Thread_GA.evalPopulation();

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) { }

            if(toStop){
                break;
            }
        }
    }

    public geneticAlgorithm getGA(){
        return Thread_GA;
    }
 }