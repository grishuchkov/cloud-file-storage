package ru.grishuchkov.cloudfilestorage.util.mapper;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
class ItemsToFileMetadataMapperTest {
    private final ItemsToFileMetadataMapper mapper = new ItemsToFileMetadataMapper();

    @Test
    void name() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = mapper.getClass().getDeclaredMethod("getExtension", String.class);
        method.setAccessible(true);

        String filename = "logo.dsd";

        String extension = (String) method.invoke(mapper, filename);
        assertEquals(".dsd", extension);
    }
}