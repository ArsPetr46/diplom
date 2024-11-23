package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> searchUsersByNickname(String nickname) {
        return userRepository.findByNicknameContainingIgnoreCase(nickname).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getNickname(), user.getEmail());
    }

    public List<UserDTO> searchUsersByMultipleCriteria(String nickname, String email) {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> (nickname == null || user.getNickname().contains(nickname)) &&
                        (email == null || user.getEmail().contains(email)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}