package com.BdayWall.bdaywall.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class MongoConfig  {
    AbstractMongoConfiguration abstractMongoConfiguration;

    @Bean
    public GridFsTemplate gridFsTemplate()throws Exception {
        return new GridFsTemplate(abstractMongoConfiguration.mongoDbFactory(),abstractMongoConfiguration.mappingMongoConverter());
    }

}

