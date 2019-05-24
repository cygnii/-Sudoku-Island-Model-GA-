import java.util.Random;

public class TuningIndividual {
    public int populationSize;
    public double mutationRate;
    public double crossoverRate;

    private double fitness;

    private double fitnessGA;
    private int generationCountGA;

    public TuningIndividual(int pS, double mR, double cR){
        populationSize              = pS;
        mutationRate                = mR;
        crossoverRate               = cR;
    }

    public TuningIndividual(){
        Random rnd = new Random();
        populationSize = rnd.nextInt(3000)+1;
        mutationRate   = Math.random();
        crossoverRate  = Math.random();
    }

    public TuningIndividual(TuningIndividual a){
        populationSize = a.populationSize;
        mutationRate   = a.mutationRate;
        crossoverRate  = a.crossoverRate;
    }

    public void setGAdata(geneticAlgorithm gen){
        fitnessGA           = gen.getPopulation().getFittest().getFitness();
        generationCountGA   = gen.getCurrentGenerationCount();
    }

    public int getGAGenerationCount(){
        return generationCountGA;
    }

    public double getFitnessGA(){
        return fitnessGA;
    }

    public void calcFitness(){
        fitness = ((float)generationCountGA/fitnessGA);
    }

    public double getFitness(){
        return fitness;
    }

}