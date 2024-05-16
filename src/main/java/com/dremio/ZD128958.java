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
      final String sql = "SELECT * FROM Nation";
      Statement ps = conn.createStatement();

      String nsql = "UPDATE nation SET n_nationkey = 9996 WHERE n_name LIKE 'A%'";
      ResultSet results = ps.executeQuery(nsql);

      int rowCount = ps.getUpdateCount();
      System.out.println("incorrect rowcount " + rowCount);

      while (results.next()) {
        System.out.println("(correct) count of rows updated from the resultSet (should be 2) " + results.getInt(1));
      }

      // print rows to prove the update worked.
      ResultSet rs = ps.executeQuery(sql);
      while (rs.next()) {
        System.out.println(rs.getString(1) + " " + rs.getString(2));
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

  String buildAFURL() {
    String URL = "jdbc:arrow-flight-sql://localhost:32010/?useEncryption=%s";

    return URL;
  }

  String buildPostgresqlURL() {
    String URL = "jdbc:postgresql://localhost:5432/";

    return URL;
  }
}
