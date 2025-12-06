package com.seccertificate.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.repository.CertificateRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class PdfService {

    private final CertificateRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public PdfService(CertificateRepository repo) {
        this.repo = repo;
    }

    public void generatePdf(Certificate cert) {
        try {
            System.out.println("=== PDF GENERATION STARTED ===");
            System.out.println("Certificate ID: " + cert.getId());

            Path folder = Path.of("storage");
            Files.createDirectories(folder);
            Path file = folder.resolve("cert_" + cert.getId() + ".pdf");

            System.out.println("Output file path: " + file.toAbsolutePath());

            // 1) Load template + values
            String template = cert.getTemplate().getHtmlTemplate();
            System.out.println("Template length: " + (template == null ? "NULL" : template.length()));

            Map<String, String> values = mapper.readValue(
                    cert.getDataJson(),
                    new TypeReference<>() {}
            );

            System.out.println("Data JSON: " + cert.getDataJson());
            System.out.println("Parsed Values: " + values);

            // 2) Fill placeholders safely
            String filled = template == null ? "" : template;
            for (var e : values.entrySet()) {
                String safe = StringEscapeUtils.escapeHtml4(
                        e.getValue() == null ? "" : e.getValue()
                );
                filled = filled.replace("{{" + e.getKey() + "}}", safe);
            }

            System.out.println("Filled HTML preview (first 500 chars):");
            System.out.println(filled.substring(0, Math.min(500, filled.length())));

            // 3) Decide: full document vs fragment
            String htmlDoc;
            String cleaned = filled.replace("\uFEFF", "").trim();
            String trimmed = cleaned.toLowerCase();

            if (trimmed.startsWith("<!doctype") || trimmed.startsWith("<html")) {
                System.out.println("Template is FULL DOCUMENT");
                htmlDoc = cleaned;
            } else {
                System.out.println("Template is FRAGMENT -> wrapping");
                htmlDoc =
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="utf-8" />
                            <meta name="viewport" content="width=device-width, initial-scale=1" />
                        </head>
                        <body>
                        """ + filled + """
                        </body>
                        </html>
                        """;
            }

            System.out.println("Final HTML (first 500 chars):");
            System.out.println(htmlDoc.substring(0, Math.min(500, htmlDoc.length())));

            // 4) Render to PDF
            System.out.println("Rendering PDF...");

            try (var out = new FileOutputStream(file.toFile())) {
                new PdfRendererBuilder()
                        .useFastMode()
                        .withHtmlContent(htmlDoc, null)
                        .toStream(out)
                        .run();
            }

            System.out.println("PDF rendered SUCCESSFULLY");

            // 5) Persist path + status
            cert.setPdfPath(file.toString());
            cert.setStatus("GENERATED");
            repo.save(cert);

            System.out.println("PDF path saved in DB");
            System.out.println("=== PDF GENERATION FINISHED ===");

        } catch (Exception e) {
            System.out.println("=== PDF GENERATION FAILED ===");
            e.printStackTrace();   // DO NOT remove yet
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}
