package org.jabref.logic.xmp;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentInformationExtractorTest {

    @Test
    public void extractBibtexEntryFromEmptyPDDocumentInformation() {
        // Arrange
        PDDocumentInformation pd = new PDDocumentInformation();
        DocumentInformationExtractor extractor = new DocumentInformationExtractor(pd);

        // Act
        Optional<BibEntry> extractedEntry = extractor.extractBibtexEntry();

        // Assert
        assertFalse(extractedEntry.isPresent());
        assertEquals(Optional.empty(), extractedEntry);
    }

    @Test
    public void extractBibtexEntryFromFilledPDDocumentInformation() {
        // Arrange
        PDDocumentInformation pd = new PDDocumentInformation();
        pd.setAuthor("J.R.R. Tolkien");
        pd.setTitle("The Hobbit");
        pd.setKeywords("Fantasy");
        pd.setSubject("A wizard asks a hobbit to join him on an adventure.");
        DocumentInformationExtractor extractor = new DocumentInformationExtractor(pd);

        // Act
        Optional<BibEntry> extractedEntry = extractor.extractBibtexEntry();

        // Assert
        assertTrue(extractedEntry.isPresent());
        assertEquals("J.R.R. Tolkien", extractedEntry.get().getField("author").get());
        assertEquals("The Hobbit", extractedEntry.get().getTitle().get());
        assertEquals("Fantasy", extractedEntry.get().getKeywords(',').get(0).get());
        assertEquals("A wizard asks a hobbit to join him on an adventure.",
                extractedEntry.get().getField("abstract").get());
    }
}
