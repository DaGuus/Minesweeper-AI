package minesweeper.ai.policyGradient;

import javafx.util.Pair;
import minesweeper.game.Game;
import minesweeper.game.gui.MainGUI;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PolicyGradients {

    private static final int MINIBATCH_SIZE = 64;
    private static final int MEMORY_SIZE = MINIBATCH_SIZE * 10;

    private static final double REWARD_GAME_END = 10d;
    private static final double REWARD_FLAG = 0.1d;
    private static final double REWARD_INCORRECT_FLAG = -0.5d;
    private static final double REWARD_INVALID_ACTION = 0;
    private static final double REWARD_PROGRESS = 1d;
    private static final double GAMMA = 0.7d; // Discount factor
    private static final double EPSILON_DECAY = 0.0001d;
    private double epsilon = 1;
    private Game game;
    private MultiLayerNetwork network;
    private MainGUI gui;
    private Random random;

    private int wins = 0;

    private List<Pair<INDArray, INDArray>> expercience;

    public PolicyGradients() {
        network = initNet();
        game = new Game();
        random = new Random();
        expercience = new ArrayList<>(MEMORY_SIZE);

        gui = new MainGUI(game);
        gui.setVisible(true);

        int epochCount = 0;
        boolean startedTraining = false;

        while (true) {
            game.reset();
            epoch(epsilon);
            if (game.hasWon()) wins++;

            epochCount++;
        }
    }

    public static void main(String[] args) {
        new PolicyGradients();
    }

    public Action getRandomAction(Game game) {
        while (true) {
            int x = random.nextInt(Game.WIDTH);
            int y = random.nextInt(Game.HEIGHT);
            Type type = Type.TEST;//Type.values()[random.nextInt(Type.values().length)];

            if (!game.getTile(x, y).isClicked()) {
                return new Action(x, y, type);
            }
        }
    }

    public void epoch(double epsilon) {
        Action lastAction = null;
        while (!(game.isGameover() || game.hasWon())) {
            // 1) Put current state into network
            DQNState currentState = new DQNState(game.getTiles());
            INDArray currentOutput = network.output(currentState.getInput());
//            int correctFlags = game.correctFlags();
//            int incorrectFlags = game.incorrectFlags();

            // 2) Apply best action
            //TODO Implement E-Greedy
            Action action;
            if (epsilon > random.nextDouble()) {
                // Take random action
                action = getRandomAction(game);
//                System.out.printf("Random action %s\n", action);
            } else {
                // Take greedy
                action = getBestAction(currentOutput);
//                System.out.println("Best action is " + action);
            }

            boolean isValidAction = true;
            if (game.getTile(action.getX(), action.getY()).isClicked()) {
                isValidAction = false;
            }

            if (epsilon == 0d) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            takeAction(action);

            // 3) Is the next state terminal?
            //      Yes? -> Update = Reward
            //      No?  -> Update = Bellman Equation
            double update = 0;
            if (game.isGameover()) {
                update = -REWARD_PROGRESS;
            } else if (game.hasWon()) {
                update = REWARD_GAME_END;
            } else if (!isValidAction) {
                // 4) Put next state into network
                DQNState nextState = new DQNState(game.getTiles());
                INDArray nextOutput = network.output(nextState.getInput());

                // 5) Get a reward
                update = REWARD_INVALID_ACTION + GAMMA * findQMax(nextOutput);
            } else {
                // 4) Put next state into network
                DQNState nextState = new DQNState(game.getTiles());
                INDArray nextOutput = network.output(nextState.getInput());

                // 5) Get a reward
//                double reward = (game.correctFlags() - correctFlags) * REWARD_FLAG + (game.incorrectFlags() - incorrectFlags) * REWARD_INCORRECT_FLAG;

                update = REWARD_PROGRESS + GAMMA * findQMax(nextOutput);
            }

            currentOutput.putScalar(new int[]{findMaxQIndex(currentOutput)}, update);
//            if (action.getType().equals(Type.TEST)) {
//                currentOutput.putScalar(new int[]{currentOutput.rows() - 2}, 1);
//                currentOutput.putScalar(new int[]{currentOutput.rows() - 1}, -1);
//            } else if (action.getType().equals(Type.FLAG)) {
//                currentOutput.putScalar(new int[]{currentOutput.rows() - 2}, -1);
//                currentOutput.putScalar(new int[]{currentOutput.rows() - 1}, 1);
//            }

            addToExperience(currentState.getInput(), currentOutput);
            if (lastAction != null && lastAction.hashCode() == action.hashCode()) break;
            lastAction = action;
        }
    }

    public void addToExperience(INDArray input, INDArray target) {
        expercience.add(new Pair<>(input, target));

        while (expercience.size() >= MEMORY_SIZE) {
            expercience.remove(0);
        }
    }

    protected void takeAction(Action action) {
        if (action.getType().equals(Type.FLAG)) {
            gui.getGamePanel().mark(action.getX(), action.getY());
        } else if (action.getType().equals(Type.TEST)) {
            gui.getGamePanel().click(action.getX(), action.getY());
        }
    }

    public Action getBestAction(INDArray output) {
        int index = findMaxQIndex(output);

//        boolean flag = false;
//        if (output.getDouble(output.rows() - 2) < output.getDouble(output.rows() - 1)) {
//            flag = true;
//        }

        int convX = index % Game.WIDTH;
        int convY = (index - convX) / Game.WIDTH;
        return new Action(convX, convY, Type.TEST);
    }

    public int findMaxQIndex(INDArray output) {
        int index = -1;
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < output.rows(); i++) {
            if (output.getDouble(i) >= max) {
                index = i;
                max = output.getDouble(i);
            }
        }

        if (index == -1) {
//            index = random.nextInt(Game.WIDTH * Game.HEIGHT);
            throw new AssertionError("No max found");
        }

        return index;
    }

    public double findQMax(INDArray output) {
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < output.rows(); i++) {
            if (output.getDouble(i) > max) {
                max = output.getDouble(i);
            }
        }

        if (max == Double.NEGATIVE_INFINITY) {
//            System.out.println(output);
            return 0;
//            throw new AssertionError("No max found");
        }

        return max;
    }

    private MultiLayerNetwork initNet() {
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .l2(0.0005)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(0.001, 0.9))
                .miniBatch(true)
                .list()
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                        .nIn(10)
                        .stride(1, 1)
                        .nOut(100)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new SubsamplingLayer.Builder(PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(2, new DenseLayer.Builder().activation(Activation.RELU)
                        .nOut(Game.WIDTH * Game.HEIGHT * 2).build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nOut(Game.WIDTH * Game.HEIGHT)
                        .activation(Activation.SIGMOID)
                        .build())
                .setInputType(InputType.convolutional(Game.HEIGHT, Game.WIDTH, 10)) //See note below
                .backprop(true).pretrain(false).build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();

        return model;
    }
}
