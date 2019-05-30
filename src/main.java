public class main {

    /*Konfigurācijas parametri*/
    // Sudoku
    private static final int sudoku_dimensions              = 9;  //Vajag būt kvadrātskaitlim!
    private static final int numbersToRemoveFromGrid        = 56; // 41 - Vienkāršā; 56 - Vidējā; 61 - Grūta.

    // Ģenētiskais Algoritms
    private static final int populationSize                 = 2500;
    private static final double mutationRate                = 0.01;
    private static final double crossoverRate               = 1.0;
    private static final int elitismCount                   = 1;
    private static final int tournamentSize                 = 10;
    private static final double tournamentSelectProbability = 0.95;
    private static final int maxGenerationsStall            = 100;

    // Salu modelis
    private static final boolean enableIslandModel          = true;
    private static final int islandSize                     = 10;
    private static final int migrationInterval              = 10;
    private static final int migrationSize                  = 250;

    // Meta-ģenētiskais algoritms
    private static final boolean enableGAParameterTuning    = false;
    private static final int metaGAPopulationSize           = 5;
    private static final double metaGAMutationRate          = 0.20;
    private static final double metaGACrossoverRate         = 0.75;
    private static final int metaGAElitismCount             = 1;
    private static final int metaGATournamentSize           = 2;
    private static final double metaGATournSelectProb       = 0.95;
    private static final int metaGAMaxGenerationsStall      = 50;
    /*Konfigurācijas parametri*/

    public static void main(String[] args) {
        if(enableIslandModel){
            islandModel();
        } else if(enableGAParameterTuning) {
            GAparameterTuning();
        } else {
            sudokuGeneticAlgorithm();
        }
    }

    public static void sudokuGeneticAlgorithm(){
        // Saģenerēt jaunu sudoku režģi
        sudokuGrid grid = SudokuFactory.sudokuFactory(sudoku_dimensions, numbersToRemoveFromGrid);
        grid.print();


        geneticAlgorithm GA = new geneticAlgorithm(populationSize, mutationRate, crossoverRate, elitismCount,
                                                   tournamentSize, tournamentSelectProbability, maxGenerationsStall);

        GA.initPopulation(grid);
        GA.evalPopulation();

        do{
            if(GA.isMaxGenerationsReached()) break;

            System.out.println("Tekošais paaudžu skaits: " + GA.getCurrentGenerationCount());
            System.out.println("Vislābākā indivīdu piemērotībā: " + GA.getPopulation().getFittest().getFitness());
            System.out.println("Populācijas piemērotībā: " + GA.getPopulation().getPopulationFitness());

            GA.sudokuUniformCrossover();
            GA.sudokuMutate();
            GA.evalPopulation();
            GA.increaseGenerationCount();
        }while(!GA.isTerminationConditionMet());

        if(!GA.isMaxGenerationsReached()){
            System.out.println("Atrisinājums atrasts!");
        }

        System.out.println("Kopējais paaudžu skaits: " + GA.getCurrentGenerationCount());
        System.out.println("Pēdējā visslābāko indivīdu piemērotībā: " + GA.getPopulation().getFittest().getFitness());
        System.out.println("Pēdēja populācijas piemērotībā: " + GA.getPopulation().getPopulationFitness());

        grid = GA.getPopulation().getFittest().getIndividual();
        grid.print();
    }

    public static void islandModel(){
        sudokuGrid grid = SudokuFactory.sudokuFactory(sudoku_dimensions, numbersToRemoveFromGrid);
        grid.print();

        IslandModel islandModel = new IslandModel(populationSize, mutationRate, crossoverRate, elitismCount,
                                                  tournamentSize, tournamentSelectProbability,
                                                  islandSize, migrationInterval, migrationSize, maxGenerationsStall);

        islandModel.init(grid);
        islandModel.start_up();

        for(;;) {
            islandModel.awaitForThreads();
            {
                if (islandModel.isArchipelagoReadyForMigration()) {
                    islandModel.sudokuMigration();
                }

                islandModel.evaluateIslands();

                System.out.println("Tekošais paaudžu skaits: " + islandModel.getGenerationCount());
                System.out.println("vislābākā indivīdu piemērotībā: " + islandModel.getFittestThread().getGA().getPopulation().getFittest().getFitness());
                System.out.println("Salu piemērotība: " + islandModel.getIslandFitness());

                if (islandModel.isIslandTerminationConditionMet() || islandModel.isMaxGenerationsReached()) {
                    islandModel.releaseThreads();
                    break;
                }

                islandModel.incGenCount();
            }
            islandModel.startNextThreadGeneration();
        }
        if(!islandModel.isMaxGenerationsReached()) {
            System.out.println("Atrisinājums atrasts!");
        }

        System.out.println("Kopējais paaudžu skaits: " + islandModel.getGenerationCount());
        System.out.println("Pēdējā vislābākā indivīdu piemērotībā: " + islandModel.getFittestThread().getGA().getPopulation().getFittest().getFitness());
        System.out.println("Pēdēja salu piemērotība: " + islandModel.getIslandFitness());

        grid = islandModel.getFittestThread().getGA().getPopulation().getFittest().getIndividual();
        grid.print();
    }

    public static void GAparameterTuning(){

        MetaGeneticAlgorithm MGA = new MetaGeneticAlgorithm(metaGAPopulationSize, metaGAMutationRate, metaGACrossoverRate,
                                                            metaGAElitismCount, metaGATournamentSize, metaGATournSelectProb);

        do {
            sudokuGrid grid = SudokuFactory.sudokuFactory(sudoku_dimensions, numbersToRemoveFromGrid);
            MGA.init_GA(grid);
            MGA.start_up();
            MGA.awaitForThreads();
            MGA.collectResultsFromGA();
            MGA.releaseGAThreads();
            MGA.evalPopulation();

            System.out.println("Tekošais paaudžu skaits: " + MGA.getCurrentGenerationCount());
            System.out.println("Vislābākā indivīdu piemērotībā: " + MGA.getPopulation().getFittestByIndex(0).getFitness());
            System.out.println("Labāko parametru nobeiguma paaudžu skaits: " + MGA.getPopulation().getFittestByIndex(0).getGAGenerationCount());
            System.out.println("Labāko parametru nobeiguma indivīdā piemērotība: " + MGA.getPopulation().getFittestByIndex(0).getFitnessGA());
            System.out.println("Populācijas indivīdu skaits: " + MGA.getPopulation().getFittestByIndex(0).populationSize);
            System.out.println("Krustošanas līmenis: " + MGA.getPopulation().getFittestByIndex(0).crossoverRate);
            System.out.println("Mutācijas līmenis: " + MGA.getPopulation().getFittestByIndex(0).mutationRate);

            MGA.AverageValueCrossover();
            MGA.Mutation();
            MGA.incGenCount();
        } while (!MGA.isTerminationConditionMet());
    }

}