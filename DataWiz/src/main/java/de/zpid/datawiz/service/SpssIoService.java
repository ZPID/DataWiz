package de.zpid.datawiz.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.zpid.datawiz.dto.RecordDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap.SimpleEntry;

@Service
public class SpssIoService {

    private static Logger log = LogManager.getLogger(SpssIoService.class);
    private final Environment env;

    @Autowired
    public SpssIoService(Environment env) {
        this.env = env;
    }


    /**
     * TODO exception handling
     *
     * @param record
     * @return
     */
    SimpleEntry<HttpStatus, byte[]> fromJson(final RecordDTO record) {
        log.trace("Entering fromJson for record [id: {}; title: {}]", record::getId, record::getRecordName);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("file", new GsonBuilder().setPrettyPrinting().create().toJson(record));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartRequest, header);
        ResponseEntity<byte[]> response = new RestTemplate().exchange(env.getRequiredProperty("spss.api.url") + "fromjson", HttpMethod.POST, request, byte[].class);
        log.trace("Leaving fromJson for record [id: {}; title: {}] with HttpStatus [{}]", record::getId, record::getRecordName, response::getStatusCode);
        return new SimpleEntry<>(response.getStatusCode(), response.getBody());
    }

    /**
     * TODO exception handling
     *
     * @param file
     * @return
     */
    SimpleEntry<HttpStatus, RecordDTO> toJson(final String file) {
        log.trace("Entering toJson for file [path: {}]", () -> file);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartRequest, header);
        ResponseEntity<String> response = new RestTemplate().exchange(env.getRequiredProperty("spss.api.url") + "tojson", HttpMethod.POST, request, String.class);
        log.trace("Leaving toJson for file [path: {}] with HttpStatus [{}]", () -> file, response::getStatusCode);
        return new SimpleEntry<>(response.getStatusCode(), new Gson().fromJson(response.getBody(), new TypeToken<RecordDTO>() {
        }.getType()));
    }

    public boolean checkAPIState() {
        ResponseEntity<String> response = new RestTemplate().getForEntity(env.getRequiredProperty("spss.api.url"), String.class);
        return response.getStatusCode().equals(HttpStatus.OK);
    }

}
