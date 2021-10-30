package com.jlbabilino.json;

import java.util.Arrays;

import com.jlbabilino.json.JSONEntry.JSONType;

@JSONSerializable(rootType = JSONType.ARRAY)
public class TempTest<A> {
    private A[] items;

    @DeserializedJSONConstructor
    public TempTest(@DeserializedJSONEntry A[] items) {
        this.items = items;
    }

    @SerializedJSONEntry
    public A[] getItems() {
        return items;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof TempTest<?>) {
            TempTest<?> tempTestObj = (TempTest<?>) obj;
            return Arrays.equals(tempTestObj.items, items);
        } else {
            return false;
        }
    } 

    public static void main(String[] args) {
        TempTest<String> tempTest = new TempTest<>(new String[]{"hi", "how", "are", "you"});
        JSON json = JSONSerializer.serializeJSON(tempTest);
        String jsonString = json.exportJSON(JSONFormatOption.ArrayNewlinePerItem.FALSE, JSONFormatOption.ArrayBeginOnNewline.FALSE);
        System.out.println(jsonString);
        if (jsonString.equals("[\"hi\", \"how\", \"are\", \"you\"]")) {
            System.out.println("SERIALIZATION SUCCESSFUL!!");
        } else {
            System.out.println("SERIALIZATION UNSUCCESSFUL :(");
        }

        TypeMarker<TempTest<String>> typeMarker = new TypeMarker<>() {};
        JSON parsedJSON = JSONParser.parseString(jsonString);
        try {
            TempTest<String> deserializedTempTest = JSONDeserializer.deserializeJSON(parsedJSON, typeMarker);
            if (tempTest.equals(deserializedTempTest)) {
                System.out.println("DESERIALIZATION SUCCESS!!");
            } else {
                System.out.println("DESERIALIZATION UNSUCCESSFUL :(");
            }
        } catch (JSONDeserializerException e) {
            System.out.println("DESERIALIZATION ERROR :(");
        }
    }
}
