package com.exp.gateway.dto;

import java.util.List;



public record UserDTO(String username, String password, List<String> roles) {

}
