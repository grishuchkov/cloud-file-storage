package ru.grishuchkov.cloudfilestorage.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilenameUtilsTest {

    private final FilenameUtils filenameUtils = new FilenameUtils();

    @Test
    void getExtension() {
        String stringWithFilenameExtension = "logo.png";
        String stringWithFolderExtension = "hello/kitty/mum/";
        String stringWithNoneExtension = "hello/kitty/mum";

        String fileExtension = filenameUtils.getExtension(stringWithFilenameExtension);
        String folderExtension = filenameUtils.getExtension(stringWithFolderExtension);
        String noneExtension = filenameUtils.getExtension(stringWithNoneExtension);

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

        String filenameFromDifficultPath = filenameUtils.getFilename(difficultPath);
        String filenameFromSimplePath = filenameUtils.getFilename(simplePath);
        String filenameFromFolderPath = filenameUtils.getFilename(folderPath);

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

        String folderExtension = filenameUtils.getFilenameWithoutServiceExtension(folder);
        String noneExtension = filenameUtils.getFilenameWithoutServiceExtension(none);
        String defaultExtension = filenameUtils.getFilenameWithoutServiceExtension(defaultPath);

        assertEquals("logo", folderExtension);
        assertEquals("logo", noneExtension);
        assertEquals("logo.psd", defaultExtension);
    }
}