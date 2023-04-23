package micro.price;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MicroPriceService {
    private String multicastGroupAddress;
    private int multicastPort;
    private String networkAdapter;
    private String tradingSystemExchangeAHost;
    private int tradingSystemExchangeAPort;

    MicroPriceCalculator microPriceCalculator;
    Map<Integer, OrderBookImpl> orderBooks;

    public static void main(String[] args) throws IOException {
        String multicastGroupAddress = "224.0.0.1";
        int multicastPort = 12345;
        String networkAdapterName = "en0";
        String tradingSystemHost = "localhost";
        int tradingSystemPort = 23456;

        MicroPriceService microPriceService = new MicroPriceService(multicastGroupAddress, multicastPort,
                networkAdapterName, tradingSystemHost, tradingSystemPort);
        microPriceService.startService();
    }

    public MicroPriceService(String multicastGroupAddress,
                              int multicastPort,
                              String networkAdapter,
                              String tradingSystemExchangeAHost,
                              int tradingSystemExchangeAPort) {
        this.multicastGroupAddress = multicastGroupAddress;
        this.multicastPort = multicastPort;
        this.networkAdapter = networkAdapter;
        this.tradingSystemExchangeAHost = tradingSystemExchangeAHost;
        this.tradingSystemExchangeAPort = tradingSystemExchangeAPort;
        this.microPriceCalculator = new MicroPriceCalculator();
        this.orderBooks = new HashMap<>();
    }

    public void startService() throws IOException {
        InetAddress ip = InetAddress.getByName(multicastGroupAddress);
        NetworkInterface networkInterface = NetworkInterface.getByName(networkAdapter);
        MulticastSocket socket = new MulticastSocket(multicastPort);
        socket.setNetworkInterface(networkInterface);
        socket.joinGroup(new InetSocketAddress(ip, multicastPort), networkInterface);

        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
            socket.receive(packet);
            ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
            int messageLength = buffer.getInt();
            int securityId = buffer.getInt();
            int numberUpdates = buffer.getShort();
            OrderBookImpl orderBook = orderBooks.computeIfAbsent(securityId, k -> new OrderBookImpl(10));
            for(int i = 0; i < numberUpdates; i++) {
                int level = buffer.getInt();
                int side = buffer.getInt();
                long scaledPrice = buffer.getLong(); // 8 bytes
                double price = scaledPrice/1e9;
                int quantity = buffer.getInt();
                if(side == 0)
                    orderBook.updateBid(level, price, quantity);
                else
                    orderBook.updateAsk(level, price, quantity);
                double microPrice = microPriceCalculator.calculateMicroPrice(orderBook);
                sendMicroPriceToExchangeA(securityId, microPrice);
            }
        }
    }

    public void sendMicroPriceToExchangeA(int securityId, double microPrice) throws IOException {
        Socket socket = new Socket(tradingSystemExchangeAHost, tradingSystemExchangeAPort);
        long scaledMicroPrice = (long) (microPrice * 1e9);
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putInt(securityId);
        buffer.putLong(scaledMicroPrice);
        socket.getOutputStream().write(buffer.array());
    }
}
