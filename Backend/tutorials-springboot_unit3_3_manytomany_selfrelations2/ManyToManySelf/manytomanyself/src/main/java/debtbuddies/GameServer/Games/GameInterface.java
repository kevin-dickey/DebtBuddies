package debtbuddies.GameServer.Games;

import debtbuddies.GameServer.Communication.ServerEvent;
import debtbuddies.GameServer.PlayerClasses.Group;
import debtbuddies.GameServer.PlayerClasses.User;

public interface GameInterface <T, K>{

    void getResponse(User user, ServerEvent serverEvent);

    K getNewGame(Group queue, int gameId);

    T getNewUser(User user);

    int getQueueSize();

}