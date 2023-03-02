package ladder;

import java.util.Scanner;
import ladder.client.LadderClientController;
import ladder.client.view.InputView;
import ladder.client.view.OutputView;
import ladder.common.LadderGame;
import ladder.server.LadderGameImpl;
import ladder.server.domain.ladder.RandomConnectionJudgement;

public class Application {

    public static void main(String[] args) {
        LadderClientController ladderClientController = new LadderClientController(
                getInputView(),
                getOutputView(),
                getLadderGame()
        );
        ladderClientController.play();
    }

    private static LadderGame getLadderGame() {
        return new LadderGameImpl(new RandomConnectionJudgement());
    }

    private static OutputView getOutputView() {
        return new OutputView();
    }

    private static InputView getInputView() {
        return new InputView(new Scanner(System.in));
    }
}
