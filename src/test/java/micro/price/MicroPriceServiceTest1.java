package micro.price;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MicroPriceServiceTest1 {
//    private MicroPriceService microPriceService;
    private MicroPriceCalculator mockMicroPriceCalculator;
    private String multicastGroupAddress;
    private int multicastPort;
    private String networkAdapter;
    private String tradingSystemExchangeAHost;
    private int tradingSystemExchangeAPort;

    @InjectMocks
    private MicroPriceService1 microPriceService;

    @Mock
    private MulticastSocket mockMulticastSocket;

    @BeforeEach
    void setUp() {
        multicastGroupAddress = "123.0.3.1";
        multicastPort = 1234;
        networkAdapter = "netAdapter";
        tradingSystemExchangeAHost = "124.0.1.1";
        tradingSystemExchangeAPort = 2345;
        microPriceService = new MicroPriceService1(multicastGroupAddress,
                                                multicastPort,
                                                networkAdapter,
                                                tradingSystemExchangeAHost,
                                                tradingSystemExchangeAPort);
        mockMicroPriceCalculator = mock(MicroPriceCalculator.class);
    }

//    @Test
//    void testStart() throws IOException {
//        OrderBookImpl mockOrderBook = mock(OrderBookImpl.class);
//        DatagramPacket mockPacket = mock(DatagramPacket.class);
//        MulticastSocket mockSocket = mock(MulticastSocket.class);
//        when(mockPacket.getData()).thenReturn(new byte[1024]);
//        when(mockPacket.getLength()).thenReturn(14);
//
//        doAnswer(invocation -> {
//            DatagramPacket packetArg = invocation.getArgument(0);
//            // Set data in the captured DatagramPacket to simulate receive() method
//            // behavior
//            byte[] data = "testData".getBytes();
//            System.arraycopy(data, 0, packetArg.getData(), packetArg.getOffset(), data.length);
//            packetArg.setLength(data.length);
//            return null;
//        }).when(mockSocket).receive(any(DatagramPacket.class));
//
//        when(mockMicroPriceCalculator.calculateMicroPrice(mockOrderBook)).thenReturn(100.0);
//
//        // Call the start() method
//        microPriceService.start();
//
//        // Verify that the expected methods were called with the expected arguments
//        verify(mockMicroPriceCalculator, times(1)).calculateMicroPrice(mockOrderBook);
//        verify(microPriceService, times(1)).sendMicroPriceToExchangeA(anyInt(), anyDouble());
//
////        when(mockSocket.receive(mockPacket)).thenReturn(mockPacket);
////        when(mockMicroPriceCalculator.calculateMicroPrice(mockOrderBook)).thenReturn(100.0);
////        // Execute
////        microPriceService.start();
////
////        // Verify
////        verify(mockMicroPriceCalculator, times(1)).calculateMicroPrice(mockOrderBook);
////        verify(microPriceService, times(1)).sendMicroPriceToExchangeA(anyInt(), anyDouble());
//
//    }


    @Test
    public void testStart() throws Exception {
        int x = 1;
    }

    @Test
    void testSendMicroPriceToExchangeA() throws IOException {
        // Test setup
        Socket mockSocket = mock(Socket.class);
        DataOutputStream mockOutputStream = mock(DataOutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        // Execute
        microPriceService.sendMicroPriceToExchangeA(1, 100.0);

        // Verify
        verify(mockOutputStream, times(1)).writeInt(1);
        verify(mockOutputStream, times(1)).writeLong(100_000_000_000L);
        verify(mockOutputStream, times(1)).flush();
        verify(mockSocket, times(1)).close();
    }
}
