package com.andy.bankbillparser.config;

import com.andy.bankbillparser.parser.TaiShinParser;
import com.andy.bankbillparser.pdf.PdfReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.io.File;

@Configuration
public class TestConfig {

    private final TaiShinParser taiShinParser;
    private final PdfReader pdfReader;

    public TestConfig(TaiShinParser taiShinParser, PdfReader pdfReader) {
        this.taiShinParser = taiShinParser;
        this.pdfReader = pdfReader;
    }

    @Bean
    public CommandLineRunner test() {
        return args -> {
            File file = new File("/Users/andy/Documents/project/bank-bill-parser/src/main/resources/TSB_Creditcard_Estatement_202304.pdf");
            Flux<String> resource = pdfReader.read(file, "");

            taiShinParser.parse(resource).subscribe();
        };
    }
}
