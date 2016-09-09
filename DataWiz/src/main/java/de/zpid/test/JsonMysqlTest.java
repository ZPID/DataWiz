package de.zpid.test;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JsonMysqlTest {
  
  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;

  private static Logger log = LogManager.getLogger(JsonMysqlTest.class);

  public static void main(String[] args) {
    Connection conn = null;
    JSONParser parser = new JSONParser();
    try {
      
      Object obj = parser.parse(new FileReader(
          "C:\\Users\\ronny\\OneDrive\\ZPID\\jsontest.txt"));
      
      System.out.println();
      
      
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/jsontest?" + "user=datawiz&password=dwpw1!");
      
      String json = obj.toString();
      
      String query = "INSERT INTO dw_dataset_matrix (jdoc) VALUES (?)";
      
      
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString (1, json);


      
      preparedStmt.execute();
      
      conn.close();

      // Do something with the Connection

    } catch (SQLException | IOException | ParseException ex) {
      // handle any errors
      log.warn("SQLException: ", () -> ex);
    }
  }

}
