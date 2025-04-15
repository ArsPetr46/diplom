package com.sumdu.petrenko.diplom.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Getter
    @Schema(description = "Unique ID of a User",
            examples = {"1", "100", "3197"}, requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Getter
    @Setter
    @Schema(description = "Unique Nickname of a User", examples = {"Alex100", "7Joe7", "MyNickname"},
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String nickname;

    @Getter
    @Setter
    @Schema(description = "Unique Email of a User", example = "myemail@example.com", format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String email;
}
