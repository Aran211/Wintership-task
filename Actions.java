public class Actions {
    private String playerId;
    private String action;
    private String matchId;
    private int coinAmount;
    private String betSide;

    public Actions(String playerId, String action, String matchId, int coinAmount, String betSide) {
        // Constructor
        this.playerId = playerId;
        this.action = action;
        this.matchId = matchId;
        this.coinAmount = coinAmount;
        this.betSide = betSide;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getAction() {
        return action;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    public String getBetSide() {
        return betSide;
    }
}

