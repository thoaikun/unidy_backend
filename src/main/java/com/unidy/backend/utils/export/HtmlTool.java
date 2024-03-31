package com.unidy.backend.utils.export;

import lombok.Getter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Date;

@Getter
public class HtmlTool implements ExportTool {
    String volunteerName;
    String organizationName;
    String campaignName;
    Date campaignDate;
    ByteArrayOutputStream outputStream;

    public HtmlTool(String volunteerName, String organizationName, String campaignName, Date campaignDate) {
        this.volunteerName = volunteerName;
        this.organizationName = organizationName;
        this.campaignName = campaignName;
        this.campaignDate = campaignDate;
    }

    @Override
    public void export() throws Exception {
        String html = parseThymeleafTemplate();
        this.outputStream = new ByteArrayOutputStream();
        this.outputStream.write(html.getBytes());
    }

    private String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("certificate/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("volunteerName", volunteerName);
        context.setVariable("campaignName", campaignName);
        context.setVariable("organizationName", organizationName);
        context.setVariable("campaignDate", campaignDate != null ? campaignDate : LocalDate.now());
        return templateEngine.process("certificate", context);
    }
}
