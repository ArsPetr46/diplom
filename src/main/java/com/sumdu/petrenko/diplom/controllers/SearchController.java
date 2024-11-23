package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> searchUsersByNickname(@RequestParam String nickname) {
        List<UserDTO> users = searchService.searchUsersByNickname(nickname);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
