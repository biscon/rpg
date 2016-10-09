package dk.bison.rpg.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Created by bison on 08-10-2016.
 */

public class GameServer {
    private static GameServer _i = null;
    Server server;

    private GameServer() {
    }

    public static GameServer instance()
    {
        if(_i == null)
        {
            _i = new GameServer();
        }
        return _i;
    }

    public void start()
    {
        try {
            server.start();
            server.bind(54555, 54777);
            server.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    if (object instanceof SomeRequest) {
                        SomeRequest request = (SomeRequest)object;
                        System.out.println(request.text);

                        SomeResponse response = new SomeResponse();
                        response.text = "Thanks";
                        connection.sendTCP(response);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
