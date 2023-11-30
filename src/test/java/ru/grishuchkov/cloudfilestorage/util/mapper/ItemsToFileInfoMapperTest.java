package ru.grishuchkov.cloudfilestorage.util.mapper;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemsToFileInfoMapperTest {
    private final ItemsToFileInfoMapper mapper = new ItemsToFileInfoMapper();

    @Test
    void getExtension() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = mapper.getClass().getDeclaredMethod("getExtension", String.class);
        method.setAccessible(true);

        String stringWithFilenameExtension = "logo.png";
        String stringWithFolderExtension = "hello/kitty/mum/";
        String stringWithNoneExtension = "hello/kitty/mum";

        String fileExtension = (String) method.invoke(mapper, stringWithFilenameExtension);
        String folderExtension = (String) method.invoke(mapper, stringWithFolderExtension);
        String noneExtension = (String) method.invoke(mapper, stringWithNoneExtension);

        System.out.println(fileExtension);
        assertEquals("png", fileExtension);
        System.out.println(folderExtension);
        assertEquals("folder", folderExtension);
        System.out.println(noneExtension);
        assertEquals("none", noneExtension);
    }


    @Test
    void toFile() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = mapper.getClass().getDeclaredMethod("getFilename", String.class);
        method.setAccessible(true);

        String difficultPath = "hello/tmp/java/logo.psd/";
        String simplePath = "logo.psd";
        String folderPath = "hello/kitty/logo/";

        String filenameFromDifficultPath = (String) method.invoke(mapper, difficultPath);
        String filenameFromSimplePath = (String) method.invoke(mapper, simplePath);
        String filenameFromFolderPath = (String) method.invoke(mapper, folderPath);

        System.out.println(filenameFromDifficultPath);
        System.out.println(filenameFromSimplePath);
        System.out.println(filenameFromFolderPath);

        assertEquals("logo", filenameFromDifficultPath);
        assertEquals("logo", filenameFromSimplePath);
        assertEquals("logo", filenameFromFolderPath);
    }
}