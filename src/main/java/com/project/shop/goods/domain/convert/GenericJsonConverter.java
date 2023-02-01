package com.project.shop.goods.domain.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

import static com.project.shop.global.error.ErrorCode.FAIL_DESERIALIZE_JSON_INTO_OBJECT;
import static com.project.shop.global.error.ErrorCode.FAIL_SERIALIZE_OBJECT_INTO_JSON;

@Slf4j
@Converter
public class GenericJsonConverter<T> implements AttributeConverter<T, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try { // JSON 문자열로 변환
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("fail to serialize as object into Json : {}", attribute, e);
            throw new BusinessException(FAIL_SERIALIZE_OBJECT_INTO_JSON);
        }
    }

    @Override // JSON 문자에서 data 로 변환
    public T convertToEntityAttribute(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<T>() {});
        } catch (IOException e) {
            log.error("fail to deserialize as Json into Object : {}", jsonStr, e);
            throw new BusinessException(FAIL_DESERIALIZE_JSON_INTO_OBJECT);
        }
    }
}
