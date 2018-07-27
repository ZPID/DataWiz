package de.zpid.datawiz.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.zpid.datawiz.dto.RecordDTO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SpssIoService {

    /**
     * TODO exception handling
     *
     * @param record
     * @return
     */
    public byte[] getSpssContentFromRest(RecordDTO record) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("file", new Gson().toJson(record));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartRequest, header);
        byte[] content = null;
        try {
            ResponseEntity<byte[]> response = new RestTemplate().exchange("http://localhost:8081/api/convertJSONToSPSS/", HttpMethod.POST, request, byte[].class);
            System.out.println(response.getBody());
            content = response.getBody();
        } catch (Exception e) {
            System.err.println(e);
        }
        // REST TEST
        return content;
    }

    /**
     * TODO exception handling
     *
     * @param file
     * @return
     */
    public RecordDTO getJSONFromSPSS(String file) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartRequest, header);
        RecordDTO content = null;
        try {
            ResponseEntity<String> response = new RestTemplate().exchange("http://localhost:8081/api/convertSPSSToJSON", HttpMethod.POST, request, String.class);
            System.out.println(response.getBody());
            content = new Gson().fromJson(response.getBody(), new TypeToken<RecordDTO>() {
            }.getType());
        } catch (Exception e) {
            System.err.println(e);
        }
        // REST TEST
        return content;
    }

}
