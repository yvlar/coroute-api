package ca.ulaval.coroute.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import jakarta.inject.Inject;
import org.bson.UuidRepresentation;
import org.glassfish.hk2.api.Factory;

public class DatastoreFactory implements Factory<Datastore> {

  private final Datastore datastore;

  @Inject
  public DatastoreFactory(final MongoConfig mongoConfig) {
    final MongoClientSettings settings =
        MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(new ConnectionString(mongoConfig.getConnectionString()))
            .build();

    final MongoClient client = MongoClients.create(settings);
    this.datastore = Morphia.createDatastore(client, mongoConfig.getDatabaseName());
    this.datastore.getMapper().mapPackage("ca.ulaval.coroute.domain.model");
  }

  @Override
  public Datastore provide() {
    return this.datastore;
  }

  @Override
  public void dispose(final Datastore instance) { }
}
