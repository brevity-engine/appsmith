package com.external.plugins.commands;

import com.appsmith.external.models.ActionConfiguration;
import com.appsmith.external.models.DatasourceStructure;
import com.appsmith.external.models.Property;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.pf4j.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.external.plugins.MongoPluginUtils.generateMongoFormConfigTemplates;
import static com.external.plugins.MongoPluginUtils.parseSafely;
import static com.external.plugins.MongoPluginUtils.validConfigurationPresent;
import static com.external.plugins.constants.ConfigurationIndex.BSON;
import static com.external.plugins.constants.ConfigurationIndex.COLLECTION;
import static com.external.plugins.constants.ConfigurationIndex.COMMAND;
import static com.external.plugins.constants.ConfigurationIndex.INPUT_TYPE;
import static com.external.plugins.constants.ConfigurationIndex.UPDATE_MANY_QUERY;
import static com.external.plugins.constants.ConfigurationIndex.UPDATE_MANY_UPDATE;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMany extends MongoCommand {
    String query;
    String update;

    public UpdateMany(ActionConfiguration actionConfiguration) {
        super(actionConfiguration);

        List<Property> pluginSpecifiedTemplates = actionConfiguration.getPluginSpecifiedTemplates();

        if (validConfigurationPresent(pluginSpecifiedTemplates, UPDATE_MANY_QUERY)) {
            this.query = (String) pluginSpecifiedTemplates.get(UPDATE_MANY_QUERY).getValue();
        }

        if (validConfigurationPresent(pluginSpecifiedTemplates, UPDATE_MANY_UPDATE)) {
            this.update = (String) pluginSpecifiedTemplates.get(UPDATE_MANY_UPDATE).getValue();
        }
    }

    @Override
    public Boolean isValid() {
        if (super.isValid()) {
            if (!StringUtils.isNullOrEmpty(query) && !StringUtils.isNullOrEmpty(update)) {
                return Boolean.TRUE;
            } else {
                if (StringUtils.isNullOrEmpty(query)) {
                    fieldNamesWithNoConfiguration.add("Query");
                }
                if (StringUtils.isNullOrEmpty(update)) {
                    fieldNamesWithNoConfiguration.add("Update");
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Document parseCommand() {
        Document document = new Document();

        document.put("update", this.collection);

        Document queryDocument = parseSafely("Query", this.query);

        Document updateDocument = parseSafely("Update", this.update);

        Document update = new Document();
        update.put("q", queryDocument);
        update.put("u", updateDocument);

        // Set true to update ALL documents meeting the query criteria
        update.put("multi", Boolean.TRUE);

        List<Document> updates = new ArrayList<>();
        updates.add(update);

        document.put("updates", updates);

        return document;
    }

    @Override
    public List<DatasourceStructure.Template> generateTemplate(Map<String, Object> templateConfiguration) {
        String collectionName = (String) templateConfiguration.get("collectionName");
        String filterFieldName = (String) templateConfiguration.get("filterFieldName");

        Map<Integer, Object> configMap = new HashMap<>();

        configMap.put(BSON, Boolean.FALSE);
        configMap.put(INPUT_TYPE, "FORM");
        configMap.put(COMMAND, "UPDATE_MANY");
        configMap.put(COLLECTION, collectionName);
        configMap.put(UPDATE_MANY_QUERY, "{ \"_id\": ObjectId(\"id_of_document_to_update\") }");
        configMap.put(UPDATE_MANY_UPDATE, "{ \"$set\": { \"" + filterFieldName + "\": \"new value\" } }");

        List<Property> pluginSpecifiedTemplates = generateMongoFormConfigTemplates(configMap);

        String rawQuery = "{\n" +
                "  \"update\": \"" + collectionName + "\",\n" +
                "  \"updates\": [\n" +
                "    {\n" +
                "      \"q\": {\n" +
                "        \"_id\": ObjectId(\"id_of_document_to_update\")\n" +
                "      },\n" +
                "      \"u\": { \"$set\": { \"" + filterFieldName + "\": \"new value\" } }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        return Collections.singletonList(new DatasourceStructure.Template(
                "Update",
                rawQuery,
                pluginSpecifiedTemplates
        ));
    }
}
