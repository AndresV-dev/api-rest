package com.andresv2.apirest.controller;

import com.andresv2.apirest.service.GcpSecretService;
import com.google.cloud.secretmanager.v1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("v1/secretManager")
public class GcpSecretController {

    @Autowired
    GcpSecretService secretService;

    @PostMapping("create/secret")
    public ResponseEntity<Object> createSecretOnSecretManager(@RequestBody HashMap<String, Object> data){
        if(!data.containsKey("secretName") || !data.containsKey("secretData"))
            return ResponseEntity.badRequest().body("Please Check the Request, the requestName and secretData doesn't have to be Missing or null");

        try {
            return secretService.createSecret(
                    (String) data.getOrDefault("secretId", null),
                    (String) data.get("secretName"),
                    (String) data.get("secretData")
            ) ? ResponseEntity.ok().body("The secret Has been Created Successfully") : ResponseEntity.internalServerError().body("The Secret could not be Created");

        }catch (Exception e){ResponseEntity.internalServerError().body("An Error has been occurred, [" + e.getCause() + "]");}
        return ResponseEntity.internalServerError().body("The Secret could not be Created");
    }

    @PostMapping("update/secret")
    public ResponseEntity<Object> updateSecretOnSecretManager(@RequestBody HashMap<String, Object> data){
        if(!data.containsKey("secretName") || !data.containsKey("secretData"))
            return ResponseEntity.badRequest().body("Please Check the Request, the requestName and secretData doesn't have to be Missing or null");

        try {
            return secretService.updateSecret(
                    (String) data.getOrDefault("secretId", null),
                    (String) data.get("secretName"),
                    (String) data.get("secretData")
            ) ? ResponseEntity.ok().body("The secret Has been Updated Successfully") : ResponseEntity.internalServerError().body("The Secret could not be Updated");

        }catch (Exception e){ResponseEntity.internalServerError().body("An Error has been occurred, [" + e.getCause() + "]");}
        return ResponseEntity.internalServerError().body("The secret could not be created");
    }
}