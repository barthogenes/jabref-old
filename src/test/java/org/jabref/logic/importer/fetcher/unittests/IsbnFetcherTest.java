package org.jabref.logic.importer.fetcher.unittests;

import org.jabref.logic.importer.FetcherException;
import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.importer.fetcher.IsbnFetcher;
import org.jabref.logic.importer.fetcher.IsbnViaChimboriFetcher;
import org.jabref.logic.importer.fetcher.IsbnViaEbookDeFetcher;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IsbnFetcherTest {

    private IsbnViaEbookDeFetcher ebookDeFetcher;
    private IsbnViaChimboriFetcher chimboriFetcher;

    @BeforeEach
    public void setUp() throws FetcherException {
        ebookDeFetcher = Mockito.mock(IsbnViaEbookDeFetcher.class);
        chimboriFetcher = Mockito.mock(IsbnViaChimboriFetcher.class);

        Mockito.when(ebookDeFetcher.performSearchById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(chimboriFetcher.performSearchById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
    }

    @Test
    public void performSearchByIdWithEmptyIdentifier() throws FetcherException {
        // Arrange
        IsbnFetcher isbnFetcher = new IsbnFetcher(Mockito.mock(ImportFormatPreferences.class),
                ebookDeFetcher, chimboriFetcher);

        // Act
        Optional<BibEntry> fetchedEntry = isbnFetcher.performSearchById("");

        // Assert
        assertEquals(Optional.empty(), fetchedEntry);
    }

    @Test
    public void performSearchByIdWithInvalidIdentifier() {
        // Arrange
        IsbnFetcher isbnFetcher = new IsbnFetcher(Mockito.mock(ImportFormatPreferences.class),
                ebookDeFetcher, chimboriFetcher);

        // Act, Assert
        assertThrows(FetcherException.class, () -> isbnFetcher.performSearchById("Some invalid identifier"));
    }

    @Test
    public void performSearchByIdEbookDeFailsAndChimboriFails() throws FetcherException {
        // Arrange
        IsbnFetcher isbnFetcher = new IsbnFetcher(Mockito.mock(ImportFormatPreferences.class),
                ebookDeFetcher, chimboriFetcher);

        // Act
        Optional<BibEntry> fetchedEntry = isbnFetcher.performSearchById("0123456789");

        // Assert
        assertEquals(Optional.empty(), fetchedEntry);
    }

    @Test
    public void performSearchByIdEbookDeFailsAndChimboriSucceeds() throws FetcherException {
        // Arrange
        BibEntry chimboriBibEntry = new BibEntry();
        chimboriBibEntry.setField("title", "I am fetched from chimbori.com!");
        Mockito.when(chimboriFetcher.performSearchById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(chimboriBibEntry));
        IsbnFetcher isbnFetcher = new IsbnFetcher(Mockito.mock(ImportFormatPreferences.class),
                ebookDeFetcher, chimboriFetcher);

        // Act
        Optional<BibEntry> fetchedEntry = isbnFetcher.performSearchById("0123456789");

        // Assert
        assertTrue(fetchedEntry.get().getField("title").isPresent());
        assertEquals("I am fetched from chimbori.com!", fetchedEntry.get().getField("title").get());
    }

    @Test
    public void performSearchByIdEbookDeSucceeds() throws FetcherException {
        // Arrange
        BibEntry ebookDeBibEntry = new BibEntry();
        ebookDeBibEntry.setField("title", "I am fetched from ebook.de!");
        Mockito.when(ebookDeFetcher.performSearchById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(ebookDeBibEntry));
        IsbnFetcher isbnFetcher = new IsbnFetcher(Mockito.mock(ImportFormatPreferences.class),
                ebookDeFetcher, chimboriFetcher);

        // Act
        Optional<BibEntry> fetchedEntry = isbnFetcher.performSearchById("0123456789");

        // Assert
        assertTrue(fetchedEntry.get().getField("title").isPresent());
        assertEquals("I am fetched from ebook.de!", fetchedEntry.get().getField("title").get());
    }
}
