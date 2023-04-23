package micro.price;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class MicroPriceService1 {
    private MicroPriceCalculator microPriceCalculator;
    private String multicastGroupAddress;
    private int multicastPort;
    private String networkAdapter;
    private String tradingSystemExchangeAHost;
    private int tradingSystemExchangeAPort;
    private OrderBookImpl1 orderBook;
    private int BUF_LENGTH = 1024;

    public MicroPriceService1(String multicastGroupAddress,
                              int multicastPort,
                              String networkAdapter,
                              String tradingSystemExchangeAHost,
                              int tradingSystemExchangeAPort) {
        this.multicastGroupAddress = multicastGroupAddress;
        this.multicastPort = multicastPort;
        this.networkAdapter = networkAdapter;
        this.tradingSystemExchangeAHost = tradingSystemExchangeAHost;
        this.tradingSystemExchangeAPort = tradingSystemExchangeAPort;
        this.orderBook = new OrderBookImpl1(0); // Create initial OrderBook object
    }

    public void start() {
        MulticastSocket socket = null;
        DatagramPacket packet = null;
        // byte[] buf = new byte[1024];
        try {
            InetAddress ip = InetAddress.getByName(multicastGroupAddress);
            socket = new MulticastSocket(multicastPort);
            NetworkInterface networkInterface = NetworkInterface.getByName(networkAdapter);
            socket.setNetworkInterface(networkInterface);
            socket.joinGroup(new InetSocketAddress(ip, multicastPort), networkInterface);

            while (true) {
                //buf = new byte[1024];
                byte[] buf = new byte[BUF_LENGTH];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                //parse
                int securityId = ByteBuffer.wrap(packet.getData(), 4, 4).getInt();
                int numUpdates = ByteBuffer.wrap(packet.getData(), 8, 2).getShort();

                for (int i = 0; i < numUpdates; i++) {
                    int offset = 10 + i * 16;
                    int level = ByteBuffer.wrap(packet.getData(), offset, 1).get();
                    int side = ByteBuffer.wrap(packet.getData(), offset + 1, 1).get();
                    long scaledPrice = ByteBuffer.wrap(packet.getData(), offset + 2, 8).getLong();
                    int qty = ByteBuffer.wrap(packet.getData(), offset + 10, 4).getInt();
                    orderBook = new OrderBookImpl1(level);
                    orderBook.updateOrderBook(level, side, scaledPrice, qty);
                    double microPrice = microPriceCalculator.calculateMicroPrice(orderBook);
                    sendMicroPriceToExchangeA(securityId, microPrice);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close(); // Close the MulticastSocket
            }
            if (packet != null) {
                packet.setData(null); // Clear the packet data
                packet.setLength(BUF_LENGTH); // Reset the packet length
            }
        }
    }

    public void sendMicroPriceToExchangeA(int securityId, double microPrice) throws IOException {
        Socket socket = new Socket(tradingSystemExchangeAHost, tradingSystemExchangeAPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(securityId);
        out.writeLong((long) (microPrice * 1e9));
        out.flush();
        socket.close();
    }


    public void setMicroPriceCalculator(MicroPriceCalculator microPriceCalculator) {
        this.microPriceCalculator = microPriceCalculator;
    }

    public void setOrderBook(OrderBookImpl1 orderBook) {
        this.orderBook = orderBook;
    }
}
