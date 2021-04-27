package com.example.sample.extra;

import com.example.sample.books.Books;
import com.example.sample.users.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

public class PatchObject<T> {
    private final T type;
    private final Class<T> classType;

    public PatchObject(T type) {
        this.classType = (Class<T>) type.getClass();
        if ((type instanceof Books) || (type instanceof Users)){
            this.type = type;
        }else {
            throw new IllegalArgumentException("The Object is not Books or Users object !!!");
        }
    }

    public T applyPatchObject(JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(this.type, JsonNode.class));
        return objectMapper.treeToValue(patched, classType);
    }
}
