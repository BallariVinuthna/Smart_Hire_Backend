package com.smarthire.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PdfTextExtractorService {

    public static class ExtractionResult {
        private final String text;
        private final int pageCount;
        private final boolean isScanned;
        private final String errorMessage;

        public ExtractionResult(String text, int pageCount, boolean isScanned, String errorMessage) {
            this.text = text;
            this.pageCount = pageCount;
            this.isScanned = isScanned;
            this.errorMessage = errorMessage;
        }

        public String getText() { return text; }
        public int getPageCount() { return pageCount; }
        public boolean isScanned() { return isScanned; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isSuccess() { return errorMessage == null && text != null && !text.trim().isEmpty(); }
    }

    public ExtractionResult extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ExtractionResult("", 0, false, "File is empty or not provided.");
        }

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (!filename.endsWith(".pdf")) {
            // For non-PDF text files (e.g. .txt, .md)
            try {
                String content = new String(file.getBytes(), StandardCharsets.UTF_8);
                return new ExtractionResult(content, 1, false, null);
            } catch (Exception e) {
                return new ExtractionResult("", 0, false, "Failed to read plain text file: " + e.getMessage());
            }
        }

        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            
            int pageCount = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            String extracted = stripper.getText(document);

            if (extracted == null || extracted.trim().isEmpty()) {
                return new ExtractionResult("", pageCount, true, 
                        "Scanned PDF detected: No selectable machine-readable text was found. OCR processing is required.");
            }

            // Clean up excessive whitespace while preserving paragraph structure
            String cleaned = extracted.replaceAll("\\r\\n", "\n").replaceAll("[ \\t]+", " ");
            return new ExtractionResult(cleaned, pageCount, false, null);

        } catch (Exception e) {
            return new ExtractionResult("", 0, false, "Corrupted or unreadable PDF: " + e.getMessage());
        }
    }
}
