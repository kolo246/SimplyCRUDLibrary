package com.example.sample.extra;

import com.example.sample.books.Books;
import com.example.sample.users.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

public class PatchObject<T> {
    private final Class<T> type;

    public PatchObject(Class<T> type) {
        if ((type.isInstance(Books.class) || (type.isInstance(Users.class)))){
            this.type = type;
        }else {
            throw new IllegalArgumentException("The Object is not Books or Users object !!!");
        }
    }

    public T applyPatchObject(T type, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(type, JsonNode.class));
        return objectMapper.treeToValue(patched, this.type);
    }
}
