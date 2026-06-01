package com.royal.msvet.dto.request;

import lombok.Data;

@Data
public class VeterinarianRequestDTO {
    String id;
    String name;
    String specialty;
    String phoneNumber;
    String email;
    String password;
    String shelterId;
}
