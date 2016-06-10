package de.zpid.datawiz.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Configuration
@EnableMongoRepositories
class MongoConfig extends AbstractMongoConfiguration {

  @Override
  @Bean(name = "mongo")
  public MongoClient mongo() {
    return new MongoClient("localhost", 27017);
  }

  @Override
  protected String getDatabaseName() {
    return "datawiz";
  }

  @Bean(name = "mongoDatabase")
  protected MongoDatabase mongoDatabase() {
    return mongo().getDatabase("datawiz");
  }

}
