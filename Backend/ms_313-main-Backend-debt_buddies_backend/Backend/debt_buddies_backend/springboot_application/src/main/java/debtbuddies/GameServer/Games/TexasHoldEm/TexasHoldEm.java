package debtbuddies.GameServer.Games.TexasHoldEm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import debtbuddies.GameServer.Communication.ServerEvent;
import debtbuddies.GameServer.DeckLibrary.*;
import debtbuddies.GameServer.Games.Game;
import debtbuddies.GameServer.Communication.Response;
import debtbuddies.GameServer.Games.GameInterface;
import debtbuddies.GameServer.Games.TexasHoldEm.TexasHoldEmInfo.*;
import debtbuddies.GameServer.PlayerClasses.Group;
import debtbuddies.GameServer.PlayerClasses.User;

public class TexasHoldEm extends Game<TexasHoldEmUser> implements GameInterface<TexasHoldEmUser, TexasHoldEm> {

    private int QUEUE_SIZE = 3;
    private final int BASE_ANTE = 10;
    private Deck deck;
    private List<Card> pit;
    private int pot;
    private int ante;
    private int stage;
    private int p_index = 0;
    private TexasHoldEmUser target_player;
    private TexasHoldEmUser final_player;

    private String last_move;

    public TexasHoldEm(List<User> users, int gameId){
        super(users, gameId);
    }

    public TexasHoldEm(){
        super();
        queue_size = QUEUE_SIZE;
    }

    @Override
    public TexasHoldEm getNewGame(Group lobby, int gameId){
        return new TexasHoldEm(lobby.getUsers(), gameId);
    }

    @Override
    protected void initializeGame(){
        num_users = users.size();
        pit = new ArrayList<>();
        deck = new Deck();
        ante = BASE_ANTE;
        running = 1;
        stage = 0;
        pot = 0;
        convertUsers();
        deal_hole();
        smallAndBigBlind();
    }

    @Override
    public TexasHoldEmUser getNewUser(User user){
        return new TexasHoldEmUser(user);
    }

    public void getResponse(User user, ServerEvent serverEvent){

        TexasHoldEmUser player = userPlayerMap.get(user);

        if(running == 0 && Objects.equals(serverEvent.getAction(), "start")){

            initializeGame();

            for(TexasHoldEmUser o_player : players){
                sendStartInfo(o_player);
                sendPlayerInfo(o_player);
            }

        }else if(running == 1 && player == target_player){
            switch(serverEvent.getAction()) {
                case "fold":
                    fold(player);
                    break;
                case "call":
                    call(player);
                    break;
                case "raise":
                    raise(player, serverEvent.getValue());
                    break;
                default:
                    return;
            }

            if(getActivePlayers() == 1){
                TexasHoldEmUser winner = end_game();
                sendEndInfo(winner);
                return;
            }else{
                sendPlayerInfo(player);
            }

            target_player = nextTargetPlayer();

            int pre_stage = stage;

            if((Objects.equals(serverEvent.getAction(), "call") || Objects.equals(serverEvent.getAction(), "fold")) && target_player == final_player) {
                switch(++stage){
                    case 1:
                        flop();
                        break;
                    case 2:
                        turn();
                        break;
                    case 3:
                        river();
                        break;
                    default:
                        sendEndInfo(end_game());
                        return;
                }
            }

            if(stage != pre_stage){
                sendStageInfo();
                newRound();
            }

            sendTurnInfo();
        }
    }

    private void sendPlayerInfo(TexasHoldEmUser player){
        PlayerInfo playerInfo = new PlayerInfo(player.getBalance(), player.getBet(), player.getAnte());
        Response.addMessage(playerUserMap.get(player), "playerInfo", playerInfo);
    }

    private void sendStartInfo(TexasHoldEmUser player){
        StartInfo startInfo = new StartInfo(player.getHand(), target_player.toString(), pot, ante);
        Response.addMessage(playerUserMap.get(player), "startInfo", startInfo);
    }

    private void sendEndInfo(TexasHoldEmUser winner){
        EndInfo endInfo = new EndInfo(winner.toString(), winner.getHigh_hand().toString(), winner.getHand());
        Response.addMessage(users, "endInfo", endInfo);
    }

    private void sendTurnInfo(){
        TurnInfo turnInfo = new TurnInfo(last_move, target_player.toString(), pot, ante);
        Response.addMessage(users, "turnInfo", turnInfo);
    }

    private void sendStageInfo(){
        StageInfo stageInfo = new StageInfo(pit);
        Response.addMessage(users, "stageInfo", stageInfo);
    }

    private void newRound(){
        for(TexasHoldEmUser player : players){
            player.setAnte(0);
        }
        ante = 0;
    }

    private void smallAndBigBlind(){

        players.get(p_index).placeBet(5);

        p_index = (p_index + 1) % players.size();

        players.get(p_index).placeBet(10);

        pot = 15;
        ante = BASE_ANTE;

        target_player = players.get((p_index + 1) % num_users);
        final_player = target_player;
    }

    private TexasHoldEmUser end_game(){
        running = 0;
        if(getActivePlayers() == 1){
            for(TexasHoldEmUser player : players){
                if(!player.foldStatus()){
                    return player;
                }
            }
        }
        return decideWinner();
    }

    private void fold(TexasHoldEmUser player){
        player.foldHand();
        last_move = "fold";
    }

    private void call(TexasHoldEmUser player){
        int increase = ante - player.getAnte();
        pot += player.placeBet(increase);
        last_move = "call";
    }

    private void raise(TexasHoldEmUser player, int ante_increase){
        ante += ante_increase;
        int increase = ante - player.getAnte();
        pot += player.placeBet(increase);
        final_player = player;
        last_move = "raise";
    }

    private TexasHoldEmUser nextTargetPlayer(){
        TexasHoldEmUser temp = players.get((players.indexOf(target_player) + 1) % players.size());
        while(temp.foldStatus()){
            temp = players.get((players.indexOf(temp) + 1) % players.size());
        }
        return temp;
    }

    private int getActivePlayers(){
        int active = 0;
        for(TexasHoldEmUser player : players){
            if(!player.foldStatus()){ active++; }
        }
        return active;
    }

    private List<TexasHoldEmUser> getActiveUsers(){
        List<TexasHoldEmUser> active = new ArrayList<>();
        for(TexasHoldEmUser user : players){
            if(!user.foldStatus()){
                active.add(user);
            }
        }
        return active;
    }

    public void deal_hole(){
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < num_users; j++){
                players.get(j).draw(deck);
            }
        }
    }
    private void flop(){
        for(int i = 0; i < 3; i++){
            pit.add(deck.draw());
        }
    }
    private void turn(){
        pit.add(deck.draw());
    }
    private void river(){
        pit.add(deck.draw());
    }

    public List<TexasHoldEmUser> decideWinners(){
        List<TexasHoldEmUser> activePlayers = getActiveUsers();
        if(activePlayers.size() == 1){
            return activePlayers;
        }

        int high_index = 0;
        PokerHands high_hand = PokerHands.LOW;

        for(int i = 0; i < activePlayers.size(); i++){
            PokerHands player_high = getHigh(i);
            if(player_high.getValue() > high_hand.getValue()){
                high_index = i;
                high_hand = player_high;
            }else if(player_high.getValue() == high_hand.getValue()){

            }
        }
    }

    public TexasHoldEmUser decideWinner(){
        int high_index = 0;
        PokerHands high_hand = PokerHands.LOW;

        for(int i = 0; i < num_users; i++){
            if(players.get(i).foldStatus()){ continue; }
            PokerHands player_high = getHigh(i);
            if(player_high.getValue() > high_hand.getValue()){
                high_index = i;
                high_hand = player_high;
            }else if(player_high.getValue() == high_hand.getValue()){
                TexasHoldEmUser player1 = players.get(high_index);
                TexasHoldEmUser player2 = players.get(i);
                try {
                    int winner = tiebreaker(player1, player2);
                    if(winner == 0){
                        high_index = players.indexOf(player1);
                    }else if(winner == 1){
                        high_index = players.indexOf(player2);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return players.get(high_index);
    }
    private int tiebreaker(TexasHoldEmUser player1, TexasHoldEmUser player2){
        List<Card> p1_total = getTotal(player1.getHand(), pit);
        List<Card> p2_total = getTotal(player2.getHand(), pit);
        Deck.CardManager.sortCards(p1_total);
        Deck.CardManager.sortCards(p2_total);
        removeDuplicates(p1_total, p2_total);
        for(int i = p1_total.size() - 1; i >= 0; i--){
            if(p1_total.get(i).getRank() > p2_total.get(i).getRank()){
                return 0;
            }else if(p2_total.get(i).getRank() > p1_total.get(i).getRank()){
                return 1;
            }
        }
        return 2;
    }

    private void removeDuplicates(List<Card> l0, List<Card> l1){
        List<Card> overlap = new ArrayList<>();
        for(Card card : l0){
            for(int i = 0; i < l1.size(); i++){
                if(card.getRank() == l1.get(i).getRank()){
                    overlap.add(l1.get(i));
                    l1.remove(l1.get(i));
                    break;
                }
            }
        }
        if(overlap.isEmpty()){ return;}
        for(Card card : overlap){
            for(int i = 0; i < l0.size(); i++){
                if(card.getRank() == l0.get(i).getRank()){
                    l0.remove(l0.get(i));
                    break;
                }
            }
        }
    }

    private List<Card> getTotal(List<Card> deck1, List<Card> deck2){
        List<Card> total = new ArrayList<>();
        total.addAll(deck1);
        total.addAll(deck2);
        return total;
    }

    private PokerHands getHigh(int index){

        List<Card> handAndPit = getTotal(players.get(index).getHand(), pit);

        Deck.CardManager.sortCards(handAndPit);

        PokerHands hh = convertHigh(handAndPit);

        players.get(index).setHigh_hand(hh);

        return hh;
    }

    private List<Integer> countNumbers(List<Card> cards){
        List<Integer> values = new ArrayList<>();

        int count = 1;

        for(int i = 1; i < cards.size(); i++){
            if(cards.get(i).getRank() == cards.get(i - 1).getRank()){
                count++;
            }else{
                values.add(count);
                count = 1;
            }
        }
        return values;
    }

    private PokerHands convertHigh(List<Card> cards){
        if(royalFlush(cards)){
            return PokerHands.ROYAL_FLUSH;
        }else if(straightFlush(cards)){
            return PokerHands.STRAIGHT_FLUSH;
        }else if(fourOfAKind(cards)){
            return PokerHands.FOUR_OF_A_KIND;
        }else if(fullHouse(cards)){
            return PokerHands.FULL_HOUSE;
        }else if(flush(cards)){
            return PokerHands.FLUSH;
        }else if(straight(cards)){
            return PokerHands.STRAIGHT;
        }else if(threeOfAKind(cards)) {
            return PokerHands.THREE_OF_A_KIND;
        }else if(twoPair(cards)){
            return PokerHands.TWO_PAIR;
        }else if(pair(cards)){
            return PokerHands.PAIR;
        }else{
            return getHighEnum(cards);
        }
    }

    private boolean royalFlush(List<Card> cards){
        List<Card> temp = new ArrayList<>();
        for (Card card : cards) {
            if (card.getRank() >= 10) {
                temp.add(card);
            }
        }
        if(temp.size() < 5){
            return false;
        }
        return straightFlush(temp);
    }

    private List<Card> checkRoyalFlush(List<Card> cards){
        List<Card> temp = new ArrayList<>();
        for(Card card : cards){
            if(card.getRank() >= 10){
                temp.add(card);
            }
        }
        if(temp.size() < 5){
            return new ArrayList<>();
        }
        return checkStraightFlush(temp);
    }

    private boolean straightFlush(List<Card> cards){
        for(int i = 0; i < cards.size() - 4; i++){
            int count = 1;
            for(int j = 1; j < 5; j++){
                if(Deck.CardManager.contains(cards, cards.get(i).getSuit(), cards.get(i).getRank() + j)){
                    if(++count == 5){ return true; }
                }
            }
        }
        return false;
    }

    private List<Card> checkStraightFlush(List<Card> cards){
        for(int i = 0; i < cards.size() - 4; i++){
            List<Card> temp = new ArrayList<>();
            temp.add(cards.get(i));
            for(int j = 1; j < 5; j++){
                if(cards.get(i).getSuit() == cards.get(i + j).getSuit() && cards.get(i).getRank() + j == cards.get(i + j).getRank()){
                    temp.add(cards.get(i + j));
                    if(temp.size() == 5){ return temp; }
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean fourOfAKind(List<Card> cards){

        List<Integer> values = countNumbers(cards);

        for(Integer value : values){
            if(values.get(value) == 4){ return true; }
        }

        return false;
    }

    private List<Card> checkFourOfAKind(List<Card> cards){
        List<Integer> values = countNumbers(cards);
        List<Card> temp = new ArrayList<>();

        for(Integer value : values){
            if(values.get(value) == 4){
                for(Card card : cards){
                    if(card.getRank() == value){
                        temp.add(card);
                    }
                }
                return temp;
            }
        }

        return new ArrayList<>();
    }

    private boolean fullHouse(List<Card> cards){

        List<Integer> values = countNumbers(cards);

        int twoCount = 0, threeCount = 0;

        for(Integer value : values){
            if(values.get(value) == 3){
                threeCount++;
            }else if(values.get(value) == 2){
                twoCount++;
            }
        }

        return threeCount == 2 || (threeCount == 1 && twoCount >= 1);
    }

    private List<Card> checkFullHouse(List<Card> cards){
        List<Integer> values = countNumbers(cards);

        List<Card> temp = new ArrayList<>();

        int twoCount = 0, threeCount = 0;

        List<Integer> threes = new ArrayList<>();
        List<Integer> twos = new ArrayList<>();

        for(Integer value : values){
            if(values.get(value) == 3){
                threeCount++;
                threes.add(value);
            }else if(values.get(value) == 2){
                twoCount++;
                twos.add(value);
            }
        }

        if(threeCount == 2 || (threeCount == 1 && twoCount >= 1)) {

            if (threes.size() > 1) {
                if (threes.get(0) > threes.get(1)) {
                    for (Card card : cards) {
                        if (card.getRank() == threes.get(0)) {
                            temp.add(card);
                        }
                    }
                    if(twoCount > 0){
                        int maxnum = threes.get(1);
                        for(Integer h : twos){
                            if(h > maxnum){
                                maxnum = h;
                            }
                        }
                        for(Card card : cards){
                            if(card.getRank() == maxnum){
                                temp.add(card);
                                if(temp.size() == 5){
                                    return temp;
                                }
                            }
                        }
                    }
                } else {
                    for (Card card : cards) {
                        if (card.getRank() == threes.get(1)){
                            temp.add(card);
                        }
                    }
                    if(twoCount > 0){
                        int maxnum = threes.get(0);
                        for(Integer h : twos){
                            if(h > maxnum){
                                maxnum = h;
                            }
                        }
                        for(Card card : cards){
                            if(card.getRank() == maxnum){
                                temp.add(card);
                                if(temp.size() == 5){
                                    return temp;
                                }
                            }
                        }
                    }
                }

            }else{
                int targ = threes.get(0);
                for(Card card : cards){
                    if(card.getRank() == targ){
                        temp.add(card);
                    }
                }
                int mx = twos.get(0);
                if(twos.size() > 1){
                    for(Integer val : twos){
                        if(val > mx){
                            mx = val;
                        }
                    }
                }
                for(Card card : cards){
                    if(card.getRank() == mx){
                        temp.add(card);
                    }
                }
                return temp;
            }
        }

        return new ArrayList<>();
    }

    private boolean flush(List<Card> cards){
        for(Suit suit : Suit.values()){
            int count = 0;
            for(Card card : cards){
                if(card.getSuit() == suit){ count++; }
            }
            if(count >= 5){ return true; }
        }
        return false;
    }

    private List<Card> checkFlush(List<Card> cards){
        for(Suit suit : Suit.values()){
            List<Card> temp = new ArrayList<>();
            for(int i = cards.size() - 1; i >= 0; i--){
                if(cards.get(i).getSuit() == suit){ temp.add(cards.get(i)); }
            }
            if(temp.size() >= 5){ return temp; }
        }
        return new ArrayList<>();
    }

    private boolean straight(List<Card> cards){
        for(int i = 0; i < cards.size() - 4; i++){
            int count = 1;
            for(int j = 1; j < 5; j++){
                if(Deck.CardManager.contains(cards, cards.get(i).getRank() + j)){
                    if(++count == 5){ return true; }
                }
            }
        }
        return false;
    }

    private List<Card> checkStraight(List<Card> cards){
        for(int i = 0; i < cards.size() - 4; i++){
            List<Card> temp = new ArrayList<>();
            for(int j = 1; j < 5; j++){
                if(cards.get(i).getRank() + j == cards.get(i + j).getRank()){
                    temp.add(cards.get(i + j));
                    if(temp.size() == 5){
                        return temp;
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean threeOfAKind(List<Card> cards){
        List<Integer> values = countNumbers(cards);

        for(Integer i : values){
            if(values.get(i) == 3){ return true; }
        }

        return false;
    }

    private List<Card> checkThreeOfAKind(List<Card> cards){
        List<Integer> values = countNumbers(cards);

        for(Integer i : values){
            if(values.get(i) == 3){
                List<Card> temp = new ArrayList<>();
                for(Card card : cards){
                    if(card.getRank() == i){
                        temp.add(card);
                    }
                }
                return temp;
            }
        }

        return new ArrayList<>();
    }

    private boolean twoPair(List<Card> cards){
        List<Integer> values = countNumbers(cards);
        int count = 0;

        for(Integer value : values){
            if(values.get(value) == 2){ count++; }
        }

        return count >= 2;
    }

    private List<Card> checkTwoPair(List<Card> cards){
        List<Integer> values = countNumbers(cards);
        List<Card> temp = new ArrayList<>();
        for(Integer value : values){
            if(values.get(value) == 2){
                for(Card card : cards){
                    if(card.getRank() == value){
                        temp.add(card);
                        if(temp.size() == 4){
                            return temp;
                        }
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private boolean pair(List<Card> cards){
        List<Integer> values = countNumbers(cards);

        for(Integer i : values){
            if(values.get(i) == 2){ return true; }
        }

        return false;
    }

    private List<Card> checkPair(List<Card> cards){
        List<Integer> values = countNumbers(cards);

        List<Card> temp = new ArrayList<>();

        for(Integer i : values){
            if(values.get(i) == 2){
                for(Card card : cards){
                    if(card.getRank() == i){
                        temp.add(card);
                    }
                }
                return temp;
            }
        }

        return new ArrayList<>();
    }

    private PokerHands getHighEnum(List<Card> cards){
        switch(cards.get(cards.size() - 1).getRank()){
            case 2:
                return PokerHands.HIGH_TWO;
            case 3:
                return PokerHands.HIGH_THREE;
            case 4:
                return PokerHands.HIGH_FOUR;
            case 5:
                return PokerHands.HIGH_FIVE;
            case 6:
                return PokerHands.HIGH_SIX;
            case 7:
                return PokerHands.HIGH_SEVEN;
            case 8:
                return PokerHands.HIGH_EIGHT;
            case 9:
                return PokerHands.HIGH_NINE;
            case 10:
                return PokerHands.HIGH_TEN;
            case 11:
                return PokerHands.HIGH_JACK;
            case 12:
                return PokerHands.HIGH_QUEEN;
            case 13:
                return PokerHands.HIGH_KING;
            default:
                return PokerHands.HIGH_ACE;
        }
    }

    @Override
    public String toString(){
        return Integer.toString(gameId);
    }

}
