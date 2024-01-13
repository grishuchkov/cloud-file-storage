package ru.grishuchkov.cloudfilestorage.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class FileUtilsTest {

    private final FileUtils fileUtils = new FileUtils();

    @Test
    @DisplayName("Extract extension from path and correctly filename")
    void shouldExtractFileExtension() {
        String actualExtension = getFileExtension("hello/kitty/logo.png");

        String expectedExtension = "png";
        assertThat(actualExtension).isEqualTo(expectedExtension);
    }

    @Test
    @DisplayName("Return 'folder' extension from path")
    void shouldReturnFolderExtension() {
        String actualExtension = getFileExtension("hello/kitty/mum/");

        String expectedExtension = "folder";
        assertThat(actualExtension).isEqualTo(expectedExtension);
    }

    @Test
    @DisplayName("Return 'none' extension if is not folder")
    void shouldReturnNoneExtension() {
        String actualExtension = getFileExtension("hello/kitty/mum");

        String expectedExtension = "none";
        assertThat(actualExtension).isEqualTo(expectedExtension);
    }

    private String getFileExtension(String absolutePathWithFilename) {
        return fileUtils.getExtension(absolutePathWithFilename);
    }

    @Test
    @DisplayName("Extract filename without extension from path")
    void shouldExtractFilenameFromPath() {
        String actualFilename = getFilename("hello/tmp/java/logo.psd");

        String expectedFilename = "logo";
        assertThat(actualFilename).isEqualTo(expectedFilename);
    }

    @Test
    @DisplayName("Extract folder name from path")
    void shouldExtractFolderNameFromPath() {
        String actualFilename = getFilename("hello/tmp/java/123/kotlin/");

        String expectedFilename = "kotlin";
        assertThat(actualFilename).isEqualTo(expectedFilename);
    }

    @Test
    @DisplayName("Extract folder name from path without end slash")
    void shouldExtractFolderNameFromPathWithoutSlash() {
        String actualFilename = getFilename("hello/tmp/java/123/kotlin");

        String expectedFilename = "kotlin";
        assertThat(actualFilename).isEqualTo(expectedFilename);
    }

    private String getFilename(String difficultPath) {
        return fileUtils.getFilename(difficultPath);
    }

    @Test
    @DisplayName("Extract filename without 'folder' service extension")
    void shouldReturnFilenameWithoutFolderServiceExtension() {
        String actualFilename = getFilenameWithoutServiceExtension("kotlin.folder");

        String expectedFilename = "kotlin";
        assertThat(actualFilename).isEqualTo(expectedFilename);
    }

    @Test
    @DisplayName("Extract filename without 'none' service extension")
    void shouldReturnFilenameWithoutNoneServiceExtension() {
        String actualFilename = getFilenameWithoutServiceExtension("nothing.none");

        String expectedFilename = "nothing";
        assertThat(actualFilename).isEqualTo(expectedFilename);
    }

    @Test
    @DisplayName("Return filename with default extensions")
    void shouldReturnFilenameWithDefaultExtensions() {
        String actualFilename = getFilenameWithoutServiceExtension("trash.psd");

        String expectedFilename = "trash.psd";
        assertThat(actualFilename).isEqualTo(expectedFilename);
    }

    private String getFilenameWithoutServiceExtension(String filenameWithExtension) {
        return fileUtils.getFilenameWithoutServiceExtension(filenameWithExtension);
    }
}