package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import com.sumdu.petrenko.diplom.services.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friendrequests")
public class FriendRequestController {
    @Autowired
    private FriendRequestService friendRequestService;

    @GetMapping("/{id}")
    public ResponseEntity<FriendRequest> getFriendRequestById(@PathVariable Long id) {
        Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequestById(id);
        return friendRequest.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<FriendRequest>> getFriendRequestsBySenderId(@PathVariable Long senderId) {
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsBySenderId(senderId);
        return new ResponseEntity<>(friendRequests, HttpStatus.OK);
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<FriendRequest>> getFriendRequestsByReceiverId(@PathVariable Long receiverId) {
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsByReceiverId(receiverId);
        return new ResponseEntity<>(friendRequests, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createFriendRequest(@RequestBody FriendRequest friendRequest) {
        friendRequestService.saveFriendRequest(friendRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendRequest(@PathVariable Long id) {
        friendRequestService.deleteFriendRequest(id);
        return ResponseEntity.noContent().build();
    }
}
