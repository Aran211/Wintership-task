class Player {
    private String playerId;
    private long balance;
    private int placedBets;
    private int wonBets;

    private boolean hasIllegalAction;
    private String firstIllegalOperation;

    public Player(String playerId) {
        //constructor
        this.playerId = playerId;
        this.balance = 0;
        this.placedBets = 0;
        this.wonBets = 0;
        this.hasIllegalAction = false;
        this.firstIllegalOperation = null;
    }

    public String getPlayerId() {
        return playerId;
    }

    public long getBalance() {
        return balance;
    }

    public int getPlacedBets() {
        return placedBets;
    }

    public int getWonBets() {
        return wonBets;
    }

    public void deposit(int amount) {
        this.balance += amount;
    }

    public boolean withdraw(int amount) {
        if (amount <= balance) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public boolean placeBet(int betAmount, Double rateA, Double rateB, String matchResult) {
        /*
        This for the bet
        it checks if the bet amount isn´t bigger than the players balance, if it is returns false
        If the bet is smaller than or equal to the balance it deducts the balance from this.balance and adds to placed bets

         */
        if (betAmount > balance) {
            return false; // Insufficient funds for the bet
        }

        this.balance -= betAmount;
        this.placedBets++;

        return true;
    }

    public long setWin(int betAmount,Double rateA, Double rateB, String matchResult, long casinoBalance)
    {
        // Check if the bet is on the winning side
        // I got the email that casino balance starts from 0 so i assume the casino may end in negative with these bets
        if (matchResult.equals("A")) {
            int winnings = (int) (betAmount * rateA);
            this.balance += winnings;
            this.wonBets++;
            casinoBalance -= winnings;
            System.out.println("Bet won"+ " " + winnings + ", " +"Casino Balance:" + casinoBalance);
        } else if (matchResult.equals("B")) {
            casinoBalance += betAmount;
            System.out.println("Bet Lost"+ betAmount+", "+ "Casino Balance after the bet:" + casinoBalance + ", " +"Size of the bet:" + betAmount);
        } else if (matchResult.equals("D")) {
            this.balance += betAmount;
            System.out.println("Bet ended in a draw");
        }
        return casinoBalance;
    }


    public double getWinRate() {
        return placedBets > 0 ? (double) wonBets / placedBets : 0.0; //If placed bets is more than 0 it puts bet true
    }

    public boolean hasIllegalAction() {
        return hasIllegalAction;
    }

    public String getFirstIllegalOperation() {
        return firstIllegalOperation;
    }

    public void markAsIllegal(String operation) {
        /*
        This is for marking as Illegal
         */
        this.hasIllegalAction = true;
        if (firstIllegalOperation == null) {
            firstIllegalOperation = operation;
        }
    }
}