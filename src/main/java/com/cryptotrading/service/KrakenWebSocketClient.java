package com.cryptotrading.service;

import com.cryptotrading.event.PriceUpdateEvent;
import com.cryptotrading.handler.CryptoWebSocketHandler;
import com.cryptotrading.model.CryptoPrice;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import jakarta.websocket.*;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@ClientEndpoint
@Service
public class KrakenWebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(KrakenWebSocketClient.class);
    private Session session;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private final Gson gson = new Gson();
    private final Map<String, CryptoPrice> cryptoPrices = new LinkedHashMap<>() {{
        put("BTC", new CryptoPrice("Bitcoin", "BTC", "0"));
        put("ETH", new CryptoPrice("Ethereum", "ETH", "0"));
        put("USDT", new CryptoPrice("Tether", "USDT", "0"));
        put("USDC", new CryptoPrice("USD Coin", "USDC", "0"));
        put("LINK", new CryptoPrice("Chainlink", "LINK", "0"));
        put("LTC", new CryptoPrice("Litecoin", "LTC", "0"));
        put("XRP", new CryptoPrice("XRP", "XRP", "0"));
        put("ADA", new CryptoPrice("Cardano", "ADA", "0"));
        put("SOL", new CryptoPrice("Solana", "SOL", "0"));
        put("MATIC", new CryptoPrice("Polygon", "MATIC", "0"));
        put("DOT", new CryptoPrice("Polkadot", "DOT", "0"));
        put("DAI", new CryptoPrice("Dai", "DAI", "0"));
        put("SHIB", new CryptoPrice("Shiba Inu", "SHIB", "0"));
        put("TRX", new CryptoPrice("TRON", "TRX", "0"));
        put("AVAX", new CryptoPrice("Avalanche", "AVAX", "0"));
        put("XLM", new CryptoPrice("Stellar", "XLM", "0"));
        put("BCH", new CryptoPrice("Bitcoin Cash", "BCH", "0"));
        put("FIL", new CryptoPrice("Filecoin", "FIL", "0"));
        put("AAVE", new CryptoPrice("Aave", "AAVE", "0"));
        put("SUSHI", new CryptoPrice("SushiSwap", "SUSHI", "0"));
    }};

    private static final List<String> CRYPTO_PAIRS = Arrays.asList(
        "XBT/USD", // Bitcoin (BTC)
        "ETH/USD", // Ethereum (ETH)
        "USDT/USD", // Tether (USDT)
        "USDC/USD", // USD Coin (USDC)
        "LINK/USD", // Chainlink (LINK)
        "LTC/USD", // Litecoin (LTC)
        "XRP/USD", // XRP (XRP)
        "ADA/USD", // Cardano (ADA)
        "SOL/USD", // Solana (SOL)
        "MATIC/USD", // Polygon (MATIC)
        "DOT/USD", // Polkadot (DOT)
        "DAI/USD", // Dai (DAI)
        "SHIB/USD", // Shiba Inu (SHIB)
        "TRX/USD", // TRON (TRX)
        "AVAX/USD", // Avalanche (AVAX)
        "XLM/USD", // Stellar (XLM)
        "BCH/USD",  // Bitcoin Cash (BCH)
        "FIL/USD",  // Filecoin (FIL)
        "AAVE/USD", // Aave (AAVE)
        "SUSHI/USD" // SushiSwap (SUSHI)
    );

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected to Kraken WebSocket, session ID: {}", session.getId());

        this.session = session;
        subscribeToTicker();
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            if (message.startsWith("[")) {
                JsonArray jsonArray = JsonParser.parseString(message).getAsJsonArray();
                JsonObject tickerData = jsonArray.get(1).getAsJsonObject();
                String pair = jsonArray.get(3).getAsString();

                JsonArray lastTradeData = tickerData.getAsJsonArray("c");
                String lastTradePrice = lastTradeData.get(0).getAsString();

                String symbol = getCryptoSymbol(pair);
                if (symbol.equals("UNK")) {
                    logger.warn("Unsupported pair received: {}", pair);
                    return;
                }

                if (cryptoPrices.containsKey(symbol)) {
                    CryptoPrice cryptoPrice = cryptoPrices.get(symbol);
                    cryptoPrice.setPrice(lastTradePrice);

                    eventPublisher.publishEvent(new PriceUpdateEvent(cryptoPrice));
                } else {
                    logger.warn("Symbol not found in cryptoPrices: {}", symbol);
                }
            } else if (message.startsWith("{")) {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

                if (jsonObject.has("event") && jsonObject.get("event").getAsString().equals("subscriptionStatus")) {
                    String status = jsonObject.get("status").getAsString();
                    if (status.equals("subscribed")) {
                        logger.info("Successfully subscribed to: " + jsonObject.get("pair").getAsString());
                    } else {
                        logger.warn("Subscription failed: " + jsonObject.get("errorMessage").getAsString());
                    }
                } else if (jsonObject.has("event") && jsonObject.get("event").getAsString().equals("heartbeat")) {
                    logger.debug("Heartbeat received");
                }
            } else {
                logger.debug("Received plain text message: {}", message);
            }
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse WebSocket message due to malformed JSON: ", e);
        } catch (Exception e) {
            logger.error("Unexpected error while processing message: ", e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Disconnected from Kraken WebSocket, session ID: {}, Reason: {}", session.getId(), closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error occurred: ", throwable);
    }

    private void subscribeToTicker() {
        JsonObject subscriptionMessage = new JsonObject();
        subscriptionMessage.addProperty("event", "subscribe");

        JsonObject subscriptionDetails = new JsonObject();
        subscriptionDetails.addProperty("name", "ticker");
        subscriptionMessage.add("subscription", subscriptionDetails);

        JsonArray pairsArray = new JsonArray();
        for (String pair : CRYPTO_PAIRS) {
            pairsArray.add(pair);
            logger.info("Successfully subscribed to pair: {}", pair);
        }
        subscriptionMessage.add("pair", pairsArray);

        session.getAsyncRemote().sendText(gson.toJson(subscriptionMessage));
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("wss://ws.kraken.com"));
        } catch (Exception e) {
            logger.error("Failed to connect to Kraken WebSocket: ", e);
        }
    }

    private String getCryptoName(String pair) {
        return switch (pair) {
            case "XBT/USD" -> "Bitcoin";
            case "ETH/USD" -> "Ethereum";
            case "USDT/USD" -> "Tether";
            case "USDC/USD" -> "USD Coin";
            case "LINK/USD" -> "Chainlink";
            case "LTC/USD" -> "Litecoin";
            case "XRP/USD" -> "XRP";
            case "ADA/USD" -> "Cardano";
            case "SOL/USD" -> "Solana";
            case "MATIC/USD" -> "Polygon";
            case "DOT/USD" -> "Polkadot";
            case "DAI/USD" -> "Dai";
            case "SHIB/USD" -> "Shiba Inu";
            case "TRX/USD" -> "TRON";
            case "AVAX/USD" -> "Avalanche";
            case "XLM/USD" -> "Stellar";
            case "BCH/USD" -> "Bitcoin Cash";
            case "FIL/USD" -> "Filecoin";
            case "AAVE/USD" -> "Aave";
            case "SUSHI/USD" -> "SushiSwap";
            default -> "Unknown";
        };
    }

    private String getCryptoSymbol(String pair) {
        return switch (pair) {
            case "XBT/USD" -> "BTC";
            case "ETH/USD" -> "ETH";
            case "USDT/USD" -> "USDT";
            case "USDC/USD" -> "USDC";
            case "LINK/USD" -> "LINK";
            case "LTC/USD" -> "LTC";
            case "XRP/USD" -> "XRP";
            case "ADA/USD" -> "ADA";
            case "SOL/USD" -> "SOL";
            case "MATIC/USD" -> "MATIC";
            case "DOT/USD" -> "DOT";
            case "DAI/USD" -> "DAI";
            case "SHIB/USD" -> "SHIB";
            case "TRX/USD" -> "TRX";
            case "AVAX/USD" -> "AVAX";
            case "XLM/USD" -> "XLM";
            case "BCH/USD" -> "BCH";
            case "FIL/USD" -> "FIL";
            case "AAVE/USD" -> "AAVE";
            case "SUSHI/USD" -> "SUSHI";
            default -> "UNK";
        };
    }

    public Map<String, CryptoPrice> getCryptoPrices() {
        return cryptoPrices;
    }
}
