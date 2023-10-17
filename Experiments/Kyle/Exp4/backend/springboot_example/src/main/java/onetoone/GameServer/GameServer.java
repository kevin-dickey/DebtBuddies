package onetoone.GameServer;

import java.io.IOException;
import java.util.*;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.sql.Types.NULL;


/**
 * Represents a WebSocket chat server for handling real-time communication
 * between users. Each user connects to the server using their unique
 * username.
 *
 * This class is annotated with Spring's `@ServerEndpoint` and `@Component`
 * annotations, making it a WebSocket endpoint that can handle WebSocket
 * connections at the "/chat/{username}" endpoint.
 *
 * Example URL: ws://localhost:8080/chat/username
 *
 * The server provides functionality for broadcasting messages to all connected
 * users and sending messages to specific users.
 */
@ServerEndpoint("/gameserver/{game}/{username}")
@Component
public class GameServer {

    private Gson gson = new Gson();

    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key

    private static Map < String, TexasHoldEmPlayer> usernamePlayerMap = new Hashtable < > ();
    private static Map < Session, TexasHoldEmPlayer > sessionPlayerMap = new Hashtable < > ();
    private static Map < TexasHoldEmPlayer , Session > playerSessionMap = new Hashtable < > ();
    private static List<Player> players = new ArrayList<>();
    private static int num_players = 0;

    private final Logger logger = LoggerFactory.getLogger(TexasHoldEmServer.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected user.
     * @param username username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("game") String game, @PathParam("username") String username) throws IOException {

        // server side log
        logger.info("[onOpen] " + username);

        // Handle the case of a duplicate username
        if (usernamePlayerMap.containsKey(username)) {
            session.getBasicRemote().sendText("Username already exists");
            session.close();
        }
        else {
            TexasHoldEmPlayer player = new TexasHoldEmPlayer(username);
            num_players++;
            players.add(player);
            // map current session with username
            sessionPlayerMap.put(session, player);

            // map current username with session
            playerSessionMap.put(player, session);

            usernamePlayerMap.put(username, player);

            // send to the user joining in
            sendMessageToParticularUser(username, "Welcome to the chat server, "+username);

            // send to everyone in the chat
            //broadcast("User: " + username + " has Joined the Chat");
        }
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session The WebSocket session representing the client's connection.
     * @param message The message received from the client.
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        /**
         * {"game":*game*,"gameid":*gameid*,*action*:value}
         */

        Player player = sessionPlayerMap.get(session);

        Action action = gson.fromJson(message, Action.class);
        String game = action.getGame();

        Response response = Manager.getResponse(player, action);

        List<Player> players_to_message = response.getPlayers();
        for(int i = 0; i < players_to_message.size(); i++){
            String msg;
            if(response.getMessages().size() == 1){
                msg = response.getMessages().get(0);
            }else{
                msg = response.getMessages().get(i);
            }
            sendMessageToParticularUser(players_to_message.get(i).toString(), msg);
        }
    }

    /**
     * Handles the closure of a WebSocket connection.
     *
     * @param session The WebSocket session that is being closed.
     */
    @OnClose
    public void onClose(Session session) throws IOException {

        // get the username from session-username mapping
        Player player = sessionPlayerMap.get(session);

        // server side log
        logger.info("[onClose] " + player.toString());

        // remove user from memory mappings
        usernamePlayerMap.remove(player.toString());
        sessionPlayerMap.remove(session);
        playerSessionMap.remove(player);
        players.remove(sessionPlayerMap.get(session));

        num_players--;

        // send the message to chat
        broadcast(player.toString() + " disconnected");
    }

    /**
     * Handles WebSocket errors that occur during the connection.
     *
     * @param session   The WebSocket session where the error occurred.
     * @param throwable The Throwable representing the error condition.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {

        // get the username from session-username mapping
        String username = sessionPlayerMap.get(session).toString();

        // do error handling here
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }

    /**
     * Sends a message to a specific user in the chat (DM).
     *
     * @param username The username of the recipient.
     * @param message  The message to be sent.
     */
    private void sendMessageToParticularUser(String username, String message) {
        try {
            playerSessionMap.get(usernamePlayerMap.get(username)).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all users in the chat.
     *
     * @param message The message to be broadcasted to all users.
     */
    private void broadcast(String message) {
        sessionPlayerMap.forEach((session, player) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
}