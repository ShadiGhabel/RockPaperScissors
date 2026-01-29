package core;

import module.abstraction.*;
import module.business.*;

public class DependencyInstaller {

    public static void install() {
        DIContainer.register(IServerService.class, ServerService.class);
        DIContainer.register(IGameService.class, GameService.class);
        DIContainer.register(IClientService.class, ClientService.class);
    }
}
