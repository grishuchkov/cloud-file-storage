package ru.grishuchkov.cloudfilestorage.util.mapper;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemsToFileMapperTest {
    private final ItemsToFileMapper mapper = new ItemsToFileMapper();

    @Test
    void getExtension() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = mapper.getClass().getDeclaredMethod("getExtension", String.class);
        method.setAccessible(true);

        String filename = "logo.png";

        String extension = (String) method.invoke(mapper, filename);
        assertEquals("png", extension);
    }


    @Test
    void toFile() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = mapper.getClass().getDeclaredMethod("getFilename", String.class);
        method.setAccessible(true);

        String difficultPath = "hello/tmp/java/logo.psd/";
        String simplePath = "logo.psd";

        String filenameFromDifficultPath = (String) method.invoke(mapper, difficultPath);
        String filenameFromSimplePath = (String) method.invoke(mapper, simplePath);

        System.out.println(filenameFromDifficultPath);
        System.out.println(filenameFromSimplePath);

        assertEquals("logo.psd", filenameFromDifficultPath);
        assertEquals("logo.psd", filenameFromSimplePath);
    }
}