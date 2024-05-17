package org.example;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import java.util.List;
import java.util.Map;

public class DynamoDBHelper {
    private final Table table;

    public DynamoDBHelper(String tableName) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion(Regions.US_EAST_1)
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable(tableName);
    }

    public void writeData(Map<String, List<String>> dataMap) {
        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            String date = entry.getKey();
            List<String> sampleIds = entry.getValue();

            // Logging the item being written
            System.out.println("Writing item to DynamoDB: date=" + date + ", sampleIds=" + sampleIds);

            Item item = new Item()
                    .withPrimaryKey("date", date)
                    .withList("sampleIds", sampleIds);
            table.putItem(item);
        }
    }

    public List<String> getSampleIdsByDate(String date) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("date", date);
        Item outcome = table.getItem(spec);

        if (outcome != null) {
            return outcome.getList("sampleIds");
        } else {
            return null;
        }
    }

    public void deleteDataByDate(String date) {
        DeleteItemSpec spec = new DeleteItemSpec().withPrimaryKey("date", date);
        table.deleteItem(spec);
        System.out.println("Deleted item with date: " + date);
    }
}
