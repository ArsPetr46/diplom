package com.sumdu.petrenko.diplom.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) для користувача.
 * <p>
 * Цей клас використовується для передачі даних про користувача між різними шарами програми.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    /**
     * Унікальний ідентифікатор користувача.
     */
    @Getter
    @Schema(description = "Unique ID of a User",
            examples = {"1", "100", "3197"}, requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    /**
     * Унікальний нікнейм користувача.
     */
    @Getter
    @Setter
    @Schema(description = "Unique Nickname of a User", examples = {"Alex100", "7Joe7", "MyNickname"},
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String nickname;

    /**
     * Унікальний email користувача.
     */
    @Getter
    @Setter
    @Schema(description = "Unique Email of a User", example = "myemail@example.com", format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String email;
}
