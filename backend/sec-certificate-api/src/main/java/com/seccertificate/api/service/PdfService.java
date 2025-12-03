package com.seccertificate.api.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.repository.CertificateRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.commons.text.StringEscapeUtils; 

import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.file.*;
import java.util.Map;

@Service
public class PdfService {

    private final CertificateRepository repo;

    public PdfService(CertificateRepository repo) {
        this.repo = repo;
    }

    // @Async
    // public void generatePdfAsync(Certificate cert) {
    //     try {
    //         Path folder = Path.of("storage");
    //         Files.createDirectories(folder);
    //         Path file = folder.resolve("cert_" + cert.getId() + ".pdf");

    //         String html = cert.getTemplate().getHtmlTemplate();
    //         Map<String,String> data = new ObjectMapper()
    //                 .readValue(cert.getDataJson(), new TypeReference<>() {});

    //         // 1) Fill the template with data (This part is correct)
    //         String filledHtml = html;
    //         for (var e : data.entrySet()) {
    //             filledHtml = filledHtml.replace("{{" + e.getKey() + "}}", e.getValue());
    //         }

    //         // 2) Initialize PDF Document and Writer
    //         Document doc = new Document();
    //         PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file.toFile()));
    //         doc.open();
            
    //         // 3) Use HTMLWorker to parse the filled HTML/CSS and render it
    //         HTMLWorker htmlWorker = new HTMLWorker(doc);
    //         htmlWorker.parse(new StringReader(filledHtml)); // <-- Crucial step

    //         // 4) Add the verification hash separately (optional, but good practice)
    //         // doc.add(new Paragraph("Verification Hash: " + cert.getVerificationHash()));

    //         doc.close();

    //         // 5) Update Certificate status and path
    //         cert.setPdfPath(file.toString());
    //         cert.setStatus("GENERATED");
    //         repo.save(cert);

    //     } catch (Exception e) {
    //         throw new RuntimeException("PDF generation failed", e);
    //     }
    // }

    @Async
    public void generatePdfAsync(Certificate cert) {
        try {
            Path folder = Path.of("storage");
            Files.createDirectories(folder);
            Path file = folder.resolve("cert_" + cert.getId() + ".pdf");

            // 1) Fill placeholders safely
            String tpl = cert.getTemplate().getHtmlTemplate();
            Map<String,String> data = new ObjectMapper()
                .readValue(cert.getDataJson(), new TypeReference<>() {});
            String filled = tpl == null ? "" : tpl;
            for (var e : data.entrySet()) {
            // escape values so they can't break HTML
            String safe = StringEscapeUtils.escapeHtml4(e.getValue() == null ? "" : e.getValue());
            filled = filled.replace("{{" + e.getKey() + "}}", safe);
            }

            // 2) Ensure single well-formed document (strip BOM/whitespace before <html>)
            filled = filled.stripLeading();
            String htmlDoc =
                """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                </head>
                <body>
                """ + filled + """
                </body>
                </html>
                """;

            // 3) Render
            try (var os = new FileOutputStream(file.toFile())) {
            new PdfRendererBuilder()
                .useFastMode()
                .withHtmlContent(htmlDoc, null) // set base URL if you reference external images/fonts
                .toStream(os)
                .run();
            }

            cert.setPdfPath(file.toString());
            cert.setStatus("GENERATED");
            repo.save(cert);

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

}
