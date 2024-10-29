package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.result.Result;
import com.andresv2.apirest.service.GcpSecretService;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("v1/secretManager")
public class GcpSecretController {

    @Autowired
    GcpSecretService secretService;

    @GetMapping("get/secret") // This Endpoint is only a test
    public ResponseEntity<Object> getSecret(@RequestBody HashMap<String, Object> data){
        if(!data.containsKey("secretName"))
            return ResponseEntity.badRequest().body("Please Check the Request, the secretName and secretData doesn't have to be Missing or null");

        Result<Secret> secretResult = secretService.getSecret((String) data.getOrDefault("projectId", null), (String) data.get("secretName"));

        return secretResult.isValid() ? ResponseEntity.ok(secretResult.getValue().toString()) : ResponseEntity.internalServerError().body(secretResult);
    }

    @GetMapping("get/secret/version") // This Endpoint is only a test
    public ResponseEntity<Object> getSecretVersion(@RequestBody HashMap<String, Object> data){
        if(!data.containsKey("secretName"))
            return ResponseEntity.badRequest().body("Please Check the Request, the secretName doesn't have to be Missing or null");

        Result<SecretVersion> secretResult = secretService.getSecretVersion((String) data.getOrDefault("projectId", null), (String) data.get("secretName"), (String) data.getOrDefault("secretVersion","1"));

        return secretResult.isValid() ? ResponseEntity.ok(secretResult) : ResponseEntity.internalServerError().body(secretResult);
    }

    @PostMapping("create/secret")
    public ResponseEntity<Object> createSecretOnSecretManager(@RequestBody HashMap<String, Object> data){
        if(!data.containsKey("secretName") || !data.containsKey("secretData"))
            return ResponseEntity.badRequest().body("Please Check the Request, the secretName and secretData doesn't have to be Missing or null");

        try {
            return secretService.createSecret(
                    (String) data.getOrDefault("projectId", null),
                    (String) data.get("secretName"),
                    (String) data.get("secretData")
            ) ? ResponseEntity.ok().body("The secret Has been Created Successfully") : ResponseEntity.internalServerError().body("The Secret could not be Created");

        }catch (Exception e){return ResponseEntity.internalServerError().body("An Error has been occurred, [" + e.getCause() + "]");}
    }

    @PostMapping("create/secret/version")
    public ResponseEntity<Object> createSecretVersionOnSecretManager(@RequestBody HashMap<String, Object> data){
        if(!data.containsKey("secretName") || !data.containsKey("secretData"))
            return ResponseEntity.badRequest().body("Please Check the Request, the secretName and secretData doesn't have to be Missing or null");

        try {
            Result<Secret> secretResult = secretService.getSecret((String) data.getOrDefault("projectId", null), (String) data.get("secretName"));

            if(!secretResult.isSuccessful())
                return ResponseEntity.internalServerError().body(secretResult);

            return secretService.createNewSecretVersion(
                    secretResult.getValue().get(),
                    (String) data.get("secretData")
            ) != null ? ResponseEntity.ok().body("The secret Has been Created Successfully") : ResponseEntity.internalServerError().body("The Secret could not be Created");

        }catch (Exception e){return ResponseEntity.internalServerError().body("An Error has been occurred, [" + e.getCause() + "]");}
    }
}