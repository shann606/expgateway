package com.exp.gateway.dto;

import java.util.List;
import java.util.UUID;



public record UserDTO(UUID id,String username, String password, List<String> roles) {

}
