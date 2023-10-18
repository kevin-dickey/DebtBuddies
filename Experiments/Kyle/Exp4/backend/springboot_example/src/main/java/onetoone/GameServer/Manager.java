package onetoone.GameServer;

import onetoone.GameServer.Communication.Events.ServerEvent;
import onetoone.GameServer.Games.GoFish.GoFishManager;
import onetoone.GameServer.PlayerClasses.Player;
import onetoone.GameServer.Communication.Responses.Response;
import onetoone.GameServer.Games.TexasHoldEm.TexasHoldEmManager;

public class Manager {

    public static Response getResponse(String game, Player player, ServerEvent serverEvent){
        switch(game){
            case "texasholdem":
                return TexasHoldEmManager.getResponse(player, serverEvent);
            case "gofish":
                //return GoFishManager.getResponse(player, serverEvent.getAction());
            default:
                return new Response();
        }
    }
}
