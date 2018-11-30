package net.jgp.books.sparkInAction.ch10.lab210ReadRecordFromNetworkStream;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadRecordFromNetworkStreamApp {
  private static transient Logger log = LoggerFactory.getLogger(
      ReadRecordFromNetworkStreamApp.class);

  public static void main(String[] args) {
    ReadRecordFromNetworkStreamApp app =
        new ReadRecordFromNetworkStreamApp();
    app.start();
  }

  private void start() {
    log.debug("-> start()");

    SparkSession spark = SparkSession.builder()
        .appName("Read lines over a network stream")
        .master("local")
        .getOrCreate();

    Dataset<Row> df = spark
        .readStream()
        .format("socket")
        .option("host", "localhost")
        .option("port", 9999)
        .load();

    StreamingQuery query = df
        .writeStream()
        .outputMode(OutputMode.Update())
        .format("console")
        .start();

    try {
      query.awaitTermination(60000);
    } catch (StreamingQueryException e) {
      log.error(
          "Exception while waiting for query to end {}.",
          e.getMessage(),
          e);
    }

    // Executed only after a nice kill
    log.debug("Query status: {}", query.status());
    log.debug("<- start()");
  }
}