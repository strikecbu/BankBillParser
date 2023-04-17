package com.andy.bankbillparser.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;

@Service
public class PdfReader {

    public Flux<String> read(File file, String password) {
        try (PDDocument document = PDDocument.load(file, password)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return Flux.fromArray(text.split("\\r?\\n"));
        } catch (IOException e) {
            return Flux.error(e);
        }
    }
}
