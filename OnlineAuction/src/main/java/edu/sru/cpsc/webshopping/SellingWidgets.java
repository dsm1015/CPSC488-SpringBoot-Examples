package edu.sru.cpsc.webshopping;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;

import edu.sru.cpsc.webshopping.service.TaxExcelToDatabaseService;
import jakarta.servlet.ServletContext;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableAsync
public class SellingWidgets extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private TaxExcelToDatabaseService taxExcelToDatabaseService;

    @Value("${server.port}")
    private String port;

    @Autowired
    private ServletContext servletContext;

    public SellingWidgets() {
    }

    public static void main(String[] args) {
        SpringApplication.run(SellingWidgets.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        Resource excelResource = new ClassPathResource("StateTaxes.xlsx");
        try {
            taxExcelToDatabaseService.loadFromExcelFile(excelResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SellingWidgets.class);
    }

    @Override
    public void run(String... args) throws Exception {
        //get ip address
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("\nRunning on http://" + ip + ":" + port + servletContext.getContextPath() + "\n");
    }
}