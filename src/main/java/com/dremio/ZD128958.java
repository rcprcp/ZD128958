package com.dremio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZD128958 {
  private static final Logger LOG = LogManager.getLogger(ZD128958.class);

  public static void main(String... args) {

    ZD128958 zd128958 = new ZD128958();

    zd128958.run();
  }

  void run() {
    // print the available JDBC Drivers.
    LOG.info("Registered JDBC Drivers:");
    try {
      Collections.list(DriverManager.getDrivers())
          .forEach(
              driver ->
                  LOG.info(
                      "Driver class {} (version {}.{})",
                      driver.getClass().getName(),
                      driver.getMajorVersion(),
                      driver.getMinorVersion()));
    } catch (Exception ex) {
      LOG.error("Exception: {}", ex.getMessage(), ex);
      System.exit(4);
    }

    String url = buildTestingURL();
    System.out.println("URL: " + url);

    try (Connection conn = DriverManager.getConnection(url)) {
      Statement ps = conn.createStatement();
      final String nsql = "UPDATE nation SET n_nationkey = 9996 WHERE n_name LIKE 'A%'";
      ResultSet results = ps.executeQuery(nsql);

      int rowCount = ps.getUpdateCount();
      System.out.println("incorrect rowcount " + rowCount);

      int countFromResultSet = 0;
      while (results.next()) {
        countFromResultSet = results.getInt(1);
        System.out.println("correct count  (should be 2) " + countFromResultSet);
      }

      // print rows to prove the update worked and generate INSERTs for Postgres test data
      System.out.println("CREATE TABLE nation (n_nationkey BIGINT, n_name VARCHAR(50)");
      final String sql = "SELECT * FROM Nation";
      ResultSet rs = ps.executeQuery(sql);
      while (rs.next()) {
        System.out.println(
            String.format(
                "INSERT INTO nations (n_nationkey, n_name) VALUES (%d, '%s')",
                rs.getInt(1), rs.getString(2)));
      }

    } catch (SQLException ex) {
      System.out.println("Exception: " + ex.getMessage());
      ex.printStackTrace();
      System.exit(2);
    }
    System.out.println("stop");
  }

  String buildTestingURL() {
    String URL =
        String.format(
            "jdbc:arrow-flight-sql://localhost:31337?useEncryption=true&user=flight_username&password=flight_password&disableCertificateVerification=true");

    return URL;
  }
}
