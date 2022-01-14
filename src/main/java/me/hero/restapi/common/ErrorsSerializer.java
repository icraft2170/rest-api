package me.hero.restapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        errors.getFieldErrors().stream().forEach(er ->{
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("field", er.getField());
                jsonGenerator.writeStringField("objectName", er.getObjectName());
                jsonGenerator.writeStringField("code", er.getCode());
                jsonGenerator.writeStringField("defaultMessage", er.getDefaultMessage());
                Object rejectedValue = er.getRejectedValue();
                if (rejectedValue != null) {
                    jsonGenerator.writeStringField("rejectedValue", er.getDefaultMessage());
                }
                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        errors.getGlobalErrors().forEach(er ->{
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", er.getObjectName());
                jsonGenerator.writeStringField("code", er.getCode());
                jsonGenerator.writeStringField("defaultMessage", er.getDefaultMessage());
                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        jsonGenerator.writeEndArray();
    }
}
