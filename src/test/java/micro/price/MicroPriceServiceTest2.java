package micro.price;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MicroPriceServiceTest2 {
    @Test
    public void testMicroPriceService() throws IOException, InterruptedException {
        String multicastGroupAddress = "224.0.0.1";
        int multicastPort = 5000;
        String networkAdapter = "eth0";
        int tradingSystemPort = findFreePort();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            try {
                MicroPriceService microPriceService = new MicroPriceService(multicastGroupAddress, multicastPort,
                        networkAdapter, "localhost", tradingSystemPort);
                microPriceService.startService();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ServerSocket serverSocket = new ServerSocket(tradingSystemPort);
        Socket socket = serverSocket.accept();

        byte[] buf = new byte[12];
        int bytesRead = socket.getInputStream().read(buf);
        assertEquals(12, bytesRead);

        ByteBuffer buffer = ByteBuffer.wrap(buf);
        int securityId = buffer.getInt();
        long scaledMicroPrice = buffer.getLong();

        // Validate the received securityId and scaledMicroPrice based on expected values from the feed.
        assertEquals(1, securityId);
        assertEquals(123456789L, scaledMicroPrice);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
