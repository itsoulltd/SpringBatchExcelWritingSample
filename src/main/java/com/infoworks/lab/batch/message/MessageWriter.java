package com.infoworks.lab.batch.message;

import com.infoworks.lab.domain.definition.ExcelItemWriter;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.ExcelWritingService;
import org.springframework.batch.core.JobExecution;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class MessageWriter implements ExcelItemWriter<Message> {

    private static Logger LOG = Logger.getLogger(MessageWriter.class.getSimpleName());
    private String exportPath;
    private ExcelWritingService service;
    private int batchSize = 10;
    private ContentWriter writer;
    private AtomicInteger progressCounter;

    public MessageWriter(String exportPath, int batchSize, ExcelWritingService service) {
        this.exportPath = exportPath;
        this.service = service;
        this.batchSize = batchSize;
    }

    @Override
    public Logger getLog() {
        return LOG;
    }

    @Override
    public String getOutputName() {
        return exportPath + "sample-excel-" + System.currentTimeMillis() + ".xlsx";
    }

    @Override
    public String[] getColumnHeaders() {
        return new String[]{"Key", "Payload", "Date"};
    }

    @Override
    public ContentWriter getWriter() {
        if (writer == null){
            this.progressCounter = new AtomicInteger(1);
            this.writer = createWriter();
        }
        return writer;
    }

    @Override
    public void afterJobCleanup(JobExecution jobExecution) {
        try {
            getWriter().close();
        } catch (Exception e) {}
        this.progressCounter = null;
        this.writer = null;
    }

    @Override
    public String getSheetName() {
        return "output";
    }

    @Override
    public Map<Integer, List<String>> convert(List<? extends Message> list) {
        //TODO:Test Dummy
        Map<Integer, List<String>> data = new HashMap<>();
        list.forEach(msg -> {
            String from = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            data.put(progressCounter.getAndIncrement(), Arrays.asList("message", msg.getPayload(), from));
        });
        return data;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public ExcelWritingService getService() {
        return service;
    }

}
