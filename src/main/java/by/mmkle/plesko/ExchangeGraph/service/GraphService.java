package by.mmkle.plesko.ExchangeGraph.service;

import by.mmkle.plesko.ExchangeGraph.dto.GraphCandleDto;
import by.mmkle.plesko.ExchangeGraph.dto.GraphTradeResponseDto;
import by.mmkle.plesko.ExchangeGraph.enums.GraphTimeEnum;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class GraphService {

    public static final String SELECT_QUERY = "SELECT " +
            "time_bucket('%s', g.time) as bucket, " +
            "min(g.val) as min, " +
            "max(g.val) as max, " +
            "first(g.val, g.time) as first, " +
            "last (g.val, g.time) as last " +
            "FROM graph g " +
            "group by bucket " +
            "order by bucket asc";

    public static final String DEFAULT_CURRENCY = "BTC_USD";

    public static final String CONN_URL = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=admin";

    @PostConstruct
    public void init() throws IOException {

        List<GraphTradeResponseDto> list = createRequest(DEFAULT_CURRENCY);

        deleteTables();

        createTables();

        fillTable(list);
    }

    public List<GraphCandleDto> list(Integer graphTimeCode) {
        List<GraphCandleDto> candleList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(CONN_URL)) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet result = stmt.executeQuery(String.format(SELECT_QUERY, getValueByCode(graphTimeCode)));

                while (result.next()) {
                    Long sec = result.getTime("bucket").getTime();
                    LocalDateTime time =
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(sec),
                                    TimeZone.getDefault().toZoneId());

                    GraphCandleDto candleDto = GraphCandleDto.builder()
                            .time(time)
                            .max(result.getBigDecimal("max"))
                            .min(result.getBigDecimal("min"))
                            .first(result.getBigDecimal("first"))
                            .last(result.getBigDecimal("last"))
                            .build();

                    candleList.add(candleDto);
                }

            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return candleList;
    }

    private String getValueByCode(Integer graphTimeCode) {
        switch (graphTimeCode) {
            case 2:
                return GraphTimeEnum.MINUTES_5.getValue();
            case 3:
                return GraphTimeEnum.MINUTES_15.getValue();
            case 4:
                return GraphTimeEnum.HOUR_1.getValue();
            case 5:
                return GraphTimeEnum.HOUR_3.getValue();
            case 6:
                return GraphTimeEnum.HOUR_6.getValue();
            case 7:
                return GraphTimeEnum.HOUR_12.getValue();
            case 8:
                return GraphTimeEnum.DAY.getValue();
            default:
                return GraphTimeEnum.MINUTES_1.getValue();
        }
    }

    private List<GraphTradeResponseDto> createRequest(String currency) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api1.binance.com/api/v3/trades?symbol=BTCUSDT&limit=4000")
                .addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        return parseBody(currency, body.string());
    }

    private void fillTable(List<GraphTradeResponseDto> list) {
        try (Connection conn = DriverManager.getConnection(CONN_URL)) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO graph (time, val) VALUES (?, ?)")) {
                for (GraphTradeResponseDto rec : list) {
                    LocalDateTime time =
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(rec.getTime()),
                                    TimeZone.getDefault().toZoneId());

                    stmt.setTimestamp(1, Timestamp.valueOf(time));
                    stmt.setBigDecimal(2, rec.getPrice());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void createTables() {
        try (Connection conn = DriverManager.getConnection(CONN_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(CONN_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE graph (time TIMESTAMPTZ NOT NULL, val DECIMAL(19,9))");

            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(CONN_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT create_hypertable('graph', 'time')");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void deleteTables() {
        try (Connection conn = DriverManager.getConnection(CONN_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP TABLE graph;");

            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private List<GraphTradeResponseDto> parseBody(String currency, String json) {
        JsonArray jsonArray = new Gson().fromJson(json, JsonArray.class);

        List<GraphTradeResponseDto> list = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray){
            GraphTradeResponseDto dto = new Gson().fromJson(jsonElement.toString(), GraphTradeResponseDto.class);
            list.add(dto);
        }

        return list;
    }
}
