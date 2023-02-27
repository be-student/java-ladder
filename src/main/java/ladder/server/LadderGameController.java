package ladder.server;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ladder.common.LadderGame;
import ladder.server.domain.ConnectionJudgement;
import ladder.server.domain.Ladder;
import ladder.server.domain.Players;
import ladder.server.domain.Position;
import ladder.server.domain.Result;

/**
 * 이 클래스는 사다리 게임에 대한 실제 로직을 담당하는 클래스 입니다
 */
public class LadderGameController implements LadderGame {

    private final ConnectionJudgement connectionJudgement;
    private Players players;
    private Ladder ladder;
    private Result result;


    public LadderGameController(ConnectionJudgement connectionJudgement) {
        this.connectionJudgement = connectionJudgement;
    }

    @Override
    public void initializePlayers(List<String> playerNames) {
        players = new Players(playerNames);
    }

    @Override
    public void initializeResults(List<String> resultNames) {
        validatePlayerState();
        int playerCount = players.size();
        result = new Result(resultNames, playerCount);
    }

    private void validatePlayerState() {
        if (players == null) {
            throw new IllegalStateException("플레이어가 초기화 되지 않았습니다");
        }
    }

    @Override
    public void initializeLadder(int height) {
        validatePlayerState();
        ladder = generateLadder(height, players);
    }

    @Override
    public List<String> getPlayerNames() {
        validatePlayerState();
        return players.getPlayerNames();
    }

    @Override
    public List<List<Boolean>> getLadderRows() {
        validateLadderState();
        return ladder.getRows();
    }

    private void validateLadderState() {
        if (ladder == null) {
            throw new IllegalStateException("사다리가 초기화 되지 않았습니다");
        }
    }

    @Override
    public List<String> getResultNames() {
        validateResultState();
        return result.getNames();
    }

    private void validateResultState() {
        if (result == null) {
            throw new IllegalStateException("결과가 초기화 되지 않았습니다");
        }
    }

    private Ladder generateLadder(int height, Players players) {
        return Ladder.of(players.size(), height, connectionJudgement);
    }

    @Override
    public Map<String, String> calculateResult() {
        validateRequiredStates();
        Map<String, Position> playerNameAndResultPosition = players.calculateResult(ladder);
        //그냥 바로 collect toMap 만 호출하면 순서가 보장이 되지 않아서 LinkedHashMap 으로 감싸준다
        return playerNameAndResultPosition.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> result.findByPosition(entry.getValue()),
                        (x, y) -> y,
                        LinkedHashMap::new));
    }

    private void validateRequiredStates() {
        validatePlayerState();
        validateLadderState();
        validateResultState();
    }
}
