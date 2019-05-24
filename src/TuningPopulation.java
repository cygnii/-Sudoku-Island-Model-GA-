import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class TuningPopulation {

    private TuningIndividual[] tuningPopulation;
    private int populationSize;

    public TuningPopulation(int Size, int pS, double mR, double cR){
        populationSize   = Size;
        tuningPopulation = new TuningIndividual[populationSize];
        for(int i=0;i<populationSize;i++){
            tuningPopulation[i] = new TuningIndividual(pS,mR,cR);
        }
    }

    public TuningPopulation(int Size){
        populationSize   = Size;
        tuningPopulation = new TuningIndividual[populationSize];
        for(int i=0;i<populationSize;i++){
            tuningPopulation[i] = new TuningIndividual();
        }
    }

    public TuningIndividual[] getPopulation(){
        return tuningPopulation;
    }

    public void shuffle(){
        Random rnd = new Random();
        for(int i=tuningPopulation.length-1;i>0;i--){
            int index = rnd.nextInt(i+1);
            TuningIndividual a      = tuningPopulation[index];
            tuningPopulation[index] = tuningPopulation[i];
            tuningPopulation[i]     = a;
        }
    }

    public TuningIndividual getFittestByIndex(int index){
        Arrays.sort(tuningPopulation, new Comparator<TuningIndividual>() {
            @Override
            public int compare(TuningIndividual indindiv1, TuningIndividual indindiv2) {
                return Double.compare(indindiv1.getFitness(),indindiv2.getFitness())*(-1);
            }
        });
        return tuningPopulation[index];
    }

}