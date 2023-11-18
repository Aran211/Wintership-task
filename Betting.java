/*****************************************************************************
 * Playtech Wintership
 * 18.11.2023
 *
 * Autor: Ron-Aran Paju
 *
 *****************************************************************************/

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Betting {



    public static void main(String[] args) throws Exception {
        /*
        *This is the main function, it creates a arraylist for players and the Hashmap is for matchrates.
        * Next is the input from file which is with Scanner and it reads with positions for example position 0 is match id
        * And finally writing the results to the output file.
         */

        List<Player> players = new ArrayList<>();
        Map<String, Double[]> matchRates = new HashMap<>();
        long casinoBalance = 0;

        // Read match data and store rates in matchRates map
        File matchData = new File("src/match_data.txt");
        try (Scanner matchReader = new Scanner(matchData, "UTF-8")) {
            while (matchReader.hasNextLine()) {
                String matchRow = matchReader.nextLine();
                String[] matchParts = matchRow.split(",");
                String matchId = matchParts[0];
                double rateA = Double.parseDouble(matchParts[1]);
                double rateB = Double.parseDouble(matchParts[2]);
                matchRates.put(matchId, new Double[]{rateA, rateB});
            }
        }

        // Read player data and process actions
        File playerData = new File("src/player_data.txt");
        try (Scanner playerReader = new Scanner(playerData, "UTF-8")) {
            while (playerReader.hasNextLine()) {
                String row = playerReader.nextLine();
                Actions playerAction = parsePlayerAction(row);

                Player player = getPlayerOrCreate(players, playerAction.getPlayerId());

                switch (playerAction.getAction()) {
                    case "DEPOSIT":
                        player.deposit(playerAction.getCoinAmount());
                        break;
                    case "WITHDRAW":
                        if (!player.withdraw(playerAction.getCoinAmount())) {
                            // Illegal action: not enough balance for withdrawal
                            player = markAsIllegal(player, row);
                        }
                        break;
                    case "BET":
                        Double[] rates = matchRates.get(playerAction.getMatchId());
                        if (rates != null) {
                            if (!player.placeBet(playerAction.getCoinAmount(), rates[0], rates[1],playerAction.getBetSide())) {
                                // Illegal action: not enough balance for the bet
                                player = markAsIllegal(player, row);
                            }else // edukas
                            {
                                casinoBalance = player.setWin(playerAction.getCoinAmount(), rates[0], rates[1],playerAction.getBetSide(),casinoBalance);
                            }
                        } else {
                            // Illegal action: invalid matchId
                            player = markAsIllegal(player, row);
                        }
                        break;
                    default:
                        // Illegal action: unknown action
                        player = markAsIllegal(player, row);
                        break;
                }
            }
        }

        // Write results to "result.txt"
        try (PrintWriter writer = new PrintWriter("src/result.txt")) {
            writeLegitimatePlayers(writer, players);
            writer.println();
            writeIllegitimatePlayers(writer, players);
            writer.println();
            writeCasinoBalance(writer, casinoBalance);
        }
    }

    private static Actions parsePlayerAction(String row) {
        /*
        This is for reading the player action
        it also slices the input to parts by positioning
        returns sliced and assigned variables
         */

        String[] parts = row.split(",");
        String playerId = parts[0];
        String action = parts[1];
        String matchId = parts.length > 2 ? parts[2] : null;
        int coinAmount = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
        String betSide = parts.length > 4 ? parts[4] : null;
        return new Actions(playerId, action, matchId, coinAmount, betSide);
    }

    private static Player getPlayerOrCreate(List<Player> players, String playerId) {
        /*
        This is for checking if a player already exists or needs to be added as a player
         */
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return player;
            }
        }
        // If the player does not exist, create a new one
        Player newPlayer = new Player(playerId);
        players.add(newPlayer);
        return newPlayer;
    }

    private static Player markAsIllegal(Player player, String row) {
        /*
        Marking the player as a Illegal, if they have commited a illegal action.
         */
        player.markAsIllegal(row);
        return player;
    }

    private static void writeLegitimatePlayers(PrintWriter writer, List<Player> players) {
        /*
        Writing the players who are so called legit to the file.
         */
        writer.println("Legitimate player IDs followed with their final balance and their betting win rate:");

        players.stream()
                .filter(player -> !player.hasIllegalAction())
                .sorted(Comparator.comparing(Player::getPlayerId))
                .forEach(player -> writer.printf("%s %d %.2f%n", player.getPlayerId(), player.getBalance(), player.getWinRate()));
    }

    private static void writeIllegitimatePlayers(PrintWriter writer, List<Player> players) {
        /*
        Writing the Illegal players to the result file
         */
        writer.println("Illegitimate players by their first illegal operation:");

        players.stream()
                .filter(player-> player.hasIllegalAction())
                .sorted(Comparator.comparing(Player::getPlayerId))
                .forEach(player -> writer.println(player.getFirstIllegalOperation()));
    }

    private static void writeCasinoBalance(PrintWriter writer, long casinoBalance) {
        /*
        This writes the casinos balance to the results.txt
         */
        writer.printf("Coin changes in casino host balance: %d%n", casinoBalance);
    }
}