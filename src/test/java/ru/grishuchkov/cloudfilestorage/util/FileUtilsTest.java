package ru.grishuchkov.cloudfilestorage.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUtilsTest {

    private final FileUtils fileUtils = new FileUtils();

    @Test
    void getExtension() {
        String stringWithFilenameExtension = "logo.png";
        String stringWithFolderExtension = "hello/kitty/mum/";
        String stringWithNoneExtension = "hello/kitty/mum";

        String fileExtension = fileUtils.getExtension(stringWithFilenameExtension);
        String folderExtension = fileUtils.getExtension(stringWithFolderExtension);
        String noneExtension = fileUtils.getExtension(stringWithNoneExtension);

        System.out.println(fileExtension);
        assertEquals("png", fileExtension);
        System.out.println(folderExtension);
        assertEquals("folder", folderExtension);
        System.out.println(noneExtension);
        assertEquals("none", noneExtension);
    }

    @Test
    void getFilename() {
        String difficultPath = "hello/tmp/java/logo.psd/";
        String simplePath = "logo.psd";
        String folderPath = "hello/kitty/logo/";

        String filenameFromDifficultPath = fileUtils.getFilename(difficultPath);
        String filenameFromSimplePath = fileUtils.getFilename(simplePath);
        String filenameFromFolderPath = fileUtils.getFilename(folderPath);

        System.out.println(filenameFromDifficultPath);
        System.out.println(filenameFromSimplePath);
        System.out.println(filenameFromFolderPath);

        assertEquals("logo", filenameFromDifficultPath);
        assertEquals("logo", filenameFromSimplePath);
        assertEquals("logo", filenameFromFolderPath);
    }

    @Test
    void getFilenameWithoutServiceExtension() {
        String folder = "logo.folder";
        String none = "logo.none";
        String defaultPath = "logo.psd";

        String folderExtension = fileUtils.getFilenameWithoutServiceExtension(folder);
        String noneExtension = fileUtils.getFilenameWithoutServiceExtension(none);
        String defaultExtension = fileUtils.getFilenameWithoutServiceExtension(defaultPath);

        assertEquals("logo", folderExtension);
        assertEquals("logo", noneExtension);
        assertEquals("logo.psd", defaultExtension);
    }
}