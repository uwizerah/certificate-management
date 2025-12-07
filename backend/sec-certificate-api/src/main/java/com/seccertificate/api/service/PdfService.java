package com.seccertificate.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.repository.CertificateRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class PdfService {

    private final CertificateRepository repo;
    private final CertificateSignatureService signatureService;
    private final ObjectMapper mapper = new ObjectMapper();

    public PdfService(
            CertificateRepository repo,
            CertificateSignatureService signatureService
    ) {
        this.repo = repo;
        this.signatureService = signatureService;
    }

    public void generatePdf(Certificate cert) {
        try {
            System.out.println("=== PDF GENERATION STARTED ===");
            System.out.println("Certificate ID: " + cert.getId());

            Path folder = Path.of("storage");
            Files.createDirectories(folder);
            Path file = folder.resolve("cert_" + cert.getId() + ".pdf");

            // 1) Load template + values
            String template = cert.getTemplate().getHtmlTemplate();
            Map<String, String> values = mapper.readValue(
                    cert.getDataJson(),
                    new TypeReference<>() {}
            );

            // 2) Fill placeholders safely
            String filled = template == null ? "" : template;
            for (var e : values.entrySet()) {
                String safe = StringEscapeUtils.escapeHtml4(
                        e.getValue() == null ? "" : e.getValue()
                );
                filled = filled.replace("{{" + e.getKey() + "}}", safe);
            }

            //3) Add verification block (hash + URL)
            String verifyBlock =
                "<div style='margin-top:30px;font-size:10px;text-align:center;color:#555'>" +
                "Verification Code:<br/>" +
                "<b>" + cert.getVerificationHash() + "</b><br/><br/>" +
                "Verify at:<br/>" +
                "<a href='http://localhost:4200/dashboard/verify'>" +
                "http://localhost:4200/dashboard/verify" +
                "</a>" +
                "</div>";

            //INJECT INSIDE BODY (not after HTML!)
            if (filled.toLowerCase().contains("</body>")) {
                filled = filled.replace("</body>", verifyBlock + "</body>");
            } else {
                filled += verifyBlock;   // safe for fragment templates
            }

            // 4) Wrap if needed
            String htmlDoc;
            String cleaned = filled.replace("\uFEFF", "").trim();
            String trimmed = cleaned.toLowerCase();

            if (trimmed.startsWith("<!doctype") || trimmed.startsWith("<html")) {
                htmlDoc = cleaned;
            } else {
                htmlDoc = """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="utf-8" />
                        </head>
                        <body>
                        """ + filled + """
                        </body>
                        </html>
                        """;
            }

            // 5) Render to PDF
            try (var out = new FileOutputStream(file.toFile())) {
                new PdfRendererBuilder()
                        .useFastMode()
                        .withHtmlContent(htmlDoc, null)
                        .toStream(out)
                        .run();
            }

            // 6) Persist path + status
            cert.setPdfPath(file.toString());
            cert.setStatus("GENERATED");

            // 7) Cryptographically sign certificate
            signatureService.sign(cert);

            // 8) Save signed certificate
            repo.save(cert);

            System.out.println("PDF generated with verification hash embedded");

        } catch (Exception e) {
            System.out.println("PDF generation failed");
            e.printStackTrace();
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}
