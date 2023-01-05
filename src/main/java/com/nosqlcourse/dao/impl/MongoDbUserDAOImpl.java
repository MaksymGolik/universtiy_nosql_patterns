package com.nosqlcourse.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IUserDAO;
import com.nosqlcourse.dao.MongoDbDAOFactory;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoDbUserDAOImpl implements IUserDAO {
    private static String uri = DataSourceConfiguration.getMongoUri();
    private static MongoClient client = MongoClients.create(uri);

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Long.class, new MongoDbDAOFactory.LongGsonDeserializer()).create();

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for (Document document : mongoDatabase.getCollection("users").find()) {
                users.add(gson.fromJson(document.toJson(), User.class));
            }
        }
        return users;
    }

    @Override
    public List<User> getUsersByName(String name) {
        List<User> users = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("name", name);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for (Document document : mongoDatabase.getCollection("users").find(query)) {
                users.add(gson.fromJson(document.toJson(), User.class));
            }
        }
        return users;
    }

    @Override
    public User getUserByEmail(String email) throws DataNotFoundException {
        BasicDBObject query = new BasicDBObject("email", email);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            MongoCursor<Document> document = mongoDatabase.getCollection("users").find(query).iterator();
            if(document.hasNext()) return gson.fromJson(document.next().toJson(), User.class);
            else throw new DataNotFoundException("User with email " + email + " not found.");
        }
    }

    @Override
    public User getUserById(Long id) throws DataNotFoundException {
        BasicDBObject query = new BasicDBObject("_id", id);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            MongoCursor<Document> document = mongoDatabase.getCollection("users").find(query).iterator();
            if(document.hasNext()) return gson.fromJson(document.next().toJson(), User.class);
            else throw new DataNotFoundException("User with id " + id + " not found.");
        }
    }

    @Override
    public Long insertUser(User user) {
        Document query = new Document("_id", "userid");
        Document update = new Document("$inc", new Document("sequence_value", 1));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        options.upsert(true);

        Long id;
        Document document = new Document();
        document.put("email",user.getEmail());
        document.put("password", user.getPassword());
        document.put("name", user.getName());
        document.put("role", new Document("name",user.getRole().getName()));
        //try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = client.getDatabase("nosqlcourse");
            id = mongoDatabase.getCollection("counters").findOneAndUpdate(query, update, options).getLong("sequence_value");
            document.put("_id", id);
            mongoDatabase.getCollection("users").withWriteConcern(WriteConcern.UNACKNOWLEDGED).insertOne(document);
        //}
        return id;
    }


    @Override
    public boolean updateUser(User user) {
        Bson update = Updates.combine(Updates.set("email", user.getEmail()),
                Updates.set("password", user.getPassword()), Updates.set("name", user.getName()),
                Updates.set("role", user.getRole()));
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            UpdateResult updateResult = mongoDatabase.getCollection("users").updateOne(new BasicDBObject("_id", user.getId()),update);
            return updateResult.getModifiedCount()>0;
        }
    }
}