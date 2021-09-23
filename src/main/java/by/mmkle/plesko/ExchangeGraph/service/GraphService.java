package by.mmkle.plesko.ExchangeGraph.service;

import by.mmkle.plesko.ExchangeGraph.dto.GraphTradeDto;
import com.google.gson.Gson;
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
import java.util.List;
import java.util.TimeZone;

@Service
public class GraphService {

    @PostConstruct
    public void init() throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("pair", "BTC_USD")
                .build();

        Request request = new Request.Builder()
                .url("https://api.exmo.com/v1.1/trades")
                .addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        List<GraphTradeDto> list = parseBody(body.string());

        String connUrl = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=admin";

        try (Connection conn = DriverManager.getConnection(connUrl)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(connUrl)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE graph (time TIMESTAMPTZ NOT NULL, val DECIMAL(19,2))");

            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

//        try (Connection conn = DriverManager.getConnection(connUrl);) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("SELECT create_hypertable('graph', 'time')");
//            }
//        } catch (SQLException ex) {
//            System.err.println(ex.getMessage());
//        }

        try (Connection conn = DriverManager.getConnection(connUrl)) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO graph (time, val) VALUES (?, ?)")) {
                for (GraphTradeDto rec : list) {
                    LocalDateTime time =
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(rec.getDate()),
                                    TimeZone.getDefault().toZoneId());

                    stmt.setTimestamp(1, Timestamp.valueOf(time));
                    stmt.setDouble(2, rec.getPrice().doubleValue());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(connUrl)) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet result = stmt.executeQuery("SELECT time_bucket('1 minutes', g.time) as bucket, min(g.val) as min, max(g.val) as max, first(g.val) as first, last (g.val) as last FROM graph g group by bucket order by bucket asc");
                while (result.next()) {
                    Long sec = result.getTime("bucket").getTime();
                    LocalDateTime time =
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(sec),
                                    TimeZone.getDefault().toZoneId());
                    Double min = result.getDouble("min");
                    Double max = result.getDouble("max");
                    System.out.println(time + " " + min + " " + max);
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }


    }

    private List<GraphTradeDto> parseBody(String json) {
        Type itemsListType = new TypeToken<List<GraphTradeDto>>() {
        }.getType();

        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        json = jsonObject.get("BTC_USD").toString();

        List<GraphTradeDto> list = new Gson().fromJson(json, itemsListType);

        return list;
    }
}
