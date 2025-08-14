package org.socialmedia.app.payload.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestPayload {

    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O formato do email é inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String email;

    @NotBlank(message = "A senha не pode estar em branco.")
    @Size(min = 3, max = 255, message = "A senha deve ter entre 3 e 255 caracteres.")
    private String password;

    @NotBlank(message = "A confirmação da senha não pode estar em branco.")
    private String passwordConfirmation;

    @NotBlank(message = "O primeiro nome не pode estar em branco.")
    @Size(max = 50, message = "O primeiro nome não pode exceder 50 caracteres.")
    private String firstName;

    @NotBlank(message = "O sobrenome não pode estar em branco.")
    @Size(max = 50, message = "O sobrenome não pode exceder 50 caracteres.")
    private String lastName;
}