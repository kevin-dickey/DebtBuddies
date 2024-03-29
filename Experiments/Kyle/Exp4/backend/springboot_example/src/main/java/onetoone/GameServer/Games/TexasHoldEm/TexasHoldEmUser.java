package onetoone.GameServer.Games.TexasHoldEm;

import onetoone.GameServer.DeckLibrary.Card;
import onetoone.GameServer.DeckLibrary.Deck;
import onetoone.GameServer.PlayerClasses.User;

import java.util.ArrayList;
import java.util.List;

public class TexasHoldEmUser extends User {
    private List<Card> hand = new ArrayList<>();

    private int bet;

    private boolean fold;

    public TexasHoldEmUser(User user){
        super(user.toString());
        bet = 0;
        fold = false;
    }

    public TexasHoldEmUser(String username){
        super(username);
        bet = 0;
        fold = false;
    }

    public TexasHoldEmUser(){
        super();
        bet = 0;
        fold = false;
    }

    public Card draw(Deck deck){
        Card pick = deck.draw();
        hand.add(pick);
        return pick;
    }

    public List<Card> getHand(){
        return hand;
    }

    public String handToString(){
        StringBuilder s = new StringBuilder("\n");
        for(Card card : hand){
            s.append(card.toString()).append("\n");
        }
        return s.toString();
    }

    public void clearInventory(){
        this.hand = new ArrayList<>();
    }

    public void foldHand(){
        fold = true;
    }

    public boolean foldStatus(){
        return fold;
    }

    public int placeBet(int bet){
        this.bet += bet;
        return bet;
    }

    public int getBet(){
        return bet;
    }

    public void setBet(int bet){
        this.bet = bet;
    }

    public int cashout(int amount){
        balance += amount;
        return balance;
    }

}
