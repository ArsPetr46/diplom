package com.sumdu.petrenko.diplom.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Getter
    private long id;
    @Getter
    @Setter
    private String nickname;
    @Getter
    @Setter
    private String email;
}
