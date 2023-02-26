package ladder.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ladder.dto.LadderInfoDto;
import ladder.dto.PlayerResultDto;

/**
 * 이 클래스는 사다리 게임에 대한 실제 로직을 담당하는 클래스 입니다
 */
public class LadderGame {

    private final ConnectionJudgement connectionJudgement;
    private final LadderRepository ladderRepository;

    public LadderGame(ConnectionJudgement connectionJudgement, LadderRepository ladderRepository) {
        this.connectionJudgement = connectionJudgement;
        this.ladderRepository = ladderRepository;
    }

    public void initializePlayers(List<String> playerNames) {
        ladderRepository.put(Players.class, new Players(playerNames));
    }

    public void initializeResults(List<String> resultNames) {
        int playerCount = ladderRepository.get(Players.class)
                .size();
        ladderRepository.put(Result.class, new Result(resultNames, playerCount));
    }

    public void initializeLadder(int height) {
        Players players = ladderRepository.get(Players.class);
        ladderRepository.put(Ladder.class, generateLadder(height, players));

    }

    private Ladder generateLadder(int height, Players players) {
        return Ladder.of(players.size(), height, connectionJudgement);
    }

    public LadderInfoDto getLadderInfo() {
        List<String> playerNames = ladderRepository.get(Players.class)
                .getPlayerNames();
        List<List<Boolean>> rows = ladderRepository.get(Ladder.class)
                .getRows();
        List<String> resultNames = ladderRepository.get(Result.class)
                .getNames();
        return new LadderInfoDto(playerNames, rows, resultNames);
    }

    public PlayerResultDto calculateResult() {
        Players players = ladderRepository.get(Players.class);
        Ladder ladder = ladderRepository.get(Ladder.class);
        Result resultItems = ladderRepository.get(Result.class);
        Map<String, Position> playerNameAndResultPosition = players.calculateResult(ladder);
        //그냥 바로 collect toMap 만 호출하면 순서가 보장이 되지 않아서 LinkedHashMap 으로 감싸준다
        Map<String, String> playerNameAndResultName = playerNameAndResultPosition.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> resultItems.findByPosition(entry.getValue()),
                        (x, y) -> y,
                        LinkedHashMap::new));
        return new PlayerResultDto(playerNameAndResultName);
    }
}
