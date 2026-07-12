package com.interviewprep.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class RegisterRequest {
    @NotBlank @Size(min=2,max=100) private String fullName;
    @NotBlank @Size(min=3,max=50) @Pattern(regexp="^[a-zA-Z0-9_]+$") private String username;
    @NotBlank @Email private String email;
    @NotBlank @Size(min=8) private String password;
    private String college;
    private Integer graduationYear;
    private String targetRole;
}
