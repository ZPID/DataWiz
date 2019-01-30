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

/**
 * Service class for SPSS MicroService Connection
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Service
public class SpssIoService {

    private static Logger log = LogManager.getLogger(SpssIoService.class);
    private final Environment env;

    @Autowired
    public SpssIoService(Environment env) {
        this.env = env;
    }


    /**
     * Sends a Record as JSON String to the SPSS MicroService and receives a .sav file as response.
     *
     * @param record {@link RecordDTO} Contains all Record Data
     * @return {@link SimpleEntry} With the {@link HttpStatus} and the .sav file as byte[] on success.
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
     * Sends a .sav file to the SPSS MicroService and receives a JSON string as response. It pareses the JSON String to a RecordDTO and returns it.
     *
     * @param file {@link String} File location
     * @return {@link SimpleEntry} With the {@link HttpStatus} and the record as RecordDTO on success.
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

    /**
     * Checks if the SPSS MicroService is available.
     *
     * @return True if MS is available, false if not.
     */
    public boolean checkAPIState() {
        ResponseEntity<String> response = new RestTemplate().getForEntity(env.getRequiredProperty("spss.api.url"), String.class);
        return response.getStatusCode().equals(HttpStatus.OK);
    }

}
