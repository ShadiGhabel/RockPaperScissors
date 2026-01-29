import core.DIContainer;
import core.DependencyInstaller;
import module.abstraction.IServerService;

public class RPSServer {
    public static void main(String[] args) {
        try {
            DependencyInstaller.install();

            IServerService serverService = DIContainer.resolve(IServerService.class);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                serverService.stop();
            }));

            serverService.start();

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}