package org.example.tpuserapi.service;

import org.example.tpuserapi.dto.UserDto;
import org.example.tpuserapi.entity.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(User user);
    UserDto updateUser(Long id, User user);
    void deleteUser(Long id);
}