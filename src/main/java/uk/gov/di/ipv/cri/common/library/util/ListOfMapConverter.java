package uk.gov.di.ipv.cri.common.library.util;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;

public class ListOfMapConverter<T> implements AttributeConverter<List<T>> {

    final ClassToMapConverter<T> converter;

    public ListOfMapConverter() {
        converter = new ClassToMapConverter<>();
    }

    @Override
    public AttributeValue transformFrom(List<T> input) {
        List<AttributeValue> list = new ArrayList<>();
        for (Object object : input) {
            list.add(converter.transformFrom(object));
        }

        return AttributeValue.builder().l(list).build();
    }

    @Override
    public List<T> transformTo(AttributeValue input) {
        List<AttributeValue> list = input.l();
        List<T> objects = new ArrayList<>();
        for (AttributeValue attributeValue : list) {
            objects.add(converter.transformTo(attributeValue));
        }

        return objects;
    }

    @Override
    public EnhancedType<List<T>> type() {
        return EnhancedType.listOf(converter.type());
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }
}
