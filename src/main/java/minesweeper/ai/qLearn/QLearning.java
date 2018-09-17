package minesweeper.ai.qLearn;

import minesweeper.game.Game;
import minesweeper.game.gui.MainGUI;

import java.util.*;

public class QLearning {

    private Game game;

    private static final double ALPHA = 0.1d; // Learning rate
    private static final double GAMMA = 0.7d; // Discount factor
    private static double EPSILON = 0.1d; // Exploration Factor, 1 is full random 0 is full greedy
    private static final int REWARD = 100;
    private static final int EPOCHS = 1000000;
    private static final int WIN_MINIMUM = 10000;
    private int wins = 0;

    private Map<State, Map<Action, Double>> q;
    private MainGUI gui;

    public QLearning() {
        game = new Game();
        q = new HashMap<>();

        gui = new MainGUI(game);
        gui.setVisible(true);

        int games = 0;
        while (wins < WIN_MINIMUM) {
//            System.out.println("Starting Epoch " + i);
            epoch();
            games++;
        }

        System.out.printf("After %s games\n", games);
        System.out.println("Playing full greedy Epoch");
        EPSILON = 0d;
        epoch();
    }

    protected List<Action> getPossibleActions(State state) {
        List<Action> actions = new ArrayList<>();

        for (int i = 0; i < state.getSurroundings().length; i++) {
            if (state.getSurroundings()[i] == -1) {
                actions.add(new Action(i % Game.WIDTH, (i - (i % Game.WIDTH)) / Game.WIDTH, Type.TEST));

                if (!state.getFlags()[i]) {
                    actions.add(new Action(i % Game.WIDTH, (i - (i % Game.WIDTH)) / Game.WIDTH, Type.FLAG));
                }
            }
        }

        return actions;
    }

    protected Action getGreedyAction(List<Action> possibleActions, State currentState) {
        if (q.containsKey(currentState)) {
            double max = Double.MIN_VALUE;
            Action greedy = null;
            Map<Action, Double> actionMap = q.get(currentState);

            for (Action possibleAction : possibleActions) {
                if (actionMap.containsKey(possibleAction)) {
                    if (actionMap.get(possibleAction) > max) {
                        max = actionMap.get(possibleAction);
                        greedy = possibleAction;
                    }
                } else {
                    if (0 > max) {
                        max = 0;
                        greedy = possibleAction;
                    }
                }
            }

            return greedy;
        } else {
            // Take random action
            return possibleActions.get(new Random().nextInt(possibleActions.size()));
        }
    }

    protected void epoch() {
        game.reset();
        Random random = new Random();

        while (true) {
            int randX = random.nextInt(Game.WIDTH);
            int randY = random.nextInt(Game.HEIGHT);

            game.click(randX, randY);
            if (!game.isGameover()) {
                break;
            } else {
                game.reset();
            }
        }

        while (!(game.hasWon() || game.isGameover())) {
            State currentState = game.getState();
            List<Action> possibleActions = getPossibleActions(currentState);

            Action action = null;
            if (random.nextDouble() < EPSILON) {
                // Take random action
                action = possibleActions.get(random.nextInt(possibleActions.size()));
            } else {
                // Take greedy action
                action = getGreedyAction(possibleActions, currentState);
            }

            if (action == null) {
                action = possibleActions.get(random.nextInt(possibleActions.size()));
            }


            takeAction(action);

            State nextState = game.getState();
            double maxNextQ = maxQ(nextState);
            double reward = 0; // Current state / action

            if (game.hasWon()) {
                reward = REWARD;
            }

            if (game.isGameover()) {
                reward = -REWARD;
            }

            double currentQ = getQ(currentState, action);

            //Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) – Q(state,action))
            double newQ = currentQ + ALPHA * (reward + GAMMA * maxNextQ - currentQ);
            setQ(currentState, action, newQ);
        }

        if (game.isGameover()) {
            System.out.println("AI died");
        }

        if (game.hasWon()) {
            System.out.println("AI has won!!!");
            wins++;
        }
    }

    protected void setQ(State currentState, Action action, double qValue) {
        if (!q.containsKey(currentState)) q.put(currentState, new HashMap<Action, Double>());
        Map<Action, Double> actionMap = q.get(currentState);
        actionMap.put(action, qValue);
    }

    protected double getQ(State state, Action action) {
        if (q.containsKey(state)) {
            if (q.get(state).containsKey(action)) {
                return q.get(state).get(action);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    protected double maxQ(State nextState) {
        List<Action> actions = getPossibleActions(nextState);

        if (q.containsKey(nextState)) {
            Map<Action, Double> actionMap = q.get(nextState);

            double max = Double.MIN_VALUE;

            for (Action action : actions) {
                if (actionMap.containsKey(action)) {
                    if (actionMap.get(action) > max) {
                        max = actionMap.get(action);
                    }
                } else {
                    if (0 > max) {
                        max = 0;
                    }
                }
            }

            return max;
        } else {
            return 0;
        }
    }

    protected void takeAction(Action action) {
        if (action.getType().equals(Type.FLAG)) {
            gui.getGamePanel().mark(action.getX(), action.getY());
        } else if (action.getType().equals(Type.TEST)) {
            gui.getGamePanel().click(action.getX(), action.getY());
        }
    }

    public static void main(String[] args) {
        new QLearning();
    }
}


/*
For each training cycle:

Select a random initial state.

Do While the final state has not been reached.

Select one among all possible actions for the current state.

Using this possible action, consider going to the next state.

Get maximum Q value for this next state based on all possible actions.

Compute Q: Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) – Q(state,action))

Set the next state as the current state.

End Do

End For
 */