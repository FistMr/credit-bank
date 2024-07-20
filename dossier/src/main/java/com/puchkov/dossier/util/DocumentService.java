package com.puchkov.dossier.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class DocumentService {

    public ByteArrayResource createDocument(UUID statementId) throws IOException{
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(statementId.toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);

            return new ByteArrayResource(out.toByteArray());
        }
    }
}
