package ru.grishuchkov.cloudfilestorage.repository;

import io.minio.messages.Item;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import ru.grishuchkov.cloudfilestorage.TestBeans;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = {MinIORepository.class, TestBeans.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MinIORepositoryTest {

    @Autowired
    private MinIORepository minioRepository;

    private static final String TEST_BUCKET = "test-bucket";
    private static final String TEST_FILENAME = "test-file.txt";
    private static final String ROOT_PATH = "";

    @Test
    @BeforeAll
    public void createTestBucket() {
        minioRepository.makeBucketIfNotExists(TEST_BUCKET);
    }

    @Test
    @Order(value = 1)
    public void shouldDeleteTestFile() throws Exception {
        //Given
        MockMultipartFile file = getTestFile();
        minioRepository.save(List.of(file), TEST_BUCKET);
        List<String> filesInBucketBeforeDelete = getBucketFileFilenamesList(ROOT_PATH);

        //When
        minioRepository.delete(TEST_FILENAME, TEST_BUCKET);

        //Then
        List<String> filesInBucketAfterDelete = getBucketFileFilenamesList(ROOT_PATH);

        Assertions.assertThat(filesInBucketBeforeDelete).contains(TEST_FILENAME);
        Assertions.assertThat(filesInBucketAfterDelete.size()).isEqualTo(0);
    }

    @Test
    public void shouldSaveAndGetTestFile() throws Exception {
        //Given
        MockMultipartFile file = getTestFile();
        byte[] expectedFileBytes = file.getBytes();

        //When
        minioRepository.save(List.of(file), TEST_BUCKET);

        //Then
        byte[] actualFileBytes = minioRepository.get(TEST_FILENAME, TEST_BUCKET);
        Assertions.assertThat(actualFileBytes).isEqualTo(expectedFileBytes);
    }

    @Test
    public void shouldCopyTestFile() throws Exception {
        //Given
        MockMultipartFile file = getTestFile();
        minioRepository.save(List.of(file), TEST_BUCKET);
        String newAbsolutePath = "fix.txt";

        //When
        minioRepository.copy(TEST_FILENAME, newAbsolutePath, TEST_BUCKET);

        //Then
        List<String> files = getBucketFileFilenamesList(ROOT_PATH);

        Assertions.assertThat(files.size()).isEqualTo(2);
        Assertions.assertThat(files).contains(newAbsolutePath);
    }

    @NotNull
    private List<String> getBucketFileFilenamesList(String path) throws Exception {
        return minioRepository
                .getListObjects(path, TEST_BUCKET, false)
                .stream()
                .map(Item::objectName)
                .toList();
    }

    private static MockMultipartFile getTestFile() throws IOException {
        ClassPathResource fileForTesting = new ClassPathResource(TEST_FILENAME);
        return new MockMultipartFile("attachments", fileForTesting.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE, fileForTesting.getInputStream());
    }


}