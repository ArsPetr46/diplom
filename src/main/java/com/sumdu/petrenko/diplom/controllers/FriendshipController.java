package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.services.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/{id}")
    public ResponseEntity<Friendship> getFriendshipById(@PathVariable Long id) {
        Optional<Friendship> friendship = friendshipService.getFriendshipById(id);
        return friendship.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> createFriendship(@RequestBody Friendship friendship) {
        friendshipService.saveFriendship(friendship);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long id) {
        friendshipService.deleteFriendship(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserDTO>> getFriendsOfUser(@PathVariable Long userId) {
        List<UserDTO> friends = friendshipService.getFriendsOfUser(userId);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }
}
